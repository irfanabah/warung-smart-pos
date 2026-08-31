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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DebtEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.WarungViewModel
import com.example.util.Formatters

@Composable
fun KasbonScreen(
    viewModel: WarungViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val debts by viewModel.debts.collectAsState()
    var filterOnlyUnpaid by remember { mutableStateOf(true) }

    val filteredDebts = remember(debts, filterOnlyUnpaid) {
        if (filterOnlyUnpaid) debts.filter { !it.isPaid } else debts
    }

    val totalUnpaid = debts.filter { !it.isPaid }.sumOf { it.remainingDebt }
    val totalPaid = debts.filter { it.isPaid }.sumOf { it.totalDebt }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("kasbon_screen"),
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
                    text = "📝 BUKU KASBON & HUTANG",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Pencatatan Piutang Pelanggan",
                    color = WarningOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = { viewModel.openAddDebtDialog() },
                colors = ButtonDefaults.buttonColors(containerColor = WarningOrange),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("add_debt_btn")
            ) {
                Text("＋ Catat Kasbon", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }

        // Summary Card
        Surface(
            color = Slate950,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Belum Lunas", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        Formatters.formatRupiah(totalUnpaid),
                        color = WarningOrange,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text("${debts.count { !it.isPaid }} Orang", color = Slate500, fontSize = 10.sp)
                }
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(Slate800))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Sudah Lunas", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        Formatters.formatRupiah(totalPaid),
                        color = SuccessGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text("${debts.count { it.isPaid }} Riwayat", color = Slate500, fontSize = 10.sp)
                }
            }
        }

        // Filter Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (filterOnlyUnpaid) WarningOrange else Slate950)
                    .border(1.dp, if (filterOnlyUnpaid) WarningOrange else Slate800, RoundedCornerShape(12.dp))
                    .clickable { filterOnlyUnpaid = true }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Belum Lunas (${debts.count { !it.isPaid }})",
                    color = if (filterOnlyUnpaid) Color.White else Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!filterOnlyUnpaid) WarningOrange else Slate950)
                    .border(1.dp, if (!filterOnlyUnpaid) WarningOrange else Slate800, RoundedCornerShape(12.dp))
                    .clickable { filterOnlyUnpaid = false }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Semua (${debts.size})",
                    color = if (!filterOnlyUnpaid) Color.White else Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Debt Cards List
        if (filteredDebts.isEmpty()) {
            Surface(
                color = Slate950,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🎉", fontSize = 28.sp)
                        Text(
                            if (filterOnlyUnpaid) "Tidak ada kasbon yang belum lunas!" else "Belum ada catatan kasbon.",
                            color = Slate400,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredDebts.forEach { debt ->
                    DebtCardItem(
                        debt = debt,
                        onPay = { viewModel.openPayDebtDialog(debt) },
                        onShareWA = { viewModel.shareDebtReminderToWhatsApp(context, debt) },
                        onDelete = { viewModel.deleteDebt(debt) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DebtCardItem(
    debt: DebtEntity,
    onPay: () -> Unit,
    onShareWA: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = Slate950,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (debt.isPaid) SuccessGreen.copy(alpha = 0.3f) else Slate800)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Name & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                    Text(
                        text = debt.customerName,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    if (debt.customerPhone.isNotBlank()) {
                        Text("WA: ${debt.customerPhone}", color = Slate400, fontSize = 10.sp)
                    }
                }

                Surface(
                    color = if (debt.isPaid) SuccessGreen.copy(alpha = 0.2f) else WarningOrange.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (debt.isPaid) "✅ LUNAS" else "BELUM LUNAS",
                        color = if (debt.isPaid) SuccessGreen else WarningOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            // Amounts
            Surface(
                color = Slate900,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Hutang:", color = Slate400, fontSize = 10.sp)
                        Text(Formatters.formatRupiah(debt.totalDebt), color = Slate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Sisa Harus Dibayar:", color = if (debt.isPaid) SuccessGreen else WarningOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            Formatters.formatRupiah(debt.remainingDebt),
                            color = if (debt.isPaid) SuccessGreen else WarningOrange,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Notes & Due Date
            if (debt.notes.isNotBlank() || debt.dueDate.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (debt.notes.isNotBlank()) {
                        Text("Rincian: ${debt.notes}", color = Slate400, fontSize = 10.sp)
                    }
                    if (debt.dueDate.isNotBlank()) {
                        Text("Janji Bayar: ${debt.dueDate}", color = Amber400, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Actions: Pay, WhatsApp, Delete
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!debt.isPaid) {
                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Text("💵 Bayar / Cicil", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }

                Button(
                    onClick = onShareWA,
                    colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Share, contentDescription = "WA", tint = SuccessGreen, modifier = Modifier.size(14.dp))
                        Text("Kirim WA", color = Slate200, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = ErrorRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
