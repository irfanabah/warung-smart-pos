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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DebtEntity
import com.example.data.model.ProductEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.WarungViewModel
import com.example.util.Formatters

@Composable
fun AtmTransactionDialog(
    serviceType: String,
    viewModel: WarungViewModel,
    onDismiss: () -> Unit
) {
    val phone by viewModel.atmPhone.collectAsState()
    val customer by viewModel.atmCustomer.collectAsState()
    val nominal by viewModel.atmNominal.collectAsState()
    val adminFee by viewModel.atmAdminFee.collectAsState()
    val source by viewModel.atmSource.collectAsState()

    var nominalInput by remember(nominal) {
        mutableStateOf(if (nominal > 0) nominal.toLong().toString() else "")
    }
    var adminFeeInput by remember(adminFee) {
        mutableStateOf(if (adminFee > 0) adminFee.toLong().toString() else "3000")
    }

    val sources = listOf("DANA", "ShopeePay", "SeaBank", "BCA / BRI")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
            modifier = Modifier.fillMaxWidth().testTag("atm_modal")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = InfoBlue.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "💳 $serviceType",
                        color = InfoBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Form Transaksi Mini ATM",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                // No. Tujuan
                Column {
                    Text("No. HP / Rekening / No. Meter PLN", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { viewModel.setAtmPhone(it) },
                        placeholder = { Text("Contoh: 081234567890", color = Slate600) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                }

                // Nama Pelanggan
                Column {
                    Text("Nama Pelanggan (Opsional)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = customer,
                        onValueChange = { viewModel.setAtmCustomer(it) },
                        placeholder = { Text("Contoh: Ibu Siti / Mas Joko", color = Slate600) },
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
                }

                // Nominal
                Column {
                    Text("Nominal Transaksi (Rp)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = nominalInput,
                        onValueChange = {
                            nominalInput = it
                            val parsed = Formatters.parseRupiah(it)
                            viewModel.setAtmNominal(parsed)
                        },
                        placeholder = { Text("Contoh: 100000", color = Slate600) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                }

                // Sumber Rekening / Akun
                Column {
                    Text("Sumber Dana / Akun Pengirim", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                        sources.forEach { src ->
                            val isSelected = source == src
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) InfoBlue else Slate950)
                                    .border(1.dp, if (isSelected) InfoBlue else Slate800, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setAtmSource(src) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = src,
                                    color = if (isSelected) Color.White else Slate300,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Biaya Admin Fleksibel
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Biaya Admin (Komisi Anda)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Otomatis / Fleksibel", color = Amber400, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = adminFeeInput,
                        onValueChange = {
                            adminFeeInput = it
                            viewModel.setAtmAdminFee(Formatters.parseRupiah(it))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Amber400,
                            unfocusedTextColor = Amber400,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Total Preview
                val totalWithFee = Formatters.parseRupiah(nominalInput) + Formatters.parseRupiah(adminFeeInput)
                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Tagih ke Pelanggan:", color = Slate300, fontSize = 11.sp)
                        Text(
                            Formatters.formatRupiah(totalWithFee),
                            color = SuccessGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Batal", color = Slate300)
                    }
                    Button(
                        onClick = { viewModel.submitAtmTransaction() },
                        colors = ButtonDefaults.buttonColors(containerColor = InfoBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1.5f).height(48.dp)
                    ) {
                        Text("Proses Transaksi", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductFormDialog(
    editingProduct: ProductEntity?,
    categories: List<String>,
    themeColor: Color,
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, price: Double, costPrice: Double, stock: Int, unit: String, barcode: String, isSeblak: Boolean) -> Unit
) {
    var name by remember(editingProduct) { mutableStateOf(editingProduct?.name ?: "") }
    var selectedCat by remember(editingProduct) { mutableStateOf(editingProduct?.category ?: "Sembako") }
    var newCatInput by remember { mutableStateOf("") }
    var customCategories by remember { mutableStateOf(categories.toMutableList()) }

    var price by remember(editingProduct) {
        mutableStateOf(editingProduct?.price?.let { if (it > 0) it.toLong().toString() else "" } ?: "")
    }
    var costPrice by remember(editingProduct) {
        mutableStateOf(editingProduct?.costPrice?.let { if (it > 0) it.toLong().toString() else "" } ?: "")
    }
    var stock by remember(editingProduct) {
        mutableStateOf(editingProduct?.stock?.toString() ?: "50")
    }
    var unit by remember(editingProduct) { mutableStateOf(editingProduct?.unit ?: "pcs") }
    var barcode by remember(editingProduct) { mutableStateOf(editingProduct?.barcode ?: "") }
    var isSeblak by remember(editingProduct) { mutableStateOf(editingProduct?.isSeblak ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (editingProduct != null) "✏️ Edit Data Produk" else "📦 Tambah Produk Baru",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                // Name
                Column {
                    Text("Nama Produk", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Contoh: Telur Ayam 1kg", color = Slate600) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Barcode with Randomizer
                Column {
                    Text("Nomor Barcode / SKU", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = barcode,
                            onValueChange = { barcode = it },
                            placeholder = { Text("89912345", color = Slate600) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { barcode = "899" + (1000..9999).random() },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Auto", color = Amber400, fontSize = 11.sp)
                        }
                    }
                }

                // Category selector & adder
                Column {
                    Text("Kategori Produk", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        customCategories.take(4).forEach { cat ->
                            val isSelected = selectedCat == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) themeColor else Slate950)
                                    .border(1.dp, if (isSelected) themeColor else Slate800, RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedCat = cat
                                        if (cat.equals("Seblak", ignoreCase = true)) isSeblak = true
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Slate950 else Slate300,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Add Custom Category Row
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = newCatInput,
                            onValueChange = { newCatInput = it },
                            placeholder = { Text("Tambah kategori...", color = Slate600, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (newCatInput.isNotBlank()) {
                                    val cat = newCatInput.trim()
                                    if (!customCategories.contains(cat)) {
                                        customCategories.add(cat)
                                    }
                                    selectedCat = cat
                                    newCatInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("＋", color = themeColor, fontWeight = FontWeight.Black)
                        }
                    }
                }

                // Prices: Jual & Modal Beli (Profit Calculator)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Harga Jual (Rp)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(3.dp))
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text("15.000", color = Slate600) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = themeColor,
                                unfocusedTextColor = themeColor,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Harga Modal/Beli", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(3.dp))
                        OutlinedTextField(
                            value = costPrice,
                            onValueChange = { costPrice = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text("12.000", color = Slate600) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Stock & Unit
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Jumlah Stok", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(3.dp))
                        OutlinedTextField(
                            value = stock,
                            onValueChange = { stock = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text("50", color = Slate600) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Satuan (pcs/kg/sak)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(3.dp))
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            placeholder = { Text("pcs", color = Slate600) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Checkbox: Menu Seblak / Custom Level
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate950)
                        .clickable { isSeblak = !isSeblak }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🔥", fontSize = 16.sp)
                        Column {
                            Text("Menu Modifikasi Seblak", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Buka modal pilih level pedas saat diklik", color = Slate400, fontSize = 10.sp)
                        }
                    }
                    Checkbox(
                        checked = isSeblak,
                        onCheckedChange = { isSeblak = it },
                        colors = CheckboxDefaults.colors(checkedColor = themeColor)
                    )
                }

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Batal", color = Slate300)
                    }
                    Button(
                        onClick = {
                            onSave(
                                name,
                                selectedCat,
                                Formatters.parseRupiah(price),
                                Formatters.parseRupiah(costPrice),
                                stock.toIntOrNull() ?: 0,
                                unit.ifBlank { "pcs" },
                                barcode,
                                isSeblak
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1.5f).height(48.dp)
                    ) {
                        Text("Simpan Produk", color = Slate950, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AddDebtDialog(
    initialName: String = "",
    initialPhone: String = "",
    initialAmount: Double = 0.0,
    initialNotes: String = "",
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, amount: Double, notes: String, dueDate: String) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var phone by remember(initialPhone) { mutableStateOf(initialPhone) }
    var amountInput by remember(initialAmount) {
        mutableStateOf(if (initialAmount > 0) initialAmount.toLong().toString() else "")
    }
    var notes by remember(initialNotes) { mutableStateOf(initialNotes) }
    var dueDate by remember { mutableStateOf("1 Minggu") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
            modifier = Modifier.fillMaxWidth().testTag("add_debt_modal")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "📝 Catat Kasbon / Hutang Pelanggan",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )

                Column {
                    Text("Nama Pelanggan", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Contoh: Ibu Siti / Mas Joko", color = Slate600) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    Text("No. WhatsApp (Untuk Kirim Pengingat)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = { Text("Contoh: 081298765432", color = Slate600) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    Text("Nominal Hutang (Rp)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("50000", color = Slate600) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WarningOrange,
                            unfocusedTextColor = WarningOrange,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    Text("Tenggat / Janji Bayar", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        placeholder = { Text("Contoh: Akhir Bulan / Sabtu Depan", color = Slate600) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    Text("Rincian Belanja / Catatan", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("Beras 5kg + Telur 1kg", color = Slate600) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Batal", color = Slate300)
                    }
                    Button(
                        onClick = {
                            onSave(name, phone, Formatters.parseRupiah(amountInput), notes, dueDate)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningOrange),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1.5f).height(48.dp)
                    ) {
                        Text("Simpan Kasbon", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PayDebtDialog(
    debt: DebtEntity,
    onDismiss: () -> Unit,
    onPay: (Double) -> Unit
) {
    var payAmountInput by remember { mutableStateOf(debt.remainingDebt.toLong().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
            modifier = Modifier.fillMaxWidth().testTag("pay_debt_modal")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "💵 Pembayaran / Cicil Kasbon",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                Surface(
                    color = Slate950,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(debt.customerName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text(
                            "Total Hutang: ${Formatters.formatRupiah(debt.totalDebt)}",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Sisa Belum Lunas:", color = WarningOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(Formatters.formatRupiah(debt.remainingDebt), color = WarningOrange, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Nominal Bayar (Rp)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "⚡ Bayar Lunas",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                payAmountInput = debt.remainingDebt.toLong().toString()
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = payAmountInput,
                        onValueChange = { payAmountInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SuccessGreen,
                            unfocusedTextColor = SuccessGreen,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Batal", color = Slate300)
                    }
                    Button(
                        onClick = { onPay(Formatters.parseRupiah(payAmountInput)) },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1.5f).height(48.dp)
                    ) {
                        Text("Catat Pembayaran", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BarcodeScanSimulationDialog(
    products: List<ProductEntity>,
    onDismiss: () -> Unit,
    onBarcodeScanned: (String) -> Unit
) {
    var manualBarcode by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
            modifier = Modifier.fillMaxWidth().testTag("barcode_scanner_modal")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "📷 Scan Barcode Scanner",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )

                // Simulated Viewfinder Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate950)
                        .border(2.dp, Amber500.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scanner", tint = Amber400, modifier = Modifier.size(36.dp))
                        Text("Arahkan scanner ke barcode produk", color = Slate400, fontSize = 10.sp)
                    }
                }

                // Quick Barcode Chips from Existing Products
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Pilih Cepat Barcode Terdaftar:", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        products.take(3).forEach { prod ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate950)
                                    .border(1.dp, Slate800, RoundedCornerShape(8.dp))
                                    .clickable {
                                        onBarcodeScanned(prod.barcode)
                                        onDismiss()
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prod.barcode,
                                    color = Amber400,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Manual barcode input
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = manualBarcode,
                        onValueChange = { manualBarcode = it },
                        placeholder = { Text("Ketik barcode...", color = Slate600, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (manualBarcode.isNotBlank()) {
                                onBarcodeScanned(manualBarcode)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cari", color = Slate950, fontWeight = FontWeight.Black)
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
