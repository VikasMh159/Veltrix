package com.veltrix.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PendingActions
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veltrix.ui.components.GlassCard
import com.veltrix.ui.components.pressScale
import com.veltrix.ui.components.rememberPressInteractionSource
import com.veltrix.ui.theme.springDampingRatio
import com.veltrix.ui.theme.springStiffness
import com.veltrix.viewmodel.Employee
import com.veltrix.viewmodel.MainUiState
import kotlinx.coroutines.launch
import kotlin.math.max

private val monoFamily = FontFamily.Monospace

@Composable
fun DashboardScreen(
    state: MainUiState,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val performers by remember(state.filteredEmployees) {
        derivedStateOf { state.filteredEmployees.sortedByDescending { it.rating } }
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        DashboardHeader()
        DashboardMetaStrip()
        StatsRow(
            totalStaff = state.employees.size,
            onTimePercent = max(72, state.avgRating),
            pendingTasks = state.totalTasks
        )
        SearchBar(
            value = state.searchQuery,
            onValueChange = onSearchChange
        )
        TopPerformersCard(
            employees = performers,
            listState = listState,
            onScrollUp = {
                scope.launch {
                    listState.animateScrollBy(
                        value = -300f,
                        animationSpec = spring<Float>(
                            stiffness = springStiffness,
                            dampingRatio = springDampingRatio
                        )
                    )
                }
            },
            onScrollDown = {
                scope.launch {
                    listState.animateScrollBy(
                        value = 300f,
                        animationSpec = spring<Float>(
                            stiffness = springStiffness,
                            dampingRatio = springDampingRatio
                        )
                    )
                }
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DashboardHeader() {
    val bellInteraction = rememberPressInteractionSource()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            GlowCapsule(
                label = "Live workspace",
                icon = Icons.Rounded.Bolt,
                tint = Color(0xFF8B5CF6)
            )
            Text(
                text = "Welcome back",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 30.sp
            )
            Text(
                text = "Staff operations are running smoothly today.",
                color = Color.White.copy(alpha = 0.54f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassCard(
                modifier = Modifier
                    .size(52.dp)
                    .pressScale(bellInteraction)
                    .clickable(
                        interactionSource = bellInteraction,
                        indication = null
                    ) {},
                cornerRadius = 18.dp
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 9.dp, end = 9.dp)
                            .size(10.dp)
                            .background(Color(0xFFF43F5E), CircleShape)
                            .border(2.dp, Color(0xFF020617), CircleShape)
                    )
                }
            }
            AvatarCircle(
                name = "Olivia Rhye",
                size = 52.dp,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun DashboardMetaStrip() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetaCard(
            title = "Review Queue",
            value = "08",
            tint = Color(0xFF8B5CF6),
            modifier = Modifier.weight(1f)
        )
        MetaCard(
            title = "Avg Rating",
            value = "4.6",
            tint = Color(0xFF10B981),
            modifier = Modifier.weight(1f)
        )
        MetaCard(
            title = "Escalations",
            value = "02",
            tint = Color(0xFFF43F5E),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetaCard(
    title: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(84.dp),
        cornerRadius = 20.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            tint.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.50f),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = value,
                color = Color.White,
                fontFamily = monoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun StatsRow(
    totalStaff: Int,
    onTimePercent: Int,
    pendingTasks: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            title = "Total Staff",
            value = totalStaff.toString(),
            icon = Icons.Rounded.Person,
            iconTint = Color(0xFF6366F1),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "On Time",
            value = "$onTimePercent%",
            icon = Icons.Rounded.Schedule,
            iconTint = Color(0xFF10B981),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Pending",
            value = pendingTasks.toString(),
            icon = Icons.Rounded.PendingActions,
            iconTint = Color(0xFFF43F5E),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(138.dp),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            iconTint.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconTint.copy(alpha = 0.14f))
                    .border(1.dp, iconTint.copy(alpha = 0.18f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint)
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.56f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = value,
                    color = Color.White,
                    fontFamily = monoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.62f)
            )
        },
        placeholder = {
            Text(
                text = "Search staff by name or role",
                color = Color.White.copy(alpha = 0.45f)
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF0B1220).copy(alpha = 0.92f),
            unfocusedContainerColor = Color(0xFF0B1220).copy(alpha = 0.92f),
            focusedBorderColor = Color(0xFF6366F1).copy(alpha = 0.45f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFF6366F1)
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TopPerformersCard(
    employees: List<Employee>,
    listState: LazyListState,
    onScrollUp: () -> Unit,
    onScrollDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    GlowCapsule(
                        label = "Performance leaderboard",
                        icon = Icons.Rounded.Star,
                        tint = Color(0xFFFBBF24)
                    )
                    Text(
                        text = "Top Performers",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${employees.size} employees ranked by live rating",
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ScrollArrowButton(
                        icon = Icons.Rounded.KeyboardArrowUp,
                        onClick = onScrollUp,
                        contentDescription = "Scroll up"
                    )
                    ScrollArrowButton(
                        icon = Icons.Rounded.KeyboardArrowDown,
                        onClick = onScrollDown,
                        contentDescription = "Scroll down"
                    )
                }
            }

            TableHeader()

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(employees, key = { _, employee -> employee.id }) { index, employee ->
                        PerformerRow(index = index + 1, employee = employee)
                    }
                }
                IndigoScrollbar(
                    state = listState,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(6.dp)
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun GlowCapsule(
    label: String,
    icon: ImageVector,
    tint: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(tint.copy(alpha = 0.14f))
            .border(1.dp, tint.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.82f),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ScrollArrowButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String
) {
    val interaction = rememberPressInteractionSource()

    GlassCard(
        modifier = Modifier
            .size(44.dp)
            .pressScale(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        cornerRadius = 16.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription, tint = Color.White)
        }
    }
}

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("Sr. No", modifier = Modifier.width(64.dp))
        HeaderCell("Name", modifier = Modifier.weight(1f))
        HeaderCell("Rating", modifier = Modifier.width(96.dp), alignEnd = true)
    }
}

@Composable
private fun HeaderCell(
    label: String,
    modifier: Modifier,
    alignEnd: Boolean = false
) {
    Box(modifier = modifier) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.42f),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(if (alignEnd) Alignment.CenterEnd else Alignment.CenterStart)
        )
    }
}

@Composable
private fun PerformerRow(
    index: Int,
    employee: Employee
) {
    val rowInteraction = rememberPressInteractionSource()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.035f),
                        Color.Transparent
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(18.dp))
            .pressScale(rowInteraction)
            .clickable(
                interactionSource = rowInteraction,
                indication = null
            ) {}
            .padding(horizontal = 12.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "%02d".format(index),
            color = Color.White.copy(alpha = 0.78f),
            modifier = Modifier.width(64.dp),
            fontFamily = monoFamily,
            fontWeight = FontWeight.Medium
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarCircle(name = employee.name, size = 42.dp)
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = employee.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = employee.role,
                    color = Color.White.copy(alpha = 0.48f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(
            modifier = Modifier.width(96.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "%.1f".format(employee.rating),
                color = Color.White,
                fontFamily = monoFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AvatarCircle(
    name: String,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF6366F1),
                        Color(0xFF10B981)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString(""),
            color = Color.White,
            fontFamily = monoFamily,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun IndigoScrollbar(
    state: LazyListState,
    modifier: Modifier = Modifier
) {
    val layoutInfo = state.layoutInfo
    val visibleItems = layoutInfo.visibleItemsInfo
    if (layoutInfo.totalItemsCount == 0 || visibleItems.isEmpty()) return

    val isActive = state.isScrollInProgress
    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = spring(
            stiffness = springStiffness,
            dampingRatio = springDampingRatio
        ),
        label = "dashboard-scrollbar-alpha"
    )

    AnimatedVisibility(visible = alpha > 0.01f, modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalItems = layoutInfo.totalItemsCount.toFloat()
            val visibleCount = visibleItems.size.toFloat()
            val viewportHeight = size.height
            val thumbHeight = (visibleCount / totalItems * viewportHeight).coerceAtLeast(42f)
            val maxTop = (viewportHeight - thumbHeight).coerceAtLeast(0f)
            val top = if (totalItems <= visibleCount) {
                0f
            } else {
                (visibleItems.first().index / (totalItems - visibleCount).coerceAtLeast(1f)) * maxTop
            }

            drawRoundRect(
                color = Color(0xFF6366F1).copy(alpha = 0.14f * alpha),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, viewportHeight),
                cornerRadius = CornerRadius(999f, 999f)
            )
            drawRoundRect(
                color = Color(0xFF6366F1).copy(alpha = alpha),
                topLeft = Offset(0f, top),
                size = Size(size.width, thumbHeight),
                cornerRadius = CornerRadius(999f, 999f)
            )
        }
    }
}
