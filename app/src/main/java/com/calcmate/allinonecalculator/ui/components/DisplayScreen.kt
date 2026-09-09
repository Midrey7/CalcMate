package com.calcmate.allinonecalculator.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calcmate.allinonecalculator.ui.theme.LocalCalculatorPalette

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayScreen(
    expression: String,
    result: String,
    livePreview: String,
    isDegrees: Boolean,
    isScientific: Boolean,
    onToggleDegrees: () -> Unit,
    onToggleScientific: () -> Unit,
    onOpenHistory: () -> Unit,
    onBackspace: () -> Unit,
    onClearAll: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val palette = LocalCalculatorPalette.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Auto-scroll to end when expression changes
    LaunchedEffect(expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    fun copyToClipboard(text: String) {
        if (text.isNotBlank() && text != "0" && text != "Invalid expression") {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("CalcMate Result", text))
            Toast.makeText(context, "Copied: $text", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Quick control pill bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = palette.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // DEG / RAD toggle
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.fnKeyBg)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                Surface(
                    onClick = onToggleDegrees,
                    color = if (isDegrees) palette.numKeyBg else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "DEG",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = if (isDegrees) FontWeight.Bold else FontWeight.Normal,
                        color = if (isDegrees) palette.opKeyText else palette.textTertiary
                    )
                }
                Surface(
                    onClick = onToggleDegrees,
                    color = if (!isDegrees) palette.numKeyBg else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "RAD",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = if (!isDegrees) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isDegrees) palette.opKeyText else palette.textTertiary
                    )
                }
            }
        }

            // Quick actions: Scientific, History, Backspace
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode badge (Sci / Std)
                Surface(
                    onClick = onToggleScientific,
                    color = if (isScientific) palette.opKeyBg else palette.fnKeyBg,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isScientific) "SCI" else "123",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isScientific) palette.opKeyText else palette.textSecondary
                    )
                }

                IconButton(
                    onClick = onOpenHistory,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = palette.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Combined clickable backspace (tap = 1 backspace, long click = clear all)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .combinedClickable(
                            onClick = onBackspace,
                            onLongClick = onClearAll
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace / Clear All",
                        tint = if (expression.isNotEmpty()) palette.clearKeyText else palette.textTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Expression display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (expression.isEmpty()) "0" else expression,
                fontSize = if (expression.length > 14) 26.sp else 34.sp,
                fontWeight = FontWeight.Medium,
                color = if (expression.isEmpty()) palette.textTertiary else palette.textSecondary,
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }

        Spacer(Modifier.height(6.dp))

        // Live preview & main result display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        val textToCopy = if (result.isNotEmpty()) result else livePreview
                        copyToClipboard(textToCopy)
                    }
                ),
            contentAlignment = Alignment.CenterEnd
        ) {
            Column(horizontalAlignment = Alignment.End) {
                // Live preview line if result not yet committed
                if (result.isEmpty() && livePreview.isNotEmpty() && livePreview != "Invalid expression" && livePreview != expression) {
                    Text(
                        text = "= $livePreview",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = palette.opKeyText.copy(alpha = 0.8f),
                        textAlign = TextAlign.End
                    )
                }

                // Final Result or committed value
                Text(
                    text = if (result.isNotEmpty()) result else (if (livePreview.isNotEmpty()) livePreview else "0"),
                    fontSize = when {
                        result.length > 12 -> 36.sp
                        result.length > 8 -> 44.sp
                        else -> 52.sp
                    },
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }
    }
}
