package com.calcmate.allinonecalculator.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calcmate.allinonecalculator.ads.AdaptiveBannerAd
import com.calcmate.allinonecalculator.domain.ExpressionCalculator
import com.calcmate.allinonecalculator.domain.Numbers
import com.calcmate.allinonecalculator.ui.components.CalcButton
import com.calcmate.allinonecalculator.ui.components.DisplayScreen
import com.calcmate.allinonecalculator.ui.components.KeyType
import com.calcmate.allinonecalculator.ui.theme.LocalCalculatorPalette
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
    val palette = LocalCalculatorPalette.current
    val precision = LocalPrecision.current
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var livePreview by remember { mutableStateOf("") }
    var degrees by remember { mutableStateOf(true) }
    var inverse by remember { mutableStateOf(false) }
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
            "⌫" -> {
                if (result.isNotEmpty()) {
                    result = ""
                } else if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
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
            "INV" -> {
                inverse = !inverse
            }
            "+/-" -> {
                if (expression.isNotEmpty() && !expression.startsWith("-")) {
                    expression = "-($expression)"
                } else if (expression.startsWith("-(") && expression.endsWith(")")) {
                    expression = expression.substring(2, expression.length - 1)
                } else {
                    expression = "-$expression"
                }
            }
            "()" -> {
                val openCount = expression.count { it == '(' }
                val closeCount = expression.count { it == ')' }
                expression += if (openCount > closeCount && expression.lastOrNull()?.isDigit() == true) ")" else "("
            }
            "√" -> {
                if (result.isNotEmpty()) {
                    expression = "sqrt($result)"
                    result = ""
                } else {
                    expression += "sqrt("
                }
            }
            "x²" -> expression += "^2"
            "xʸ" -> expression += "^"
            "1/x" -> {
                if (result.isNotEmpty()) {
                    expression = "1/($result)"
                    result = ""
                } else {
                    expression = "1/($expression)"
                }
            }
            "π" -> expression += "pi"
            "e" -> expression += "e"
            "sin" -> expression += if (inverse) "asin(" else "sin("
            "cos" -> expression += if (inverse) "acos(" else "cos("
            "tan" -> expression += if (inverse) "atan(" else "tan("
            "ln" -> expression += "ln("
            "log" -> expression += "log("
            else -> {
                val operators = listOf("+", "−", "×", "÷", "%", "^")
                if (result.isNotEmpty()) {
                    // Start new calculation or chain with operator
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
                        // Replace trailing operator with new operator
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardGradient)
    ) {
        // Display Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            DisplayScreen(
                expression = expression,
                result = result,
                livePreview = livePreview,
                isDegrees = degrees,
                isScientific = isScientific,
                onToggleDegrees = { degrees = !degrees },
                onToggleScientific = { isScientific = !isScientific },
                onOpenHistory = { showHistorySheet = true },
                onBackspace = { handleKey("⌫") },
                onClearAll = { handleKey("AC") },
                onBack = onBack
            )
        }

        // Scientific Keys (collapsible with smooth expand animation)
        AnimatedVisibility(
            visible = isScientific,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Sci Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalcButton(text = if (inverse) "INV*" else "INV", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("INV") }
                    CalcButton(text = if (inverse) "sin⁻¹" else "sin", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("sin") }
                    CalcButton(text = if (inverse) "cos⁻¹" else "cos", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("cos") }
                    CalcButton(text = if (inverse) "tan⁻¹" else "tan", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("tan") }
                    CalcButton(text = "π", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("π") }
                }
                // Sci Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalcButton(text = "ln", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("ln") }
                    CalcButton(text = "log", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("log") }
                    CalcButton(text = "√", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("√") }
                    CalcButton(text = "x²", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("x²") }
                    CalcButton(text = "xʸ", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("xʸ") }
                }
                // Sci Row 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalcButton(text = "e", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("e") }
                    CalcButton(text = "!", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("!") }
                    CalcButton(text = "1/x", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("1/x") }
                    CalcButton(text = "(", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey("(") }
                    CalcButton(text = ")", keyType = KeyType.SCIENTIFIC, height = 44.dp, fontSize = 14.sp, modifier = Modifier.weight(1f)) { handleKey(")") }
                }
            }
        }

        // Standard Keypad Area (4x5 layout)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val keyHeight = if (isScientific) 56.dp else 66.dp

            // Row 1
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton(text = "AC", keyType = KeyType.CLEAR, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("AC") }
                CalcButton(text = "( )", keyType = KeyType.FUNCTION, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("()") }
                CalcButton(text = "%", keyType = KeyType.FUNCTION, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("%") }
                CalcButton(text = "÷", keyType = KeyType.OPERATOR, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("÷") }
            }

            // Row 2
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton(text = "7", keyType = KeyType.NUMBER, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("7") }
                CalcButton(text = "8", keyType = KeyType.NUMBER, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("8") }
                CalcButton(text = "9", keyType = KeyType.NUMBER, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("9") }
                CalcButton(text = "×", keyType = KeyType.OPERATOR, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("×") }
            }

            // Row 3
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton(text = "4", keyType = KeyType.NUMBER, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("4") }
                CalcButton(text = "5", keyType = KeyType.NUMBER, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("5") }
                CalcButton(text = "6", keyType = KeyType.NUMBER, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("6") }
                CalcButton(text = "−", keyType = KeyType.OPERATOR, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("−") }
            }

            // Row 4
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton(text = "1", keyType = KeyType.NUMBER, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("1") }
                CalcButton(text = "2", keyType = KeyType.NUMBER, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("2") }
                CalcButton(text = "3", keyType = KeyType.NUMBER, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("3") }
                CalcButton(text = "+", keyType = KeyType.OPERATOR, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("+") }
            }

            // Row 5
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalcButton(text = "+/-", keyType = KeyType.FUNCTION, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("+/-") }
                CalcButton(text = "0", keyType = KeyType.NUMBER, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("0") }
                CalcButton(text = ".", keyType = KeyType.FUNCTION, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey(".") }
                CalcButton(text = "=", keyType = KeyType.EQUALS, height = keyHeight, modifier = Modifier.weight(1f)) { handleKey("=") }
            }
        }

        AdaptiveBannerAd(modifier = Modifier.padding(top = 4.dp))
    }

    // Quick History Bottom Sheet
    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            containerColor = palette.cardBorder.copy(alpha = 0.95f),
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
                    Text("Calculation History", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    if (historyList.isNotEmpty()) {
                        IconButton(onClick = onClearHistory) {
                            Icon(Icons.Default.DeleteSweep, "Clear History", tint = palette.clearKeyText)
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
                        Text("No calculations yet", color = palette.textTertiary, fontSize = 15.sp)
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
                                colors = CardDefaults.cardColors(containerColor = palette.numKeyBg)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                                    if (parts.size == 2) {
                                        Text(parts[0], fontSize = 14.sp, color = palette.textSecondary)
                                        Spacer(Modifier.height(4.dp))
                                        Text("= ${parts[1]}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = palette.opKeyText)
                                    } else {
                                        Text(item, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = palette.textPrimary)
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
