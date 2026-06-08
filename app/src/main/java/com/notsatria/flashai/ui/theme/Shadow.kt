package com.notsatria.flashai.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.flashShadow(
    color: Color = Color(0x1A6366F1),
    borderRadius: Dp = 16.dp,
    blurRadius: Dp = 12.dp,
): Modifier = shadow(
    elevation = blurRadius,
    shape = RoundedCornerShape(borderRadius),
    ambientColor = color,
    spotColor = color,
)
