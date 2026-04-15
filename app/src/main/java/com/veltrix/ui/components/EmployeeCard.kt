package com.veltrix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.veltrix.viewmodel.Employee

@Composable
fun EmployeeCard(
    employee: Employee,
    onEditClick: (Employee) -> Unit,
    onClick: (Employee) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val status = when {
        employee.rating >= 4.6f -> "Active"
        employee.rating >= 4.0f -> "Engaged"
        else -> "Review"
    }
    val rowInteraction = rememberPressInteractionSource()
    val editInteraction = rememberPressInteractionSource()

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(rowInteraction)
            .clickable(
                interactionSource = rowInteraction,
                indication = null
            ) { onClick(employee) },
        cornerRadius = 20.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF10B981)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = employee.name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString(""),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(employee.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                Text(employee.role, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatChip(status, Color(0xFF10B981))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFF6366F1).copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24)
                            )
                            Text(
                                text = "%.1f".format(employee.rating),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF6366F1),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            IconButton(
                onClick = { onEditClick(employee) },
                interactionSource = editInteraction,
                modifier = Modifier.pressScale(editInteraction)
            ) {
                Icon(Icons.Rounded.Edit, contentDescription = "Edit employee", tint = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
fun StatChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}
