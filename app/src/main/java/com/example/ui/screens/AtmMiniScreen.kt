package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.WarungViewModel
import com.example.util.Formatters

@Composable
fun AtmMiniScreen(
    viewModel: WarungViewModel,
    modifier: Modifier = Modifier
) {
    val atmTransactions by viewModel.atmTransactions.collectAsState()
    val totalAdminFee = atmTransactions.sumOf { it.adminFee }
    val totalVolume = atmTransactions.sumOf { it.totalCharged }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("atm_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "💳 MINI ATM & E-WALLET PPOB",
            color = Slate400,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        // Summary Card
        Surface(
            color = Slate950,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Keuntungan Admin:", color = Slate400, fontSize = 12.sp)
                    Text(
                        Formatters.formatRupiah(totalAdminFee),
                        color = SuccessGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Volume Transaksi:", color = Slate400, fontSize = 11.sp)
                    Text(Formatters.formatRupiah(totalVolume), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Service Catalog Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AtmServiceCard(
                title = "Top-Up DANA / ShopeePay",
                subtitle = "Isi saldo e-wallet instan dengan admin otomatis",
                emoji = "💙",
                badgeColor = InfoBlue,
                onOpen = { viewModel.openAtmDialog("Top-Up DANA / ShopeePay") }
            )

            AtmServiceCard(
                title = "Transfer / Tarik Tunai SeaBank",
                subtitle = "Transfer antar bank digital & tarik tunai aman",
                emoji = "🌊",
                badgeColor = Cyan500,
                onOpen = { viewModel.openAtmDialog("Transfer / SeaBank") }
            )

            AtmServiceCard(
                title = "Token Listrik PLN & Pulsa",
                subtitle = "Isi pulsa operator & token listrik prabayar",
                emoji = "⚡",
                badgeColor = Amber500,
                onOpen = { viewModel.openAtmDialog("Token PLN / Pulsa") }
            )

            AtmServiceCard(
                title = "Top-Up GoPay / OVO / LinkAja",
                subtitle = "Isi saldo semua dompet digital",
                emoji = "🟢",
                badgeColor = SuccessGreen,
                onOpen = { viewModel.openAtmDialog("Top-Up GoPay / OVO") }
            )
        }

        // Recent ATM History
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "📋 RIWAYAT TRANSAKSI MINI ATM",
                color = Slate400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            if (atmTransactions.isEmpty()) {
                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada transaksi Mini ATM.", color = Slate500, fontSize = 12.sp)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    atmTransactions.forEach { trx ->
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
                                    Text(trx.serviceType, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "Tujuan: ${trx.targetNumber} • Sumber: ${trx.sourceAccount}",
                                        color = InfoBlue,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        "${trx.timeFormatted} • Admin: ${Formatters.formatRupiah(trx.adminFee)}",
                                        color = Slate400,
                                        fontSize = 9.sp
                                    )
                                }
                                Text(
                                    Formatters.formatRupiah(trx.totalCharged),
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
private fun AtmServiceCard(
    title: String,
    subtitle: String,
    emoji: String,
    badgeColor: Color,
    onOpen: () -> Unit
) {
    Surface(
        color = Slate950,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(emoji, fontSize = 22.sp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = Slate400, fontSize = 10.sp)
                }
            }

            Button(
                onClick = onOpen,
                colors = ButtonDefaults.buttonColors(containerColor = badgeColor),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Buka", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
