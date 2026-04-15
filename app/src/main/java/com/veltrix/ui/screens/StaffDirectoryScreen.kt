package com.veltrix.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.veltrix.ui.components.EmployeeCard
import com.veltrix.ui.components.GlassCard
import com.veltrix.ui.components.VerticalLazyScrollbar
import com.veltrix.ui.components.pressScale
import com.veltrix.ui.components.rememberPressInteractionSource
import com.veltrix.viewmodel.Employee
import com.veltrix.viewmodel.MainUiState

@Composable
fun StaffDirectoryScreen(
    state: MainUiState,
    onSearchChange: (String) -> Unit,
    onAddEmployee: (Employee) -> Unit,
    onUpdateEmployee: (Employee) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingEmployee by remember { mutableStateOf<Employee?>(null) }
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val addInteraction = rememberPressInteractionSource()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Staff Directory", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            IconButton(
                onClick = {
                    editingEmployee = null
                    showSheet = true
                },
                interactionSource = addInteraction,
                modifier = Modifier.pressScale(addInteraction)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add employee")
            }
        }

        SearchField(
            value = state.searchQuery,
            onValueChange = onSearchChange,
            label = "Search by name or role"
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.filteredEmployees, key = { it.id }) { employee ->
                    EmployeeCard(
                        employee = employee,
                        onEditClick = {
                            editingEmployee = it
                            showSheet = true
                        },
                        onClick = {
                            editingEmployee = it
                            showSheet = true
                        }
                    )
                }
            }
            VerticalLazyScrollbar(
                state = listState,
                modifier = Modifier.align(Alignment.CenterEnd).padding(vertical = 8.dp)
            )
        }
    }

    if (showSheet) {
        EmployeeEditorSheet(
            employee = editingEmployee,
            onDismiss = { showSheet = false },
            onSave = { employee ->
                if (editingEmployee == null) onAddEmployee(employee) else onUpdateEmployee(employee)
                showSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeEditorSheet(
    employee: Employee?,
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by rememberSaveable(employee?.id) { mutableStateOf(employee?.name.orEmpty()) }
    var rating by rememberSaveable(employee?.id) { mutableStateOf(employee?.rating?.toString() ?: "4.5") }
    var roleSearch by rememberSaveable(employee?.id) { mutableStateOf(employee?.role ?: RolesCatalog.all.first()) }
    var selectedRole by rememberSaveable(employee?.id) { mutableStateOf(employee?.role ?: RolesCatalog.all.first()) }
    var expanded by rememberSaveable(employee?.id) { mutableStateOf(false) }
    val filteredRoles = RolesCatalog.all.filter { it.contains(roleSearch, ignoreCase = true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F172A).copy(alpha = 0.88f),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (employee == null) "Add Employee" else "Edit Employee",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            SearchField(name, { name = it }, "Name", darkField = true)
            SearchField(rating, { rating = it }, "Rating", darkField = true)

            Box {
                SearchField(
                    value = roleSearch,
                    onValueChange = {
                        roleSearch = it
                        expanded = true
                    },
                    label = "Role",
                    darkField = true,
                    trailing = {
                        Icon(Icons.Rounded.ArrowDropDown, contentDescription = null, tint = Color.White.copy(alpha = 0.82f))
                    },
                    modifier = Modifier.clickable { expanded = !expanded }
                )
                if (expanded) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
                        cornerRadius = 18.dp
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            items(filteredRoles.take(8)) { role ->
                                TextButton(
                                    onClick = {
                                        selectedRole = role
                                        roleSearch = role
                                        expanded = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = role, modifier = Modifier.fillMaxWidth(), color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    onSave(
                        Employee(
                            id = employee?.id ?: "emp-${System.currentTimeMillis()}",
                            name = name.ifBlank { "New Employee" },
                            role = selectedRole,
                            rating = rating.toFloatOrNull()?.coerceIn(0f, 5f) ?: 4.5f,
                            avatarUrl = employee?.avatarUrl.orEmpty()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (employee == null) "Create Employee" else "Update Employee")
            }
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    darkField: Boolean = false,
    trailing: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        trailingIcon = trailing,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = if (darkField) 0.06f else 0.04f),
            unfocusedContainerColor = Color.White.copy(alpha = if (darkField) 0.04f else 0.03f),
            focusedBorderColor = Color.White.copy(alpha = 0.18f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
            focusedTextColor = if (darkField) Color.White else MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = if (darkField) Color.White else MaterialTheme.colorScheme.onBackground,
            focusedLabelColor = if (darkField) Color.White else MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = if (darkField) Color.White.copy(alpha = 0.66f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        modifier = modifier.fillMaxWidth()
    )
}

private object RolesCatalog {
    val all = listOf(
        "CEO", "COO", "CTO", "CFO", "CHRO", "Manager", "Project Manager", "Operations Manager",
        "HR Manager", "HR Executive", "Recruiter", "Developer", "Senior Developer", "Tech Lead",
        "QA Engineer", "DevOps Engineer", "UI Designer", "UX Researcher", "Product Manager",
        "Business Analyst", "Data Analyst", "Data Scientist", "Accountant", "Finance Executive",
        "Legal Advisor", "Marketing Lead", "Sales Executive", "Customer Success", "Support Agent",
        "Office Admin", "Security Officer", "Procurement Specialist", "Trainer", "Intern"
    )
}
