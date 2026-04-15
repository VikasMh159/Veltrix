package com.veltrix.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.veltrix.ui.theme.VeltrixThemeDefaults

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    content: @Composable () -> Unit
) {
    val colors = VeltrixThemeDefaults.colors
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        colors.glassHighlight,
                        colors.glass,
                        colors.glass.copy(alpha = 0.94f)
                    )
                )
            )
            .border(1.dp, colors.glassBorder, shape)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.015f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.14f)
                        )
                    )
                )
        )
        content()
    }
}

@Composable
fun VerticalLazyScrollbar(
    state: LazyListState,
    modifier: Modifier = Modifier
) {
    val layoutInfo = state.layoutInfo
    if (layoutInfo.totalItemsCount == 0) return
    val visibleItems = layoutInfo.visibleItemsInfo
    if (visibleItems.isEmpty()) return

    val alpha by animateFloatAsState(
        targetValue = if (state.isScrollInProgress) 1f else 0f,
        label = "scrollbar-alpha"
    )
    val colors = VeltrixThemeDefaults.colors

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .width(4.dp)
    ) {
        val totalItems = layoutInfo.totalItemsCount.toFloat()
        val visibleCount = visibleItems.size.toFloat()
        val viewportHeight = size.height
        val thumbHeight = (visibleCount / totalItems * viewportHeight).coerceAtLeast(36f)
        val maxTop = (viewportHeight - thumbHeight).coerceAtLeast(0f)
        val top = if (totalItems <= visibleCount) {
            0f
        } else {
            (visibleItems.first().index / (totalItems - visibleCount).coerceAtLeast(1f)) * maxTop
        }
        drawRoundRect(
            color = colors.scrollbar.copy(alpha = alpha),
            topLeft = Offset(0f, top),
            size = Size(size.width, thumbHeight),
            cornerRadius = CornerRadius(12f, 12f)
        )
    }
}

@Composable
fun GlowBar(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier.background(
            color = color.copy(alpha = 0.22f),
            shape = RoundedCornerShape(18.dp)
        )
    )
}
