package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.WarungViewModel
import com.example.util.Formatters

@Composable
fun SeblakModifierDialog(
    product: ProductEntity,
    viewModel: WarungViewModel,
    themeColor: Color,
    onDismiss: () -> Unit
) {
    val spicyLevels = listOf("Tidak Pedas", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5")
    val selectedSpicy by viewModel.seblakSpicyLevel.collectAsState()
    val customPrice by viewModel.seblakCustomPrice.collectAsState()
    val notes by viewModel.seblakNotes.collectAsState()

    var priceInput by remember(customPrice) {
        mutableStateOf(if (customPrice > 0) customPrice.toLong().toString() else product.price.toLong().toString())
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
            modifier = Modifier.fillMaxWidth().testTag("seblak_modal")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = themeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "🔥 Menu Seblak Fleksibel",
                            color = themeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = Slate400)
                    }
                }

                Text(
                    text = product.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                // Custom Price Field
                Column {
                    Text("Sesuaikan Harga (Rp)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = {
                            priceInput = it
                            viewModel.setSeblakPrice(Formatters.parseRupiah(it))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = themeColor,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Spicy Level Grid
                Column {
                    Text("Pilih Level Pedas", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        val row1 = spicyLevels.take(3)
                        val row2 = spicyLevels.drop(3)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            row1.forEach { lvl ->
                                val isSelected = selectedSpicy == lvl
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) themeColor else Slate950)
                                        .border(1.dp, if (isSelected) themeColor else Slate800, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.setSeblakSpicy(lvl) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lvl,
                                        color = if (isSelected) Slate950 else Slate300,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            row2.forEach { lvl ->
                                val isSelected = selectedSpicy == lvl
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) themeColor else Slate950)
                                        .border(1.dp, if (isSelected) themeColor else Slate800, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.setSeblakSpicy(lvl) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lvl,
                                        color = if (isSelected) Slate950 else Slate300,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Notes / Topping
                Column {
                    Text("Catatan Topping / Porsi", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { viewModel.setSeblakNotes(it) },
                        placeholder = { Text("Contoh: Tambah ceker, kuah pisah...", color = Slate600, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Batal", color = Slate300, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.confirmSeblakToCart() },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1.5f).height(48.dp)
                    ) {
                        Text("Tambah ke Kasir", color = Slate950, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentDialog(
    viewModel: WarungViewModel,
    themeColor: Color,
    onDismiss: () -> Unit
) {
    val cart by viewModel.cart.collectAsState()
    val discount by viewModel.discount.collectAsState()
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    val cashGiven by viewModel.cashGiven.collectAsState()

    val subtotal = cart.sumOf { it.total }
    val grandTotal = (subtotal - discount).coerceAtLeast(0.0)
    var cashInput by remember(cashGiven) {
        mutableStateOf(if (cashGiven > 0) cashGiven.toLong().toString() else "")
    }
    var customerNameInput by remember { mutableStateOf("") }

    val change = if (paymentMethod == "Tunai") {
        (Formatters.parseRupiah(cashInput) - grandTotal).coerceAtLeast(0.0)
    } else 0.0

    val quickCashOptions = listOf(10000.0, 20000.0, 50000.0, 100000.0)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
            modifier = Modifier.fillMaxWidth().testTag("payment_modal")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "💵 Pembayaran Kasir",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Tagihan", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = Formatters.formatRupiah(grandTotal),
                            color = themeColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Payment Methods
                Column {
                    Text("Metode Pembayaran", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    val methods = listOf("Tunai", "QRIS", "Transfer", "Kasbon")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                        methods.forEach { method ->
                            val isSelected = paymentMethod == method
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) themeColor else Slate950)
                                    .border(1.dp, if (isSelected) themeColor else Slate800, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setPaymentMethod(method) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = method,
                                    color = if (isSelected) Slate950 else Slate300,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Customer Name (Required for Kasbon, optional for others)
                Column {
                    Text(
                        if (paymentMethod == "Kasbon") "Nama Pelanggan Hutang (Wajib)" else "Nama Pelanggan (Opsional)",
                        color = if (paymentMethod == "Kasbon") WarningOrange else Slate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = customerNameInput,
                        onValueChange = { customerNameInput = it },
                        placeholder = { Text("Contoh: Ibu Siti / Mas Budi", color = Slate600, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (paymentMethod == "Tunai") {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Uang Diterima (Cash)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                color = SuccessGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable {
                                    cashInput = grandTotal.toLong().toString()
                                    viewModel.setCashGiven(grandTotal)
                                }
                            ) {
                                Text(
                                    "💵 Uang Pas",
                                    color = SuccessGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = cashInput,
                            onValueChange = {
                                cashInput = it
                                viewModel.setCashGiven(Formatters.parseRupiah(it))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text("Nominal uang cash...", color = Slate600, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Quick cash chips
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                            quickCashOptions.forEach { opt ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Slate800)
                                        .clickable {
                                            cashInput = opt.toLong().toString()
                                            viewModel.setCashGiven(opt)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${(opt / 1000).toInt()}rb",
                                        color = Slate200,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Change banner or Insufficient Funds Warning Banner
                        val cashParsed = Formatters.parseRupiah(cashInput)
                        val isInsufficient = cashParsed < grandTotal && grandTotal > 0
                        val shortage = (grandTotal - cashParsed).coerceAtLeast(0.0)

                        if (isInsufficient) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = WarningOrange.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, WarningOrange.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth().testTag("insufficient_funds_warning")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("⚠️", fontSize = 14.sp)
                                            Text("Uang Kurang:", color = WarningOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            text = "-${Formatters.formatRupiah(shortage)}",
                                            color = WarningOrange,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }

                                    Text(
                                        text = if (cashParsed > 0)
                                            "Uang tunai kurang ${Formatters.formatRupiah(shortage)}. Klik tombol di bawah untuk mencatat sisa kekurangan langsung ke buku Kasbon."
                                        else
                                            "Belum ada pembayaran tunai. Klik tombol di bawah untuk mencatat total belanja langsung ke Kasbon.",
                                        color = Slate300,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.transferShortageToKasbon(
                                                customerName = customerNameInput,
                                                cashPaid = cashParsed,
                                                shortageAmount = shortage
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = WarningOrange),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                            .testTag("insufficient_funds_to_kasbon_btn"),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Filled.AccountBalanceWallet,
                                                contentDescription = "Kasbon",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = if (cashParsed > 0)
                                                    "📝 Masuk ke Kasbon (Sisa ${Formatters.formatRupiah(shortage)})"
                                                else
                                                    "📝 Langsung Masuk ke Kasbon",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                            }
                        } else if (cashParsed >= grandTotal && grandTotal > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = SuccessGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Kembalian:", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        Formatters.formatRupiah(change),
                                        color = SuccessGreen,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Batal", color = Slate300, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.processCheckout(customerNameInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1.5f).height(48.dp).testTag("finalize_payment_btn")
                    ) {
                        Text("Selesaikan", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptDialog(
    trx: TransactionEntity,
    viewModel: WarungViewModel,
    storeName: String,
    storeAddress: String,
    storeEmoji: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val items = remember(trx.itemsJson) { viewModel.deserializeCartItems(trx.itemsJson) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().testTag("receipt_modal")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Text(
                    text = "$storeEmoji $storeName",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = storeAddress,
                    color = Color.DarkGray,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "ID: ${trx.id} • ${trx.timeFormatted}",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )

                Divider(color = Color.LightGray, thickness = 1.dp)

                // Items list
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.displayName,
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${item.qty} x ${Formatters.formatRupiah(item.price)}",
                                    color = Color.DarkGray,
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = Formatters.formatRupiah(item.total),
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Divider(color = Color.LightGray, thickness = 1.dp)

                // Totals
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = Color.DarkGray, fontSize = 11.sp)
                        Text(Formatters.formatRupiah(trx.subtotal), color = Color.Black, fontSize = 11.sp)
                    }
                    if (trx.discount > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Diskon", color = WarningOrange, fontSize = 11.sp)
                            Text("-${Formatters.formatRupiah(trx.discount)}", color = WarningOrange, fontSize = 11.sp)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text(Formatters.formatRupiah(trx.totalAmount), color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Metode", color = Color.DarkGray, fontSize = 11.sp)
                        Text(trx.paymentMethod, color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    if (trx.paymentMethod == "Tunai") {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Bayar", color = Color.DarkGray, fontSize = 11.sp)
                            Text(Formatters.formatRupiah(trx.cashGiven), color = Color.Black, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kembalian", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(Formatters.formatRupiah(trx.change), color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (trx.paymentMethod == "Tunai + Kasbon") {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Bayar Tunai", color = Color.DarkGray, fontSize = 11.sp)
                            Text(Formatters.formatRupiah(trx.cashGiven), color = Color.Black, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Sisa Kasbon", color = WarningOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(Formatters.formatRupiah(trx.totalAmount - trx.cashGiven), color = WarningOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (trx.paymentMethod == "Kasbon") {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Dicatat Kasbon", color = WarningOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(Formatters.formatRupiah(trx.totalAmount), color = WarningOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = "Terima Kasih Telah Berbelanja!",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Main Action: Print to External / Thermal Printer
                Button(
                    onClick = { viewModel.printReceiptExternal(context, trx) },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("print_receipt_external_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Print,
                            contentDescription = "Cetak Struk",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "🖨️ Cetak Struk External",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }

                // Secondary Actions: WhatsApp, RawBT / Thermal App, Selesai
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { viewModel.shareReceiptToWhatsApp(context, trx) },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(42.dp).testTag("share_receipt_wa_btn"),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.Share, contentDescription = "WA", tint = Color.White, modifier = Modifier.size(14.dp))
                            Text("Kirim WA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    Button(
                        onClick = { viewModel.sendReceiptToThermalBluetoothApp(context, trx) },
                        colors = ButtonDefaults.buttonColors(containerColor = InfoBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(42.dp).testTag("share_receipt_thermal_btn"),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.Bluetooth, contentDescription = "Thermal BT", tint = Color.White, modifier = Modifier.size(14.dp))
                            Text("RawBT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate950),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(42.dp).testTag("close_receipt_btn"),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text("Selesai", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun HoldInputDialog(
    heldCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var labelInput by remember { mutableStateOf("Meja #${heldCount + 1}") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "⏸️ Tunda Transaksi (Hold)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Beri label nomor meja atau nama pelanggan untuk antrean ini.",
                    color = Slate400,
                    fontSize = 11.sp
                )
                OutlinedTextField(
                    value = labelInput,
                    onValueChange = { labelInput = it },
                    placeholder = { Text("Contoh: Meja 2 / Ibu Siti", color = Slate600) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Slate950,
                        unfocusedContainerColor = Slate950
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Batal", color = Slate300)
                    }
                    Button(
                        onClick = { onConfirm(labelInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text("Simpan Hold", color = Slate950, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun HoldListDialog(
    heldCarts: List<HeldCartEntity>,
    viewModel: WarungViewModel,
    themeColor: Color,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "⏸️ Antrean Transaksi Hold",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                if (heldCarts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada antrean transaksi hold.", color = Slate500, fontSize = 12.sp)
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        heldCarts.forEach { held ->
                            val items = remember(held.itemsJson) { viewModel.deserializeCartItems(held.itemsJson) }
                            val total = items.sumOf { it.total }
                            Surface(
                                color = Slate950,
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(held.label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            "${held.timeFormatted} • ${items.size} item • ${Formatters.formatRupiah(total)}",
                                            color = Slate400,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Button(
                                            onClick = { viewModel.restoreHeldCart(held) },
                                            colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text("Ambil", color = Slate950, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        }
                                        IconButton(onClick = { viewModel.deleteHeldCart(held) }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = ErrorRed, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tutup", color = Slate300)
                }
            }
        }
    }
}
