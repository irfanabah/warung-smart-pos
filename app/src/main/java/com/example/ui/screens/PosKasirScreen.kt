package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.WarungViewModel
import com.example.util.Formatters

@Composable
fun PosKasirScreen(
    viewModel: WarungViewModel,
    themeColor: Color,
    onOpenScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val heldCarts by viewModel.heldCarts.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val discount by viewModel.discount.collectAsState()

    val categories = remember(products) {
        val list = mutableListOf("Semua")
        val unique = products.map { it.category }.distinct().filter { it.isNotBlank() }
        list.addAll(unique)
        list
    }

    val filteredProducts = remember(products, selectedCategory, searchQuery) {
        products.filter { prod ->
            val matchesCategory = selectedCategory == "Semua" || prod.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    prod.name.contains(searchQuery, ignoreCase = true) ||
                    prod.barcode.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val subtotal = cart.sumOf { it.total }
    val grandTotal = (subtotal - discount).coerceAtLeast(0.0)

    var discountInput by remember(discount) {
        mutableStateOf(if (discount > 0) discount.toLong().toString() else "")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("pos_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search and Scanner Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("🔍 Cari nama atau ketik barcode...", color = Slate500, fontSize = 12.sp) },
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear", tint = Slate400, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Slate950,
                    unfocusedContainerColor = Slate950,
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = Slate800
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f).testTag("search_product_input")
            )

            Button(
                onClick = onOpenScanner,
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(52.dp).testTag("scan_camera_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan", tint = Slate950, modifier = Modifier.size(18.dp))
                    Text("Scan", color = Slate950, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }

        // Category Pills Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) themeColor else Slate950)
                        .border(1.dp, if (isSelected) themeColor else Slate800, RoundedCornerShape(12.dp))
                        .clickable { viewModel.setSelectedCategory(cat) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("cat_pill_$cat"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Slate950 else Slate300,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold
                    )
                }
            }
        }

        // Split Layout: Products Grid (Top 55%) and Cart Panel (Bottom 45%)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Products Section
            Surface(
                color = Slate950,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
            ) {
                if (filteredProducts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("📦", fontSize = 24.sp)
                            Text("Tidak ada produk yang cocok", color = Slate500, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                themeColor = themeColor,
                                onClick = { viewModel.handleProductClick(product) }
                            )
                        }
                    }
                }
            }

            // Shopping Cart Panel
            Surface(
                color = Slate950,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Cart Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart", tint = themeColor, modifier = Modifier.size(16.dp))
                            Text(
                                "Keranjang (${cart.sumOf { it.qty }})",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            color = themeColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.clickable { viewModel.openHoldListDialog() }
                        ) {
                            Text(
                                "⏸️ Hold (${heldCarts.size})",
                                color = themeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Cart Items Scrollable List
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 4.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (cart.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🛒 Keranjang belanja masih kosong", color = Slate600, fontSize = 11.sp)
                            }
                        } else {
                            cart.forEachIndexed { idx, item ->
                                Surface(
                                    color = Slate900,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                                            Text(
                                                item.displayName,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                "${Formatters.formatRupiah(item.price)} x ${item.qty}",
                                                color = themeColor,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Slate800)
                                                    .clickable { viewModel.updateCartItemQty(idx, -1) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("-", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                            }
                                            Text(
                                                item.qty.toString(),
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.width(16.dp),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Slate800)
                                                    .clickable { viewModel.updateCartItemQty(idx, 1) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("+", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                            }
                                            IconButton(
                                                onClick = { viewModel.removeCartItem(idx) },
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = ErrorRed, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Calculation & Action Row
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Diskon Kilat (Rp):", color = Amber400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = discountInput,
                                onValueChange = {
                                    discountInput = it
                                    viewModel.setDiscount(Formatters.parseRupiah(it))
                                },
                                placeholder = { Text("0", color = Slate600, fontSize = 10.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Slate900,
                                    unfocusedContainerColor = Slate900
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.width(90.dp).height(38.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Bayar:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            Text(
                                Formatters.formatRupiah(grandTotal),
                                color = themeColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openHoldDialog() },
                                colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(44.dp).testTag("hold_cart_btn")
                            ) {
                                Text("⏸️ Tunda", color = Slate200, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Button(
                                onClick = { viewModel.openPaymentDialog() },
                                enabled = cart.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.5f).height(44.dp).testTag("pay_cart_btn")
                            ) {
                                Text("💵 Bayar", color = Slate950, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: ProductEntity,
    themeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = Slate900,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("prod_card_${product.id}")
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    color = if (product.isSeblak) themeColor.copy(alpha = 0.2f) else Slate800,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = product.category,
                        color = if (product.isSeblak) themeColor else Slate300,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = product.name,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    Formatters.formatRupiah(product.price),
                    color = themeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Stok:${product.stock}",
                    color = if (product.stock <= 5) ErrorRed else Slate500,
                    fontSize = 9.sp,
                    fontWeight = if (product.stock <= 5) FontWeight.Black else FontWeight.Normal
                )
            }
        }
    }
}
