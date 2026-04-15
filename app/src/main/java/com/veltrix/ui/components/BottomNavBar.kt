package com.veltrix.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.veltrix.AppTab
import com.veltrix.ui.theme.springDampingRatio
import com.veltrix.ui.theme.springStiffness

private val navSpring = spring<Float>(
    stiffness = springStiffness,
    dampingRatio = springDampingRatio
)

private val navDpSpring = spring<androidx.compose.ui.unit.Dp>(
    stiffness = springStiffness,
    dampingRatio = springDampingRatio
)

@Composable
fun BottomNavBar(
    tabs: List<AppTab>,
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)
    var containerWidth by remember { mutableFloatStateOf(0f) }
    val itemWidth = if (tabs.isEmpty()) 0.dp else (containerWidth / tabs.size).dp
    val indicatorWidth = 22.dp
    val indicatorOffset by animateDpAsState(
        targetValue = if (tabs.isEmpty()) 0.dp else {
            (itemWidth * selectedIndex) + ((itemWidth - indicatorWidth) / 2)
        },
        animationSpec = navDpSpring,
        label = "bottom-nav-indicator-offset"
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .blur(24.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White.copy(alpha = 0.10f))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.White.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.14f),
                            Color.White.copy(alpha = 0.04f)
                        )
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .onSizeChanged { containerWidth = it.width.toFloat() }
        ) {
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset - 8.dp, y = (-6).dp)
                    .align(Alignment.BottomStart)
                    .width(38.dp)
                    .height(14.dp)
                    .blur(16.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF6366F1).copy(alpha = 0.55f),
                                Color(0xFF8B5CF6).copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .align(Alignment.BottomStart)
                    .width(indicatorWidth)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF6366F1),
                                Color(0xFF8B5CF6),
                                Color(0xFF3B82F6)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tab ->
                    BottomNavItem(
                        tab = tab,
                        selected = index == selectedIndex,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(
    tab: AppTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember(tab) { MutableInteractionSource() }
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = navSpring,
        label = "bottom-nav-icon-scale"
    )
    val labelAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.58f,
        animationSpec = navSpring,
        label = "bottom-nav-label-alpha"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = navSpring,
        label = "bottom-nav-glow-alpha"
    )
    val highlightAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = navSpring,
        label = "bottom-nav-highlight-alpha"
    )

    val selectedBrush = Brush.linearGradient(
        listOf(
            Color(0xFF6366F1),
            Color(0xFF8B5CF6),
            Color(0xFF3B82F6)
        )
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .pressScale(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(42.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFF6366F1).copy(alpha = 0.22f * highlightAlpha),
                                Color(0xFF8B5CF6).copy(alpha = 0.16f * highlightAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .blur(20.dp)
                    .background(
                        brush = selectedBrush,
                        shape = CircleShape,
                        alpha = 0.85f * glowAlpha
                    )
            )
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(10.dp)
                    .offset(y = 10.dp)
                    .blur(12.dp)
                    .background(
                        brush = selectedBrush,
                        shape = RoundedCornerShape(999.dp),
                        alpha = 0.55f * glowAlpha
                    )
            )
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = if (selected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f),
                modifier = Modifier.scale(iconScale)
            )
        }

        Text(
            text = tab.label,
            style = if (selected) {
                TextStyle(
                    brush = selectedBrush,
                    fontSize = MaterialTheme.typography.labelLarge.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = labelAlpha),
                    fontWeight = FontWeight.Medium
                )
            }
        )

        Spacer(modifier = Modifier.height(2.dp))
    }
}
