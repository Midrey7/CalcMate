package com.calcmate.allinonecalculator.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calcmate.allinonecalculator.ads.AdaptiveBannerAd
import com.calcmate.allinonecalculator.domain.ExpressionCalculator
import com.calcmate.allinonecalculator.domain.Numbers
import com.calcmate.allinonecalculator.ui.theme.LocalCalculatorPalette
import com.calcmate.allinonecalculator.ui.theme.LocalHapticsEnabled
import com.calcmate.allinonecalculator.ui.theme.LocalPrecision

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    isScientificInitially: Boolean = false,
    favorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCalculationComplete: (String, String) -> Unit,
    historyList: List<String>,
    onClearHistory: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val precision = LocalPrecision.current
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var livePreview by remember { mutableStateOf("") }
    var degrees by remember { mutableStateOf(true) }
    var shift by remember { mutableStateOf(false) }
    var hyp by remember { mutableStateOf(false) }
    var isScientific by remember { mutableStateOf(isScientificInitially) }
    var showHistorySheet by remember { mutableStateOf(false) }

    val engine = remember(degrees) { ExpressionCalculator(degrees) }

    // Real-time evaluation as expression changes
    LaunchedEffect(expression, degrees, precision) {
        if (expression.isBlank()) {
            livePreview = ""
        } else {
            engine.evaluate(expression).onSuccess {
                livePreview = Numbers.format(it, precision)
            }.onFailure {
                livePreview = ""
            }
        }
    }

    fun handleKey(key: String) {
        when (key) {
            "AC" -> {
                expression = ""
                result = ""
                livePreview = ""
            }
            "C", "⌫", "DEL" -> {
                if (result.isNotEmpty()) {
                    result = ""
                } else if (expression.isNotEmpty()) {
                    val funcs = listOf("asinh(", "acosh(", "atanh(", "sinh(", "cosh(", "tanh(", "asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "sqrt(", "cbrt(", "log2(", "log(", "ln(", "abs(")
                    val matchingFunc = funcs.firstOrNull { expression.endsWith(it) }
                    if (matchingFunc != null) {
                        expression = expression.dropLast(matchingFunc.length)
                    } else {
                        expression = expression.dropLast(1)
                    }
                }
            }
            "=" -> {
                if (expression.isNotEmpty()) {
                    engine.evaluate(expression).fold(
                        onSuccess = { res ->
                            val formatted = Numbers.format(res, precision)
                            result = formatted
                            onCalculationComplete(expression, formatted)
                        },
                        onFailure = {
                            result = "Invalid expression"
                        }
                    )
                }
            }
            "SHIFT" -> {
                shift = !shift
            }
            "HYP" -> {
                hyp = !hyp
            }
            "RAD/DEG", "DEG", "RAD" -> {
                degrees = !degrees
            }
            "+/-", "±" -> {
                if (expression.isNotEmpty() && !expression.startsWith("-")) {
                    expression = "-($expression)"
                } else if (expression.startsWith("-(") && expression.endsWith(")")) {
                    expression = expression.substring(2, expression.length - 1)
                } else if (expression.startsWith("-")) {
                    expression = expression.substring(1)
                } else {
                    expression = "-$expression"
                }
            }
            "()" -> {
                val openCount = expression.count { it == '(' }
                val closeCount = expression.count { it == ')' }
                expression += if (openCount > closeCount && expression.lastOrNull()?.isDigit() == true) ")" else "("
            }
            "(" -> expression += "("
            ")" -> expression += ")"
            "√" -> {
                if (shift) {
                    expression += "cbrt("
                    shift = false
                } else {
                    if (result.isNotEmpty()) {
                        expression = "sqrt($result)"
                        result = ""
                    } else {
                        expression += "sqrt("
                    }
                }
            }
            "x²" -> {
                if (shift) {
                    expression += "³"
                    shift = false
                } else {
                    expression += "²"
                }
            }
            "x³" -> expression += "³"
            "xʸ", "^" -> expression += "^"
            "1/x" -> {
                if (result.isNotEmpty()) {
                    expression = "1/($result)"
                    result = ""
                } else {
                    expression += "1/("
                }
            }
            "|x|" -> expression += "abs("
            "π" -> expression += "π"
            "e" -> expression += "e"
            "!" , "n!" -> expression += "!"
            "%" -> expression += "%"
            "sin" -> {
                val f = when {
                    shift && hyp -> "asinh("
                    hyp -> "sinh("
                    shift -> "asin("
                    else -> "sin("
                }
                expression += f
                if (shift) shift = false
            }
            "cos" -> {
                val f = when {
                    shift && hyp -> "acosh("
                    hyp -> "cosh("
                    shift -> "acos("
                    else -> "cos("
                }
                expression += f
                if (shift) shift = false
            }
            "tan" -> {
                val f = when {
                    shift && hyp -> "atanh("
                    hyp -> "tanh("
                    shift -> "atan("
                    else -> "tan("
                }
                expression += f
                if (shift) shift = false
            }
            "ln" -> {
                expression += if (shift) "exp(" else "ln("
                if (shift) shift = false
            }
            "log" -> {
                expression += if (shift) "10^(" else "log("
                if (shift) shift = false
            }
            else -> {
                val operators = listOf("+", "−", "×", "÷", "%", "^")
                if (result.isNotEmpty()) {
                    if (key in operators) {
                        expression = result + key
                    } else {
                        expression = key
                    }
                    result = ""
                } else if (key in operators) {
                    if (expression.isEmpty()) {
                        expression = "0$key"
                    } else if (expression.last().toString() in operators) {
                        expression = expression.dropLast(1) + key
                    } else {
                        expression += key
                    }
                } else if (key == ".") {
                    val lastToken = expression.split('+', '−', '×', '÷', '%', '^', '(', ')').lastOrNull() ?: ""
                    if (!lastToken.contains('.')) {
                        expression += if (lastToken.isEmpty()) "0." else "."
                    }
                } else {
                    expression += key
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF080C14))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (!isScientific) {
                // STANDARD CALCULATOR (Matches screenshot_2_calculator.jpg)
                StandardCalculatorContent(
                    expression = expression,
                    result = result,
                    livePreview = livePreview,
                    onBack = onBack,
                    onSwitchToScientific = { isScientific = true },
                    onOpenHistory = { showHistorySheet = true },
                    onKeyClick = { handleKey(it) },
                    modifier = Modifier.weight(1f)
                )
            } else {
                // SCIENTIFIC CALCULATOR (Matches screenshot_3_scientific.jpg)
                ScientificCalculatorContent(
                    expression = expression,
                    result = result,
                    livePreview = livePreview,
                    degrees = degrees,
                    shift = shift,
                    hyp = hyp,
                    onBack = onBack,
                    onSwitchToStandard = { isScientific = false },
                    onToggleDegrees = { degrees = !degrees },
                    onToggleShift = { shift = !shift },
                    onToggleHyp = { hyp = !hyp },
                    onOpenHistory = { showHistorySheet = true },
                    onKeyClick = { handleKey(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Adaptive Banner Ad at the bottom
            AdaptiveBannerAd(modifier = Modifier.padding(bottom = 2.dp))
        }

        // History Bottom Sheet
        if (showHistorySheet) {
            ModalBottomSheet(
                onDismissRequest = { showHistorySheet = false },
                containerColor = Color(0xFF0F1626),
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Calculation History",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF8FAFC)
                        )
                        if (historyList.isNotEmpty()) {
                            IconButton(onClick = onClearHistory) {
                                Icon(Icons.Default.DeleteSweep, "Clear History", tint = Color(0xFFFB7185))
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (historyList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No calculations yet", color = Color(0xFF64748B), fontSize = 15.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 380.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(historyList) { item ->
                                val parts = item.split(" = ")
                                Card(
                                    onClick = {
                                        if (parts.size == 2) {
                                            expression = parts[1]
                                            result = ""
                                            showHistorySheet = false
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131C2E))
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                                        if (parts.size == 2) {
                                            Text(parts[0], fontSize = 14.sp, color = Color(0xFF94A3B8))
                                            Spacer(Modifier.height(4.dp))
                                            Text("= ${parts[1]}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                                        } else {
                                            Text(item, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFFF8FAFC))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

/**
 * Standard Calculator Layout (matches screenshot_2_calculator.jpg)
 */
@Composable
private fun StandardCalculatorContent(
    expression: String,
    result: String,
    livePreview: String,
    onBack: (() -> Unit)?,
    onSwitchToScientific: () -> Unit,
    onOpenHistory: () -> Unit,
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Back Arrow
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Spacer(Modifier.size(40.dp))
            }

            // Center: Mode Switcher Tabs (SmartCalc / Scientific)
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SmartCalc",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF)
                )
                Text(
                    text = "Scientific",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B),
                    modifier = Modifier.clickable { onSwitchToScientific() }
                )
            }

            // Right: History Icon
            IconButton(
                onClick = onOpenHistory,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Display Card (Matches screenshot_2_calculator.jpg)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1626)),
            border = BorderStroke(1.dp, Color(0xFF1E2D44))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Formula / Expression display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .horizontalScroll(scrollState),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = if (expression.isEmpty()) "0" else expression,
                        fontSize = if (expression.length > 14) 28.sp else 34.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFF8FAFC),
                        textAlign = TextAlign.End,
                        maxLines = 2
                    )
                }

                // Evaluated result in glowing neon cyan
                val displayResult = when {
                    result.isNotEmpty() -> "= $result"
                    livePreview.isNotEmpty() -> "= $livePreview"
                    else -> ""
                }

                Text(
                    text = displayResult.ifEmpty { " " },
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Standard Keypad (4 columns x 5 rows matching screenshot_2_calculator.jpg)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.6f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: AC, %, √, ÷
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StandardCalcButton(text = "AC", textColor = Color(0xFF00E5FF), borderColor = Color(0xFF00E5FF).copy(alpha = 0.5f), isBordered = true, modifier = Modifier.weight(1f)) { onKeyClick("AC") }
                StandardCalcButton(text = "%", textColor = Color(0xFF00E5FF), modifier = Modifier.weight(1f)) { onKeyClick("%") }
                StandardCalcButton(text = "√", textColor = Color(0xFF00E5FF), modifier = Modifier.weight(1f)) { onKeyClick("√") }
                StandardCalcButton(text = "÷", textColor = Color(0xFF38BDF8), bgColor = Color(0xFF16233B), fontSize = 26.sp, modifier = Modifier.weight(1f)) { onKeyClick("÷") }
            }

            // Row 2: 7, 8, 9, ×
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StandardCalcButton(text = "7", textColor = Color(0xFF818CF8), modifier = Modifier.weight(1f)) { onKeyClick("7") }
                StandardCalcButton(text = "8", textColor = Color(0xFF818CF8), modifier = Modifier.weight(1f)) { onKeyClick("8") }
                StandardCalcButton(text = "9", textColor = Color(0xFF818CF8), modifier = Modifier.weight(1f)) { onKeyClick("9") }
                StandardCalcButton(text = "×", textColor = Color(0xFF38BDF8), bgColor = Color(0xFF16233B), fontSize = 26.sp, modifier = Modifier.weight(1f)) { onKeyClick("×") }
            }

            // Row 3: 4, 5, 6, −
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StandardCalcButton(text = "4", textColor = Color(0xFF2DD4BF), modifier = Modifier.weight(1f)) { onKeyClick("4") }
                StandardCalcButton(text = "5", textColor = Color(0xFF2DD4BF), modifier = Modifier.weight(1f)) { onKeyClick("5") }
                StandardCalcButton(text = "6", textColor = Color(0xFF2DD4BF), modifier = Modifier.weight(1f)) { onKeyClick("6") }
                StandardCalcButton(text = "−", textColor = Color(0xFF38BDF8), bgColor = Color(0xFF16233B), fontSize = 26.sp, modifier = Modifier.weight(1f)) { onKeyClick("−") }
            }

            // Row 4: 1, 2, 3, +
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StandardCalcButton(text = "1", textColor = Color(0xFF00E5FF), modifier = Modifier.weight(1f)) { onKeyClick("1") }
                StandardCalcButton(text = "2", textColor = Color(0xFF00E5FF), modifier = Modifier.weight(1f)) { onKeyClick("2") }
                StandardCalcButton(text = "3", textColor = Color(0xFF00E5FF), modifier = Modifier.weight(1f)) { onKeyClick("3") }
                StandardCalcButton(text = "+", textColor = Color(0xFF38BDF8), bgColor = Color(0xFF16233B), fontSize = 26.sp, modifier = Modifier.weight(1f)) { onKeyClick("+") }
            }

            // Row 5: 0, ., C, =
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StandardCalcButton(text = "0", textColor = Color(0xFF00E5FF), modifier = Modifier.weight(1f)) { onKeyClick("0") }
                StandardCalcButton(text = ".", textColor = Color(0xFF00E5FF), modifier = Modifier.weight(1f)) { onKeyClick(".") }
                StandardCalcButton(text = "C", textColor = Color(0xFFA78BFA), modifier = Modifier.weight(1f)) { onKeyClick("C") }
                StandardCalcButton(
                    text = "=",
                    textColor = Color.White,
                    bgBrush = Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFF6366F1))),
                    fontSize = 28.sp,
                    modifier = Modifier.weight(1f)
                ) { onKeyClick("=") }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

/**
 * Standard Calculator Key Button with squircle shape & tactile haptics
 */
@Composable
private fun StandardCalcButton(
    text: String,
    textColor: Color,
    bgColor: Color = Color(0xFF121A2A),
    bgBrush: Brush? = null,
    borderColor: Color = Color(0xFF1E2D44),
    isBordered: Boolean = false,
    fontSize: TextUnit = 24.sp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "btnScale"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(22.dp))
            .then(
                if (bgBrush != null) Modifier.background(bgBrush)
                else Modifier.background(bgColor)
            )
            .then(
                if (isBordered || borderColor != Color.Transparent) {
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(22.dp))
                } else Modifier
            )
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
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

/**
 * Scientific Calculator Layout (matches screenshot_3_scientific.jpg)
 */
@Composable
private fun ScientificCalculatorContent(
    expression: String,
    result: String,
    livePreview: String,
    degrees: Boolean,
    shift: Boolean,
    hyp: Boolean,
    onBack: (() -> Unit)?,
    onSwitchToStandard: () -> Unit,
    onToggleDegrees: () -> Unit,
    onToggleShift: () -> Unit,
    onToggleHyp: () -> Unit,
    onOpenHistory: () -> Unit,
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        // Top Bar (Left: Back arrow, Center: CalcMate Scientific, Right: History)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Spacer(Modifier.size(38.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CalcMate",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Standard",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.clickable { onSwitchToStandard() }
                    )
                    Text("•", fontSize = 11.sp, color = Color(0xFF334155))
                    Text(
                        text = "Scientific",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00E5FF)
                    )
                }
            }

            IconButton(
                onClick = onOpenHistory,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Display Card (Matches screenshot_3_scientific.jpg)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C101C)),
            border = BorderStroke(1.dp, Color(0xFF1E2D44))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Formula expression
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .horizontalScroll(scrollState),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = if (expression.isEmpty()) "0" else expression,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFE2E8F0),
                        textAlign = TextAlign.End,
                        maxLines = 2
                    )
                }

                // Large Result / Live Preview
                val displayResult = when {
                    result.isNotEmpty() -> result
                    livePreview.isNotEmpty() -> livePreview
                    else -> "0"
                }

                Text(
                    text = displayResult,
                    fontSize = if (displayResult.length > 10) 28.sp else 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC),
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // UPPER SCIENTIFIC KEYPAD MATRIX (6 columns x 5 rows)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Sci Row 1: +, −, x², ×, ÷, ⌫
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                SciKeyButton(text = "+", textColor = Color(0xFF818CF8), borderColor = Color(0xFF818CF8).copy(alpha = 0.4f), modifier = Modifier.weight(1f)) { onKeyClick("+") }
                SciKeyButton(text = "−", textColor = Color(0xFF818CF8), borderColor = Color(0xFF818CF8).copy(alpha = 0.4f), modifier = Modifier.weight(1f)) { onKeyClick("−") }
                SciKeyButton(text = if (shift) "x³" else "x²", textColor = Color(0xFF38BDF8), modifier = Modifier.weight(1f)) { onKeyClick("x²") }
                SciKeyButton(text = "×", textColor = Color(0xFF818CF8), borderColor = Color(0xFF818CF8).copy(alpha = 0.4f), modifier = Modifier.weight(1f)) { onKeyClick("×") }
                SciKeyButton(text = "÷", textColor = Color(0xFF818CF8), borderColor = Color(0xFF818CF8).copy(alpha = 0.4f), modifier = Modifier.weight(1f)) { onKeyClick("÷") }
                SciKeyButton(
                    text = "⌫",
                    textColor = Color(0xFFFB7185),
                    borderColor = Color(0xFFFB7185).copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                ) { onKeyClick("⌫") }
            }

            // Sci Row 2: sin, cos, tan, ln, log, π
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                val sinText = when {
                    shift && hyp -> "sinh⁻¹"
                    hyp -> "sinh"
                    shift -> "sin⁻¹"
                    else -> "sin"
                }
                val cosText = when {
                    shift && hyp -> "cosh⁻¹"
                    hyp -> "cosh"
                    shift -> "cos⁻¹"
                    else -> "cos"
                }
                val tanText = when {
                    shift && hyp -> "tanh⁻¹"
                    hyp -> "tanh"
                    shift -> "tan⁻¹"
                    else -> "tan"
                }
                SciKeyButton(text = sinText, textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("sin") }
                SciKeyButton(text = cosText, textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("cos") }
                SciKeyButton(text = tanText, textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("tan") }
                SciKeyButton(text = if (shift) "eˣ" else "ln", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("ln") }
                SciKeyButton(text = if (shift) "10ˣ" else "log", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("log") }
                SciKeyButton(text = "π", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("π") }
            }

            // Sci Row 3: 1/x, e, √, xʸ, ^, |x|
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                SciKeyButton(text = "1/x", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("1/x") }
                SciKeyButton(text = "e", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("e") }
                SciKeyButton(text = if (shift) "∛" else "√", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("√") }
                SciKeyButton(text = "xʸ", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("xʸ") }
                SciKeyButton(text = "^", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("^") }
                SciKeyButton(text = "|x|", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("|x|") }
            }

            // Sci Row 4: RAD/DEG, (, ), n!, %, +/-
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                SciKeyButton(
                    text = if (degrees) "DEG" else "RAD",
                    textColor = Color(0xFF00E5FF),
                    borderColor = Color(0xFF00E5FF).copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                ) { onToggleDegrees() }
                SciKeyButton(text = "(", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("(") }
                SciKeyButton(text = ")", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick(")") }
                SciKeyButton(text = "n!", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("n!") }
                SciKeyButton(text = "%", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("%") }
                SciKeyButton(text = "±", textColor = Color(0xFFE0F2FE), modifier = Modifier.weight(1f)) { onKeyClick("+/-") }
            }

            // Sci Row 5: SHIFT, HYP, RAD/DEG status, DEL
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                SciKeyButton(
                    text = "SHIFT",
                    textColor = if (shift) Color(0xFF080C14) else Color(0xFF00E5FF),
                    bgColor = if (shift) Color(0xFF00E5FF) else Color(0xFF101625),
                    borderColor = Color(0xFF00E5FF),
                    modifier = Modifier.weight(1.5f)
                ) { onToggleShift() }

                SciKeyButton(
                    text = "HYP",
                    textColor = if (hyp) Color(0xFF080C14) else Color(0xFFF59E0B),
                    bgColor = if (hyp) Color(0xFFF59E0B) else Color(0xFF101625),
                    borderColor = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1.5f)
                ) { onToggleHyp() }

                SciKeyButton(
                    text = if (degrees) "DEG" else "RAD",
                    textColor = Color(0xFF94A3B8),
                    borderColor = Color(0xFF334155),
                    modifier = Modifier.weight(1.5f)
                ) { onToggleDegrees() }

                SciKeyButton(
                    text = "DEL",
                    textColor = Color(0xFFFB7185),
                    borderColor = Color(0xFFFB7185).copy(alpha = 0.6f),
                    modifier = Modifier.weight(1.5f)
                ) { onKeyClick("DEL") }
            }
        }

        Spacer(Modifier.height(6.dp))

        // LOWER NUMERIC KEYPAD MATRIX (5 columns x 4 rows)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 6: 7, 8, 9, DEL, AC
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SciKeyButton(text = "7", textColor = Color(0xFFF8FAFC), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick("7") }
                SciKeyButton(text = "8", textColor = Color(0xFFF8FAFC), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick("8") }
                SciKeyButton(text = "9", textColor = Color(0xFFF8FAFC), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick("9") }
                SciKeyButton(text = "DEL", textColor = Color(0xFFFB7185), borderColor = Color(0xFFFB7185).copy(alpha = 0.5f), modifier = Modifier.weight(1f)) { onKeyClick("DEL") }
                SciKeyButton(text = "AC", textColor = Color(0xFFFB7185), borderColor = Color(0xFFFB7185).copy(alpha = 0.7f), modifier = Modifier.weight(1f)) { onKeyClick("AC") }
            }

            // Row 7: 4, 5, 6, ×, ÷
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SciKeyButton(text = "4", textColor = Color(0xFFF8FAFC), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick("4") }
                SciKeyButton(text = "5", textColor = Color(0xFFF8FAFC), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick("5") }
                SciKeyButton(text = "6", textColor = Color(0xFFF8FAFC), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick("6") }
                SciKeyButton(text = "×", textColor = Color(0xFF818CF8), borderColor = Color(0xFF818CF8).copy(alpha = 0.5f), modifier = Modifier.weight(1f)) { onKeyClick("×") }
                SciKeyButton(text = "÷", textColor = Color(0xFF818CF8), borderColor = Color(0xFF818CF8).copy(alpha = 0.5f), modifier = Modifier.weight(1f)) { onKeyClick("÷") }
            }

            // Row 8: 1, 2, 3, +, −
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SciKeyButton(text = "1", textColor = Color(0xFFF8FAFC), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick("1") }
                SciKeyButton(text = "2", textColor = Color(0xFFF8FAFC), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick("2") }
                SciKeyButton(text = "3", textColor = Color(0xFFF8FAFC), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick("3") }
                SciKeyButton(text = "+", textColor = Color(0xFF818CF8), borderColor = Color(0xFF818CF8).copy(alpha = 0.5f), modifier = Modifier.weight(1f)) { onKeyClick("+") }
                SciKeyButton(text = "−", textColor = Color(0xFF818CF8), borderColor = Color(0xFF818CF8).copy(alpha = 0.5f), modifier = Modifier.weight(1f)) { onKeyClick("−") }
            }

            // Row 9: 0, ., +/-, ±, =
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SciKeyButton(text = "0", textColor = Color(0xFFF8FAFC), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick("0") }
                SciKeyButton(text = ".", textColor = Color(0xFFF8FAFC), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick(".") }
                SciKeyButton(text = "+/-", textColor = Color(0xFFE0F2FE), borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f), modifier = Modifier.weight(1f)) { onKeyClick("+/-") }
                SciKeyButton(text = "()", textColor = Color(0xFF818CF8), borderColor = Color(0xFF818CF8).copy(alpha = 0.5f), modifier = Modifier.weight(1f)) { onKeyClick("()") }
                SciKeyButton(
                    text = "=",
                    textColor = Color.White,
                    bgColor = Color(0xFF00E5FF),
                    bgBrush = Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFF3B82F6))),
                    borderColor = Color(0xFF00E5FF),
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)
                ) { onKeyClick("=") }
            }
        }

        Spacer(Modifier.height(6.dp))
    }
}

/**
 * Scientific Calculator Key Button with cyberpunk dark glass styling
 */
@Composable
private fun SciKeyButton(
    text: String,
    textColor: Color,
    bgColor: Color = Color(0xFF101625),
    bgBrush: Brush? = null,
    borderColor: Color = Color(0xFF6366F1).copy(alpha = 0.35f),
    fontSize: TextUnit = 14.sp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "sciBtnScale"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(11.dp))
            .then(
                if (bgBrush != null) Modifier.background(bgBrush)
                else Modifier.background(bgColor)
            )
            .border(1.dp, borderColor, RoundedCornerShape(11.dp))
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
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            maxLines = 1
        )
    }
}
