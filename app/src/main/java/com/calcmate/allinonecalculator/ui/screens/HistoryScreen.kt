package com.calcmate.allinonecalculator.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calcmate.allinonecalculator.ads.AdaptiveBannerAd
import com.calcmate.allinonecalculator.ui.theme.BrandCyan
import com.calcmate.allinonecalculator.ui.theme.BrandIndigo
import com.calcmate.allinonecalculator.ui.theme.LocalCalculatorPalette

@Composable
fun HistoryScreen(
    historyList: List<String>,
    onDeleteItem: (String) -> Unit = {},
    onClearHistory: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val palette = LocalCalculatorPalette.current
    val context = LocalContext.current
    var showClearConfirm by remember { mutableStateOf(false) }

    fun copyItem(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Calculation", text))
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardGradient)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = palette.textPrimary
                        )
                    }
                }
                Column {
                    Text(
                        text = "History",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                    Text(
                        text = "${historyList.size} calculations stored",
                        fontSize = 13.sp,
                        color = palette.textSecondary
                    )
                }
            }

            if (historyList.isNotEmpty()) {
                FilledTonalButton(
                    onClick = { showClearConfirm = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = palette.fnKeyBg,
                        contentColor = palette.clearKeyText
                    )
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Clear All")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(palette.fnKeyBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = palette.textTertiary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "No calculations yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Results from any calculator tool will automatically appear here.",
                        fontSize = 13.sp,
                        color = palette.textSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(historyList) { item ->
                    val parts = item.split(" = ")
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = palette.numKeyBg),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                if (parts.size == 2) {
                                    Text(
                                        text = parts[0],
                                        fontSize = 14.sp,
                                        color = palette.textSecondary
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "= ${parts[1]}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandIndigo
                                    )
                                } else {
                                    Text(
                                        text = item,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.textPrimary
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { copyItem(if (parts.size == 2) parts[1] else item) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = palette.textSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteItem(item) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Delete",
                                        tint = palette.textTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        AdaptiveBannerAd(modifier = Modifier.padding(bottom = 8.dp))
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear History?") },
            text = { Text("This will permanently erase all saved calculation records.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.clearKeyText)
                ) {
                    Text("Clear All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
