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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AtmTransactionEntity
import com.example.data.model.TransactionEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.WarungViewModel
import com.example.util.Formatters

@Composable
fun LaporanScreen(
    viewModel: WarungViewModel,
    themeColor: Color,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val atmTransactions by viewModel.atmTransactions.collectAsState()

    var activeSubTab by remember { mutableStateOf("pos") } // pos, atm, profit
    var showClearConfirm by remember { mutableStateOf(false) }

    val totalOmzet = transactions.sumOf { it.totalAmount }
    val totalProfit = transactions.sumOf { it.totalProfit }
    val totalAtmFee = atmTransactions.sumOf { it.adminFee }
    val combinedNetProfit = totalProfit + totalAtmFee

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Hapus Semua Riwayat?", color = Color.White, fontWeight = FontWeight.Black) },
            text = { Text("Yakin ingin membersihkan seluruh data laporan penjualan kasir dan transaksi ATM?", color = Slate400) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearReports()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Hapus Riwayat", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showClearConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Slate800)
                ) {
                    Text("Batal", color = Slate300)
                }
            },
            containerColor = Slate900
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("laporan_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "📈 LAPORAN KEUANGAN",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Riwayat & Perhitungan Laba",
                    color = themeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = { showClearConfirm = true },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("🗑️ Reset", color = ErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Sub Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                Triple("pos", "Kasir (${transactions.size})", "🛒"),
                Triple("atm", "ATM (${atmTransactions.size})", "💳"),
                Triple("profit", "Laba Bersih", "📊")
            ).forEach { (key, label, emoji) ->
                val isSelected = activeSubTab == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) themeColor else Slate950)
                        .border(1.dp, if (isSelected) themeColor else Slate800, RoundedCornerShape(12.dp))
                        .clickable { activeSubTab = key }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$emoji $label",
                        color = if (isSelected) Slate950 else Slate400,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                    )
                }
            }
        }

        when (activeSubTab) {
            "profit" -> {
                // Profit / Laba Rugi Breakdown (RECOMMENDED FEATURE)
                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("📊 ANALISIS LABA USAHA", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Omzet Penjualan POS:", color = Slate400, fontSize = 12.sp)
                            Text(Formatters.formatRupiah(totalOmzet), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Laba Bersih Produk POS:", color = Slate400, fontSize = 12.sp)
                            Text(Formatters.formatRupiah(totalProfit), color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Komisi Admin Mini ATM:", color = Slate400, fontSize = 12.sp)
                            Text(Formatters.formatRupiah(totalAtmFee), color = InfoBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Slate800))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TOTAL KEUNTUNGAN BERSIH:", color = themeColor, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            Text(
                                Formatters.formatRupiah(combinedNetProfit),
                                color = themeColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            "pos" -> {
                // POS Transactions List
                if (transactions.isEmpty()) {
                    Surface(
                        color = Slate950,
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada riwayat transaksi kasir.", color = Slate500, fontSize = 12.sp)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        transactions.forEach { trx ->
                            PosTransactionCard(trx = trx, viewModel = viewModel)
                        }
                    }
                }
            }

            "atm" -> {
                // ATM Transactions List
                if (atmTransactions.isEmpty()) {
                    Surface(
                        color = Slate950,
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada riwayat transaksi Mini ATM.", color = Slate500, fontSize = 12.sp)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        atmTransactions.forEach { atm ->
                            AtmTransactionCard(atm = atm, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PosTransactionCard(
    trx: TransactionEntity,
    viewModel: WarungViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val items = remember(trx.itemsJson) { viewModel.deserializeCartItems(trx.itemsJson) }

    Surface(
        color = Slate950,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(trx.id, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "${trx.timeFormatted} • Metode: ${trx.paymentMethod}",
                        color = Slate400,
                        fontSize = 10.sp
                    )
                }
                Text(
                    Formatters.formatRupiah(trx.totalAmount),
                    color = SuccessGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Summary of items
            Surface(
                color = Slate900,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    items.take(4).forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "${item.displayName} (x${item.qty})",
                                color = Slate300,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                            Text(
                                Formatters.formatRupiah(item.total),
                                color = Slate400,
                                fontSize = 10.sp
                            )
                        }
                    }
                    if (items.size > 4) {
                        Text("+ ${items.size - 4} item lainnya...", color = Slate500, fontSize = 9.sp)
                    }
                }
            }

            // Quick Actions: Print External & WA Share
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { viewModel.printReceiptExternal(context, trx) },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(32.dp).testTag("print_history_trx_${trx.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Print, contentDescription = "Cetak", tint = Amber500, modifier = Modifier.size(13.dp))
                        Text("🖨️ Cetak Struk", color = Amber500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { viewModel.shareReceiptToWhatsApp(context, trx) },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(32.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Share, contentDescription = "WA", tint = SuccessGreen, modifier = Modifier.size(13.dp))
                        Text("📲 Kirim WA", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AtmTransactionCard(
    atm: AtmTransactionEntity,
    viewModel: WarungViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Surface(
        color = Slate950,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(atm.serviceType, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Tujuan: ${atm.targetNumber} (${atm.sourceAccount})",
                        color = InfoBlue,
                        fontSize = 10.sp
                    )
                    Text(
                        "${atm.timeFormatted} • Admin: ${Formatters.formatRupiah(atm.adminFee)}",
                        color = Slate400,
                        fontSize = 9.sp
                    )
                }
                Text(
                    Formatters.formatRupiah(atm.totalCharged),
                    color = SuccessGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Quick Actions: Print External & Send
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { viewModel.printAtmReceiptExternal(context, atm) },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(32.dp).testTag("print_history_atm_${atm.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Print, contentDescription = "Cetak", tint = Amber500, modifier = Modifier.size(13.dp))
                        Text("🖨️ Cetak Bukti ATM", color = Amber500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { viewModel.sendAtmReceiptToThermalBluetoothApp(context, atm) },
                    colors = ButtonDefaults.buttonColors(containerColor = InfoBlue.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(32.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Bluetooth, contentDescription = "Thermal BT", tint = InfoBlue, modifier = Modifier.size(13.dp))
                        Text("📄 Thermal App", color = InfoBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
