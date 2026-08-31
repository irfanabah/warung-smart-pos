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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.WarungViewModel
import com.example.util.Formatters

@Composable
fun StokScreen(
    viewModel: WarungViewModel,
    themeColor: Color,
    onOpenScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    var searchStok by remember { mutableStateOf("") }

    val filteredProducts = remember(products, searchStok) {
        if (searchStok.isBlank()) products else {
            products.filter {
                it.name.contains(searchStok, ignoreCase = true) ||
                        it.barcode.contains(searchStok, ignoreCase = true) ||
                        it.category.contains(searchStok, ignoreCase = true)
            }
        }
    }

    val lowStockCount = products.count { it.stock <= 5 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("stok_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "📦 MANAJEMEN STOK",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${products.size} Total Produk Terdaftar",
                    color = themeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onOpenScanner,
                    colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan", tint = Amber400, modifier = Modifier.size(16.dp))
                        Text("Scan", color = Slate200, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { viewModel.openAddProductDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("＋ Produk", color = Slate950, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchStok,
            onValueChange = { searchStok = it },
            placeholder = { Text("🔍 Cari produk, kategori, atau barcode...", color = Slate500, fontSize = 12.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Slate950,
                unfocusedContainerColor = Slate950,
                focusedBorderColor = themeColor,
                unfocusedBorderColor = Slate800
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Low stock banner
        if (lowStockCount > 0) {
            Surface(
                color = ErrorRed.copy(alpha = 0.15f),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚠️", fontSize = 16.sp)
                    Text(
                        "Ada $lowStockCount produk dengan stok menipis (<= 5). Harap lakukan kulakan.",
                        color = ErrorRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Product Items List
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filteredProducts.forEach { product ->
                InventoryItemCard(
                    product = product,
                    themeColor = themeColor,
                    onEdit = { viewModel.openEditProductDialog(product) },
                    onDelete = { viewModel.deleteProduct(product) }
                )
            }
        }
    }
}

@Composable
private fun InventoryItemCard(
    product: ProductEntity,
    themeColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = Slate950,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = product.name,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (product.isSeblak) {
                        Surface(color = Amber500.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text("🔥 Seblak", color = Amber400, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                }

                Text(
                    "Barcode: ${product.barcode} • Kategori: ${product.category}",
                    color = Slate400,
                    fontSize = 10.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 2.dp)) {
                    Text(
                        "Jual: ${Formatters.formatRupiah(product.price)}",
                        color = themeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    if (product.costPrice > 0) {
                        Text(
                            "Modal: ${Formatters.formatRupiah(product.costPrice)}",
                            color = Slate400,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    color = if (product.stock <= 5) ErrorRed.copy(alpha = 0.2f) else Slate900,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (product.stock <= 5) ErrorRed else Slate800)
                ) {
                    Text(
                        text = "Stok: ${product.stock} ${product.unit}",
                        color = if (product.stock <= 5) ErrorRed else Slate300,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Amber400, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = ErrorRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
