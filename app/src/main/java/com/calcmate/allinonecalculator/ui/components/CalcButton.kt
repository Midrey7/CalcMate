package com.calcmate.allinonecalculator.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calcmate.allinonecalculator.ui.theme.LocalCalculatorPalette
import com.calcmate.allinonecalculator.ui.theme.LocalHapticsEnabled

enum class KeyType {
    NUMBER,
    OPERATOR,
    FUNCTION,
    EQUALS,
    SCIENTIFIC,
    CLEAR
}

@Composable
fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    keyType: KeyType = KeyType.NUMBER,
    height: Dp = 64.dp,
    fontSize: TextUnit = 22.sp,
    onClick: () -> Unit
) {
    val palette = LocalCalculatorPalette.current
    val haptic = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "btnScale"
    )

    val (bgBrush, textColor) = when (keyType) {
        KeyType.EQUALS -> {
            palette.accentGradient to palette.equalsKeyText
        }
        KeyType.OPERATOR -> {
            Brush.linearGradient(listOf(palette.opKeyBg, palette.opKeyBg)) to palette.opKeyText
        }
        KeyType.FUNCTION -> {
            Brush.linearGradient(listOf(palette.fnKeyBg, palette.fnKeyBg)) to palette.fnKeyText
        }
        KeyType.CLEAR -> {
            Brush.linearGradient(listOf(palette.fnKeyBg, palette.fnKeyBg)) to palette.clearKeyText
        }
        KeyType.SCIENTIFIC -> {
            Brush.linearGradient(listOf(palette.fnKeyBg.copy(alpha = 0.85f), palette.fnKeyBg.copy(alpha = 0.85f))) to palette.textSecondary
        }
        KeyType.NUMBER -> {
            Brush.linearGradient(listOf(palette.numKeyBg, palette.numKeyBg)) to palette.numKeyText
        }
    }

    Box(
        modifier = modifier
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(20.dp))
            .background(brush = bgBrush)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (hapticsEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = if (keyType == KeyType.NUMBER || keyType == KeyType.EQUALS) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
