package com.veltrix.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.veltrix.ui.components.GlassCard
import com.veltrix.ui.components.VerticalLazyScrollbar
import com.veltrix.viewmodel.MainUiState
import kotlinx.coroutines.launch

@Composable
fun AnalyticsScreen(
    state: MainUiState,
    modifier: Modifier = Modifier
) {
    val roleCounts = remember(state.employees) {
        AnalyticsRoles.all.associateWith { role -> state.employees.count { it.role == role } }
    }
    val innerCircleColor = MaterialTheme.colorScheme.background.copy(alpha = 0.94f)
    val chartColors = listOf(
        Color(0xFF6366F1), Color(0xFF10B981), Color(0xFF38BDF8), Color(0xFFF59E0B),
        Color(0xFFF43F5E), Color(0xFF8B5CF6), Color(0xFF06B6D4), Color(0xFF84CC16)
    )
    val legendState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Analytics", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        GlassCard(
            modifier = Modifier.fillMaxWidth().height(280.dp),
            cornerRadius = 24.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val nonZero = roleCounts.filterValues { it > 0 }
                Canvas(modifier = Modifier.size(220.dp)) {
                    val strokeWidth = 42.dp.toPx()
                    var startAngle = -90f
                    val total = nonZero.values.sum().coerceAtLeast(1)
                    nonZero.entries.forEachIndexed { index, entry ->
                        val sweep = (entry.value / total.toFloat()) * 360f
                        drawArc(
                            color = chartColors[index % chartColors.size],
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        startAngle += sweep
                    }
                    drawCircle(
                        color = innerCircleColor,
                        radius = size.minDimension / 2.8f,
                        center = Offset(size.width / 2f, size.height / 2f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.employees.size.toString(), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                    Text("Employees", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.68f))
                }
            }
        }
        GlassCard(
            modifier = Modifier.fillMaxWidth().weight(1f),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Role Distribution", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                    Row {
                        IconButton(onClick = {
                            scope.launch {
                                legendState.animateScrollToItem((legendState.firstVisibleItemIndex - 1).coerceAtLeast(0))
                            }
                        }) {
                            Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Scroll up")
                        }
                        IconButton(onClick = {
                            scope.launch {
                                legendState.animateScrollToItem((legendState.firstVisibleItemIndex + 1).coerceAtMost((legendState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)))
                            }
                        }) {
                            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "Scroll down")
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = legendState,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(AnalyticsRoles.all) { role ->
                            val count = roleCounts[role] ?: 0
                            Row(
                                modifier = Modifier.fillMaxWidth().alpha(if (count == 0) 0.45f else 1f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Canvas(modifier = Modifier.size(12.dp)) {
                                        drawCircle(color = chartColors[AnalyticsRoles.all.indexOf(role) % chartColors.size])
                                    }
                                    Text(role, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                                }
                                Text(count.toString(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.76f))
                            }
                        }
                    }
                    VerticalLazyScrollbar(
                        state = legendState,
                        modifier = Modifier.align(Alignment.CenterEnd).padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

private object AnalyticsRoles {
    val all = listOf(
        "CEO", "COO", "CTO", "CFO", "CHRO", "Manager", "Project Manager", "Operations Manager",
        "HR Manager", "HR Executive", "Recruiter", "Developer", "Senior Developer", "Tech Lead",
        "QA Engineer", "DevOps Engineer", "UI Designer", "UX Researcher", "Product Manager",
        "Business Analyst", "Data Analyst", "Data Scientist", "Accountant", "Finance Executive",
        "Legal Advisor", "Marketing Lead", "Sales Executive", "Customer Success", "Support Agent",
        "Office Admin", "Security Officer", "Procurement Specialist", "Trainer", "Intern"
    )
}
