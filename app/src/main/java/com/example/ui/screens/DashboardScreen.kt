package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.WarungViewModel
import com.example.util.Formatters

@Composable
fun DashboardScreen(
    viewModel: WarungViewModel,
    themeColor: Color,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val atmTransactions by viewModel.atmTransactions.collectAsState()
    val debts by viewModel.debts.collectAsState()

    val todayOmzet = transactions.sumOf { it.totalAmount }
    val todayProfit = transactions.sumOf { it.totalProfit }
    val todayAtmFee = atmTransactions.sumOf { it.adminFee }
    val totalUnpaidDebt = debts.filter { !it.isPaid }.sumOf { it.remainingDebt }
    val lowStockProducts = products.filter { it.stock <= 5 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📊 RINGKASAN USAHA HARI INI",
                color = Slate400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Surface(
                color = themeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Live Data",
                    color = themeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Metrics Grid (2x2 Cards)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                // Omzet Kasir
                MetricCard(
                    title = "Omzet Kasir POS",
                    value = Formatters.formatRupiah(todayOmzet),
                    subtitle = "${transactions.size} Transaksi",
                    accentColor = themeColor,
                    modifier = Modifier.weight(1f)
                )
                // Laba Bersih
                MetricCard(
                    title = "Estimasi Laba Bersih",
                    value = Formatters.formatRupiah(todayProfit),
                    subtitle = "Omzet - Modal HPP",
                    accentColor = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                // Komisi Mini ATM
                MetricCard(
                    title = "Komisi Admin ATM",
                    value = Formatters.formatRupiah(todayAtmFee),
                    subtitle = "${atmTransactions.size} Transaksi PPOB",
                    accentColor = InfoBlue,
                    modifier = Modifier.weight(1f)
                )
                // Kasbon Belum Lunas (Recommended Feature)
                MetricCard(
                    title = "Kasbon / Piutang",
                    value = Formatters.formatRupiah(totalUnpaidDebt),
                    subtitle = "${debts.count { !it.isPaid }} Pelanggan",
                    accentColor = WarningOrange,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Action Buttons
        Text(
            text = "⚡ MENU CEPAT",
            color = Slate400,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            QuickActionButton(
                title = "Kasir POS",
                emoji = "🛒",
                color = themeColor,
                onClick = { viewModel.switchTab(MainTab.POS) },
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                title = "Mini ATM",
                emoji = "💳",
                color = InfoBlue,
                onClick = { viewModel.switchTab(MainTab.ATM) },
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                title = "Buku Kasbon",
                emoji = "📝",
                color = WarningOrange,
                onClick = { viewModel.switchTab(MainTab.KASBON) },
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                title = "Stok",
                emoji = "📦",
                color = Emerald500,
                onClick = { viewModel.switchTab(MainTab.INVENTORY) },
                modifier = Modifier.weight(1f)
            )
        }

        // Low Stock Warning Section
        if (lowStockProducts.isNotEmpty()) {
            Surface(
                color = ErrorRed.copy(alpha = 0.12f),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("⚠️", fontSize = 14.sp)
                            Text(
                                "Peringatan: ${lowStockProducts.size} Produk Hampir Habis!",
                                color = ErrorRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "Belanja Stok >",
                            color = ErrorRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.switchTab(MainTab.INVENTORY) }
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        lowStockProducts.take(3).forEach { prod ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(prod.name, color = Slate200, fontSize = 11.sp)
                                Text(
                                    "Sisa ${prod.stock} ${prod.unit}",
                                    color = ErrorRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Activity Preview
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🕒 TRANSAKSI TERAKHIR",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Lihat Semua >",
                    color = themeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.switchTab(MainTab.REPORTS) }
                )
            }

            if (transactions.isEmpty()) {
                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada transaksi hari ini.", color = Slate500, fontSize = 12.sp)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    transactions.take(3).forEach { trx ->
                        Surface(
                            color = Slate950,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(trx.id, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("${trx.timeFormatted} • ${trx.paymentMethod}", color = Slate400, fontSize = 10.sp)
                                }
                                Text(
                                    Formatters.formatRupiah(trx.totalAmount),
                                    color = SuccessGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Slate950,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                text = value,
                color = accentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
            Text(subtitle, color = Slate500, fontSize = 9.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    emoji: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Slate950,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
