package com.example

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        loadUrl("https://warung-smart-pos.vercel.app")
                    }
                }
            )
        }
    }
}
    val showReceiptDialog by viewModel.showReceiptDialog.collectAsState()
    val activeReceipt by viewModel.activeReceipt.collectAsState()
    val showHoldDialog by viewModel.showHoldDialog.collectAsState()
    val showHoldListDialog by viewModel.showHoldListDialog.collectAsState()
    val heldCarts by viewModel.heldCarts.collectAsState()
    val showAtmDialog by viewModel.showAtmDialog.collectAsState()
    val activeAtmService by viewModel.activeAtmService.collectAsState()
    val showProductDialog by viewModel.showProductDialog.collectAsState()
    val editingProduct by viewModel.editingProduct.collectAsState()
    val showDebtDialog by viewModel.showDebtDialog.collectAsState()
    val initialDebtName by viewModel.initialDebtName.collectAsState()
    val initialDebtAmount by viewModel.initialDebtAmount.collectAsState()
    val initialDebtNotes by viewModel.initialDebtNotes.collectAsState()
    val showPayDebtDialog by viewModel.showPayDebtDialog.collectAsState()
    val selectedDebtForPay by viewModel.selectedDebtForPay.collectAsState()
    val products by viewModel.products.collectAsState()

    var showBarcodeScanner by remember { mutableStateOf(false) }
    var currentToast by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Collect Toast events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { event ->
            currentToast = event
            delay(2200)
            currentToast = null
        }
    }

    val themeColor = selectedTheme.primaryColor

    WarungSmartTheme(selectedTheme = selectedTheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                HeaderBar(
                    storeName = storeName,
                    cashierName = cashierName,
                    storeEmoji = storeEmoji,
                    themeColor = themeColor,
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                BottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.switchTab(it) },
                    themeColor = themeColor
                )
            },
            containerColor = Slate900
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Main Tab Screens
                when (currentTab) {
                    MainTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        themeColor = themeColor
                    )
                    MainTab.POS -> PosKasirScreen(
                        viewModel = viewModel,
                        themeColor = themeColor,
                        onOpenScanner = { showBarcodeScanner = true }
                    )
                    MainTab.ATM -> AtmMiniScreen(
                        viewModel = viewModel
                    )
                    MainTab.KASBON -> KasbonScreen(
                        viewModel = viewModel
                    )
                    MainTab.INVENTORY -> StokScreen(
                        viewModel = viewModel,
                        themeColor = themeColor,
                        onOpenScanner = { showBarcodeScanner = true }
                    )
                    MainTab.REPORTS -> LaporanScreen(
                        viewModel = viewModel,
                        themeColor = themeColor
                    )
                    MainTab.SETTINGS -> SettingsScreen(
                        viewModel = viewModel
                    )
                }

                // Toast Notification Overlay
                AnimatedVisibility(
                    visible = currentToast != null,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -40 }),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                ) {
                    currentToast?.let { (msg, icon) ->
                        Surface(
                            color = Slate950,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            shadowElevation = 8.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(icon, fontSize = 14.sp)
                                Text(msg, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Modals & Dialogs
        if (showSeblakDialog && activeSeblakProduct != null) {
            SeblakModifierDialog(
                product = activeSeblakProduct!!,
                viewModel = viewModel,
                themeColor = themeColor,
                onDismiss = { viewModel.closeSeblakDialog() }
            )
        }

        if (showPaymentDialog) {
            PaymentDialog(
                viewModel = viewModel,
                themeColor = themeColor,
                onDismiss = { viewModel.closePaymentDialog() }
            )
        }

        if (showReceiptDialog && activeReceipt != null) {
            ReceiptDialog(
                trx = activeReceipt!!,
                viewModel = viewModel,
                storeName = storeName,
                storeAddress = storeAddress,
                storeEmoji = storeEmoji,
                onDismiss = { viewModel.closeReceiptDialog() }
            )
        }

        if (showHoldDialog) {
            HoldInputDialog(
                heldCount = heldCarts.size,
                onDismiss = { viewModel.closeHoldDialog() },
                onConfirm = { label -> viewModel.confirmHoldCart(label) }
            )
        }

        if (showHoldListDialog) {
            HoldListDialog(
                heldCarts = heldCarts,
                viewModel = viewModel,
                themeColor = themeColor,
                onDismiss = { viewModel.closeHoldListDialog() }
            )
        }

        if (showAtmDialog) {
            AtmTransactionDialog(
                serviceType = activeAtmService,
                viewModel = viewModel,
                onDismiss = { viewModel.closeAtmDialog() }
            )
        }

        if (showProductDialog) {
            val categories = remember(products) {
                products.map { it.category }.distinct().filter { it.isNotBlank() }
            }
            ProductFormDialog(
                editingProduct = editingProduct,
                categories = categories.ifEmpty { listOf("Sembako", "Seblak", "Minuman", "Makanan", "Jajanan") },
                themeColor = themeColor,
                onDismiss = { viewModel.closeProductDialog() },
                onSave = { name, cat, price, costPrice, stock, unit, barcode, isSeblak ->
                    viewModel.saveProduct(name, cat, price, costPrice, stock, unit, barcode, isSeblak)
                }
            )
        }

        if (showDebtDialog) {
            AddDebtDialog(
                initialName = initialDebtName,
                initialAmount = initialDebtAmount,
                initialNotes = initialDebtNotes,
                onDismiss = { viewModel.closeDebtDialog() },
                onSave = { name, phone, amount, notes, dueDate ->
                    viewModel.saveNewDebt(name, phone, amount, notes, dueDate)
                }
            )
        }

        if (showPayDebtDialog && selectedDebtForPay != null) {
            PayDebtDialog(
                debt = selectedDebtForPay!!,
                onDismiss = { viewModel.closePayDebtDialog() },
                onPay = { amount -> viewModel.payDebt(amount) }
            )
        }

        if (showBarcodeScanner) {
            BarcodeScanSimulationDialog(
                products = products,
                onDismiss = { showBarcodeScanner = false },
                onBarcodeScanned = { code ->
                    viewModel.onBarcodeScanned(code)
                }
            )
        }
    }
}
