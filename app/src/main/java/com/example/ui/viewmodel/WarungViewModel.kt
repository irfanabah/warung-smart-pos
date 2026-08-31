package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.WarungRepository
import com.example.ui.theme.AppThemePreset
import com.example.util.Formatters
import com.example.util.ReceiptPrinterHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainTab(val id: String, val title: String) {
    DASHBOARD("dashboard", "Home"),
    POS("pos", "Kasir"),
    ATM("atm", "Mini ATM"),
    KASBON("kasbon", "Buku Kasbon"),
    INVENTORY("inventory", "Stok"),
    REPORTS("reports", "Laporan"),
    SETTINGS("settings", "Setelan")
}

class WarungViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WarungRepository
    private val prefs = application.getSharedPreferences("warung_prefs", Context.MODE_PRIVATE)

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = WarungRepository(
            db.productDao(),
            db.transactionDao(),
            db.atmTransactionDao(),
            db.debtDao(),
            db.heldCartDao()
        )
    }

    // Database Flows
    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val atmTransactions: StateFlow<List<AtmTransactionEntity>> = repository.allAtmTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debts: StateFlow<List<DebtEntity>> = repository.allDebts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val heldCarts: StateFlow<List<HeldCartEntity>> = repository.allHeldCarts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // App Navigation & Identity State
    private val _currentTab = MutableStateFlow(MainTab.POS)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _storeName = MutableStateFlow(prefs.getString("store_name", "WARUNG SMART") ?: "WARUNG SMART")
    val storeName: StateFlow<String> = _storeName.asStateFlow()

    private val _cashierName = MutableStateFlow(prefs.getString("cashier_name", "Budi") ?: "Budi")
    val cashierName: StateFlow<String> = _cashierName.asStateFlow()

    private val _storeAddress = MutableStateFlow(prefs.getString("store_address", "Jl. Raya Warung No. 12") ?: "Jl. Raya Warung No. 12")
    val storeAddress: StateFlow<String> = _storeAddress.asStateFlow()

    private val _selectedTheme = MutableStateFlow(
        AppThemePreset.entries.find { it.id == prefs.getString("theme_id", "amber") } ?: AppThemePreset.AMBER
    )
    val selectedTheme: StateFlow<AppThemePreset> = _selectedTheme.asStateFlow()

    private val _storeEmoji = MutableStateFlow(prefs.getString("store_emoji", "🔥") ?: "🔥")
    val storeEmoji: StateFlow<String> = _storeEmoji.asStateFlow()

    // POS Cart & Checkout State
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _discount = MutableStateFlow(0.0)
    val discount: StateFlow<Double> = _discount.asStateFlow()

    private val _paymentMethod = MutableStateFlow("Tunai")
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    private val _cashGiven = MutableStateFlow(0.0)
    val cashGiven: StateFlow<Double> = _cashGiven.asStateFlow()

    // Active Dialogs & Modals
    private val _activeReceipt = MutableStateFlow<TransactionEntity?>(null)
    val activeReceipt: StateFlow<TransactionEntity?> = _activeReceipt.asStateFlow()

    private val _showPaymentDialog = MutableStateFlow(false)
    val showPaymentDialog: StateFlow<Boolean> = _showPaymentDialog.asStateFlow()

    private val _showReceiptDialog = MutableStateFlow(false)
    val showReceiptDialog: StateFlow<Boolean> = _showReceiptDialog.asStateFlow()

    private val _showHoldDialog = MutableStateFlow(false)
    val showHoldDialog: StateFlow<Boolean> = _showHoldDialog.asStateFlow()

    private val _showHoldListDialog = MutableStateFlow(false)
    val showHoldListDialog: StateFlow<Boolean> = _showHoldListDialog.asStateFlow()

    // Seblak Customizer Modal State
    private val _showSeblakDialog = MutableStateFlow(false)
    val showSeblakDialog: StateFlow<Boolean> = _showSeblakDialog.asStateFlow()

    private val _activeSeblakProduct = MutableStateFlow<ProductEntity?>(null)
    val activeSeblakProduct: StateFlow<ProductEntity?> = _activeSeblakProduct.asStateFlow()

    private val _seblakSpicyLevel = MutableStateFlow("Level 1")
    val seblakSpicyLevel: StateFlow<String> = _seblakSpicyLevel.asStateFlow()

    private val _seblakCustomPrice = MutableStateFlow(0.0)
    val seblakCustomPrice: StateFlow<Double> = _seblakCustomPrice.asStateFlow()

    private val _seblakNotes = MutableStateFlow("")
    val seblakNotes: StateFlow<String> = _seblakNotes.asStateFlow()

    // Product Add/Edit Modal
    private val _showProductDialog = MutableStateFlow(false)
    val showProductDialog: StateFlow<Boolean> = _showProductDialog.asStateFlow()

    private val _editingProduct = MutableStateFlow<ProductEntity?>(null)
    val editingProduct: StateFlow<ProductEntity?> = _editingProduct.asStateFlow()

    // ATM Mini Modal State
    private val _showAtmDialog = MutableStateFlow(false)
    val showAtmDialog: StateFlow<Boolean> = _showAtmDialog.asStateFlow()

    private val _activeAtmService = MutableStateFlow("Top-Up DANA / ShopeePay")
    val activeAtmService: StateFlow<String> = _activeAtmService.asStateFlow()

    private val _atmPhone = MutableStateFlow("")
    val atmPhone: StateFlow<String> = _atmPhone.asStateFlow()

    private val _atmCustomer = MutableStateFlow("")
    val atmCustomer: StateFlow<String> = _atmCustomer.asStateFlow()

    private val _atmNominal = MutableStateFlow(0.0)
    val atmNominal: StateFlow<Double> = _atmNominal.asStateFlow()

    private val _atmAdminFee = MutableStateFlow(3000.0)
    val atmAdminFee: StateFlow<Double> = _atmAdminFee.asStateFlow()

    private val _atmSource = MutableStateFlow("DANA")
    val atmSource: StateFlow<String> = _atmSource.asStateFlow()

    // Debt / Kasbon Modal State
    private val _showDebtDialog = MutableStateFlow(false)
    val showDebtDialog: StateFlow<Boolean> = _showDebtDialog.asStateFlow()

    private val _initialDebtName = MutableStateFlow("")
    val initialDebtName: StateFlow<String> = _initialDebtName.asStateFlow()

    private val _initialDebtAmount = MutableStateFlow(0.0)
    val initialDebtAmount: StateFlow<Double> = _initialDebtAmount.asStateFlow()

    private val _initialDebtNotes = MutableStateFlow("")
    val initialDebtNotes: StateFlow<String> = _initialDebtNotes.asStateFlow()

    private val _showPayDebtDialog = MutableStateFlow(false)
    val showPayDebtDialog: StateFlow<Boolean> = _showPayDebtDialog.asStateFlow()

    private val _selectedDebtForPay = MutableStateFlow<DebtEntity?>(null)
    val selectedDebtForPay: StateFlow<DebtEntity?> = _selectedDebtForPay.asStateFlow()

    // Barcode Scanner Modal
    private val _showBarcodeDialog = MutableStateFlow(false)
    val showBarcodeDialog: StateFlow<Boolean> = _showBarcodeDialog.asStateFlow()

    // Toast message trigger
    private val _toastEvent = MutableSharedFlow<Pair<String, String>>()
    val toastEvent: SharedFlow<Pair<String, String>> = _toastEvent.asSharedFlow()

    fun showToast(message: String, icon: String = "✨") {
        viewModelScope.launch {
            _toastEvent.emit(Pair(message, icon))
        }
    }

    fun switchTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun setDiscount(amount: Double) {
        _discount.value = amount
    }

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun setCashGiven(amount: Double) {
        _cashGiven.value = amount
    }

    // Cart Operations
    fun handleProductClick(product: ProductEntity) {
        if (product.isSeblak || product.category.equals("Seblak", ignoreCase = true)) {
            _activeSeblakProduct.value = product
            _seblakCustomPrice.value = product.price
            _seblakSpicyLevel.value = "Level 1"
            _seblakNotes.value = ""
            _showSeblakDialog.value = true
        } else {
            addToCart(product)
        }
    }

    fun addToCart(product: ProductEntity) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.productId == product.id && it.spicyLevel.isEmpty() && it.notes.isEmpty() }
        if (index >= 0) {
            val item = current[index]
            current[index] = item.copy(qty = item.qty + 1)
        } else {
            current.add(
                CartItem(
                    productId = product.id,
                    name = product.name,
                    category = product.category,
                    price = product.price,
                    costPrice = product.costPrice,
                    qty = 1
                )
            )
        }
        _cart.value = current
        showToast("Ditambahkan: ${product.name}", "🛒")
    }

    fun confirmSeblakToCart() {
        val product = _activeSeblakProduct.value ?: return
        val current = _cart.value.toMutableList()
        current.add(
            CartItem(
                productId = product.id,
                name = product.name,
                category = product.category,
                price = if (_seblakCustomPrice.value > 0) _seblakCustomPrice.value else product.price,
                costPrice = product.costPrice,
                spicyLevel = _seblakSpicyLevel.value,
                notes = _seblakNotes.value,
                qty = 1
            )
        )
        _cart.value = current
        _showSeblakDialog.value = false
        showToast("Seblak kustom ditambahkan!", "🔥")
    }

    fun closeSeblakDialog() {
        _showSeblakDialog.value = false
    }

    fun setSeblakSpicy(level: String) {
        _seblakSpicyLevel.value = level
    }

    fun setSeblakPrice(price: Double) {
        _seblakCustomPrice.value = price
    }

    fun setSeblakNotes(notes: String) {
        _seblakNotes.value = notes
    }

    fun updateCartItemQty(index: Int, delta: Int) {
        val current = _cart.value.toMutableList()
        if (index in current.indices) {
            val item = current[index]
            val newQty = item.qty + delta
            if (newQty <= 0) {
                current.removeAt(index)
            } else {
                current[index] = item.copy(qty = newQty)
            }
            _cart.value = current
        }
    }

    fun removeCartItem(index: Int) {
        val current = _cart.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _cart.value = current
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
        _discount.value = 0.0
    }

    // Hold / Parkir Cart Operations
    fun openHoldDialog() {
        if (_cart.value.isEmpty()) {
            showToast("Keranjang masih kosong!", "⚠️")
            return
        }
        _showHoldDialog.value = true
    }

    fun closeHoldDialog() {
        _showHoldDialog.value = false
    }

    fun confirmHoldCart(label: String) {
        if (_cart.value.isEmpty()) return
        val itemsSerialized = serializeCartItems(_cart.value)
        val defaultLabel = if (label.isNotBlank()) label else "Antrean #${(heldCarts.value.size + 1)}"
        val held = HeldCartEntity(
            label = defaultLabel,
            timeFormatted = Formatters.formatCurrentTime(),
            itemsJson = itemsSerialized,
            discount = _discount.value
        )
        viewModelScope.launch {
            repository.insertHeldCart(held)
            clearCart()
            _showHoldDialog.value = false
            showToast("Transaksi ditunda ($defaultLabel)", "⏸️")
        }
    }

    fun openHoldListDialog() {
        _showHoldListDialog.value = true
    }

    fun closeHoldListDialog() {
        _showHoldListDialog.value = false
    }

    fun restoreHeldCart(held: HeldCartEntity) {
        val items = deserializeCartItems(held.itemsJson)
        _cart.value = items
        _discount.value = held.discount
        viewModelScope.launch {
            repository.deleteHeldCart(held.id)
            _showHoldListDialog.value = false
            showToast("Antrean \"${held.label}\" diambil!", "🛒")
        }
    }

    fun deleteHeldCart(held: HeldCartEntity) {
        viewModelScope.launch {
            repository.deleteHeldCart(held.id)
            showToast("Antrean dihapus", "🗑️")
        }
    }

    // Checkout & Payment
    fun openPaymentDialog() {
        if (_cart.value.isEmpty()) {
            showToast("Keranjang masih kosong!", "⚠️")
            return
        }
        val subtotal = _cart.value.sumOf { it.total }
        val grandTotal = (subtotal - _discount.value).coerceAtLeast(0.0)
        _cashGiven.value = grandTotal
        _showPaymentDialog.value = true
    }

    fun closePaymentDialog() {
        _showPaymentDialog.value = false
    }

    fun processCheckout(customerName: String = "") {
        val cartItems = _cart.value
        if (cartItems.isEmpty()) return

        val subtotal = cartItems.sumOf { it.total }
        val grandTotal = (subtotal - _discount.value).coerceAtLeast(0.0)
        val totalProfit = cartItems.sumOf { it.profit } - _discount.value
        val cash = _cashGiven.value
        val method = _paymentMethod.value

        if (method == "Tunai" && cash < grandTotal) {
            showToast("Uang pembayaran kurang!", "⚠️")
            return
        }

        val trxId = "TRX-${System.currentTimeMillis().toString().takeLast(6)}"
        val change = if (method == "Tunai") (cash - grandTotal).coerceAtLeast(0.0) else 0.0

        val transaction = TransactionEntity(
            id = trxId,
            timeFormatted = Formatters.formatFullDateTime(),
            itemsJson = serializeCartItems(cartItems),
            subtotal = subtotal,
            discount = _discount.value,
            totalAmount = grandTotal,
            totalProfit = totalProfit.coerceAtLeast(0.0),
            cashGiven = if (method == "Tunai") cash else grandTotal,
            change = change,
            paymentMethod = method,
            customerName = customerName,
            cashierName = _cashierName.value
        )

        viewModelScope.launch {
            // Deduct inventory stock for each product
            cartItems.forEach { item ->
                repository.reduceProductStock(item.productId, item.qty)
            }
            repository.insertTransaction(transaction)

            // If payment method is Kasbon, also record into debt database
            if (method == "Kasbon") {
                val debtCustomer = if (customerName.isNotBlank()) customerName else "Pelanggan POS"
                val debt = DebtEntity(
                    customerName = debtCustomer,
                    totalDebt = grandTotal,
                    remainingDebt = grandTotal,
                    notes = "Kasir Nota $trxId (${cartItems.size} item)",
                    dueDate = "1 Minggu",
                    isPaid = false
                )
                repository.insertDebt(debt)
            }

            _activeReceipt.value = transaction
            clearCart()
            _showPaymentDialog.value = false
            _showReceiptDialog.value = true
            showToast("Transaksi $trxId Berhasil!", "🎉")
        }
    }

    fun closeReceiptDialog() {
        _showReceiptDialog.value = false
    }

    // Mini ATM Operations
    fun openAtmDialog(serviceType: String) {
        _activeAtmService.value = serviceType
        _atmPhone.value = ""
        _atmCustomer.value = ""
        _atmNominal.value = 0.0
        _atmAdminFee.value = 3000.0
        _atmSource.value = "DANA"
        _showAtmDialog.value = true
    }

    fun closeAtmDialog() {
        _showAtmDialog.value = false
    }

    fun setAtmNominal(amount: Double) {
        _atmNominal.value = amount
        // Dynamic flexible auto fee calculator
        _atmAdminFee.value = when {
            amount > 500000 -> 7000.0
            amount > 300000 -> 5000.0
            amount >= 100000 -> 3000.0
            amount > 0 -> 2000.0
            else -> 3000.0
        }
    }

    fun setAtmAdminFee(fee: Double) {
        _atmAdminFee.value = fee
    }

    fun setAtmPhone(phone: String) {
        _atmPhone.value = phone
    }

    fun setAtmCustomer(customer: String) {
        _atmCustomer.value = customer
    }

    fun setAtmSource(source: String) {
        _atmSource.value = source
    }

    fun submitAtmTransaction() {
        val phone = _atmPhone.value
        val nominal = _atmNominal.value
        val fee = _atmAdminFee.value

        if (phone.isBlank() || nominal <= 0) {
            showToast("Nomor tujuan dan nominal wajib diisi!", "⚠️")
            return
        }

        val atmId = "ATM-${System.currentTimeMillis().toString().takeLast(4)}"
        val totalCharged = nominal + fee

        val atmTrx = AtmTransactionEntity(
            id = atmId,
            serviceType = _activeAtmService.value,
            targetNumber = phone,
            customerName = _atmCustomer.value,
            nominalAmount = nominal,
            adminFee = fee,
            totalCharged = totalCharged,
            sourceAccount = _atmSource.value,
            timeFormatted = Formatters.formatFullDateTime(),
            status = "Berhasil"
        )

        viewModelScope.launch {
            repository.insertAtmTransaction(atmTrx)
            _showAtmDialog.value = false
            showToast("Transaksi ${atmTrx.serviceType} Berhasil!", "🎉")
        }
    }

    // Debt / Kasbon Management (RECOMMENDED FEATURE)
    fun openAddDebtDialog(name: String = "", amount: Double = 0.0, notes: String = "") {
        _initialDebtName.value = name
        _initialDebtAmount.value = amount
        _initialDebtNotes.value = notes
        _showDebtDialog.value = true
    }

    fun closeDebtDialog() {
        _showDebtDialog.value = false
        _initialDebtName.value = ""
        _initialDebtAmount.value = 0.0
        _initialDebtNotes.value = ""
    }

    /**
     * Catat sisa uang kurang langsung ke kasbon dan selesaikan transaksi kasir.
     */
    fun transferShortageToKasbon(customerName: String, cashPaid: Double, shortageAmount: Double) {
        val cartItems = _cart.value
        if (cartItems.isEmpty()) return

        val subtotal = cartItems.sumOf { it.total }
        val grandTotal = (subtotal - _discount.value).coerceAtLeast(0.0)
        val totalProfit = cartItems.sumOf { it.profit } - _discount.value

        val validCustomerName = if (customerName.isNotBlank()) customerName.trim() else "Pelanggan Kasbon"
        val trxId = "TRX-${System.currentTimeMillis().toString().takeLast(6)}"
        val method = if (cashPaid > 0) "Tunai + Kasbon" else "Kasbon"

        val transaction = TransactionEntity(
            id = trxId,
            timeFormatted = Formatters.formatFullDateTime(),
            itemsJson = serializeCartItems(cartItems),
            subtotal = subtotal,
            discount = _discount.value,
            totalAmount = grandTotal,
            totalProfit = totalProfit.coerceAtLeast(0.0),
            cashGiven = cashPaid,
            change = 0.0,
            paymentMethod = method,
            customerName = validCustomerName,
            cashierName = _cashierName.value
        )

        viewModelScope.launch {
            // Deduct inventory stock for each product in cart
            cartItems.forEach { item ->
                repository.reduceProductStock(item.productId, item.qty)
            }
            repository.insertTransaction(transaction)

            // Record debt entity for the shortage amount
            val debtNotes = if (cashPaid > 0) {
                "Sisa Nota $trxId (Total: ${Formatters.formatRupiah(grandTotal)}, Tunai: ${Formatters.formatRupiah(cashPaid)})"
            } else {
                "Kasir Nota $trxId (${cartItems.size} item)"
            }

            val debt = DebtEntity(
                customerName = validCustomerName,
                totalDebt = shortageAmount,
                remainingDebt = shortageAmount,
                notes = debtNotes,
                dueDate = "1 Minggu",
                isPaid = false
            )
            repository.insertDebt(debt)

            _activeReceipt.value = transaction
            clearCart()
            _showPaymentDialog.value = false
            _showReceiptDialog.value = true
            showToast("Sisa ${Formatters.formatRupiah(shortageAmount)} dicatat ke Kasbon $validCustomerName!", "📝")
        }
    }

    fun saveNewDebt(name: String, phone: String, amount: Double, notes: String, dueDate: String) {
        if (name.isBlank() || amount <= 0) {
            showToast("Nama pelanggan dan nominal hutang wajib diisi!", "⚠️")
            return
        }
        val debt = DebtEntity(
            customerName = name,
            customerPhone = phone,
            totalDebt = amount,
            remainingDebt = amount,
            notes = notes,
            dueDate = dueDate,
            isPaid = false
        )
        viewModelScope.launch {
            repository.insertDebt(debt)
            _showDebtDialog.value = false
            showToast("Kasbon $name berhasil dicatat!", "📝")
        }
    }

    fun openPayDebtDialog(debt: DebtEntity) {
        _selectedDebtForPay.value = debt
        _showPayDebtDialog.value = true
    }

    fun closePayDebtDialog() {
        _showPayDebtDialog.value = false
        _selectedDebtForPay.value = null
    }

    fun payDebt(amount: Double) {
        val debt = _selectedDebtForPay.value ?: return
        if (amount <= 0) {
            showToast("Nominal bayar harus lebih dari 0!", "⚠️")
            return
        }
        viewModelScope.launch {
            repository.payDebtInstallment(debt.id, amount)
            _showPayDebtDialog.value = false
            _selectedDebtForPay.value = null
            showToast("Pembayaran kasbon dicatat!", "💵")
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt.id)
            showToast("Catatan kasbon dihapus", "🗑️")
        }
    }

    // Product / Inventory Operations
    fun openAddProductDialog() {
        _editingProduct.value = null
        _showProductDialog.value = true
    }

    fun openEditProductDialog(product: ProductEntity) {
        _editingProduct.value = product
        _showProductDialog.value = true
    }

    fun closeProductDialog() {
        _showProductDialog.value = false
        _editingProduct.value = null
    }

    fun saveProduct(
        name: String,
        category: String,
        price: Double,
        costPrice: Double,
        stock: Int,
        unit: String,
        barcode: String,
        isSeblak: Boolean
    ) {
        if (name.isBlank() || price <= 0) {
            showToast("Nama dan harga jual wajib diisi!", "⚠️")
            return
        }

        viewModelScope.launch {
            val currentEdit = _editingProduct.value
            val productToSave = if (currentEdit != null) {
                currentEdit.copy(
                    name = name,
                    category = category,
                    price = price,
                    costPrice = costPrice,
                    stock = stock,
                    unit = unit,
                    barcode = barcode,
                    isSeblak = isSeblak
                )
            } else {
                ProductEntity(
                    name = name,
                    category = category,
                    price = price,
                    costPrice = costPrice,
                    stock = stock,
                    unit = unit,
                    barcode = barcode.ifBlank { "BRD-${(products.value.size + 1)}" },
                    isSeblak = isSeblak
                )
            }

            if (currentEdit != null) {
                repository.updateProduct(productToSave)
                showToast("Produk diperbarui!", "✏️")
            } else {
                repository.insertProduct(productToSave)
                showToast("Produk baru ditambahkan!", "📦")
            }
            _showProductDialog.value = false
            _editingProduct.value = null
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product.id)
            showToast("Produk ${product.name} dihapus", "🗑️")
        }
    }

    fun onBarcodeScanned(code: String) {
        val clean = code.trim()
        val found = products.value.find { it.barcode.equals(clean, ignoreCase = true) || it.id.toString() == clean }
        if (found != null) {
            handleProductClick(found)
        } else {
            showToast("Barcode \"$clean\" tidak ditemukan!", "❌")
        }
    }

    // Reports Operations
    fun clearReports() {
        viewModelScope.launch {
            repository.clearTransactions()
            repository.clearAtmTransactions()
            showToast("Seluruh riwayat laporan berhasil dibersihkan!", "🗑️")
        }
    }

    // Settings
    fun saveStoreSettings(name: String, cashier: String, address: String) {
        _storeName.value = name
        _cashierName.value = cashier
        _storeAddress.value = address
        prefs.edit()
            .putString("store_name", name)
            .putString("cashier_name", cashier)
            .putString("store_address", address)
            .apply()
        showToast("Profil toko berhasil disimpan!", "🎉")
    }

    fun setThemePreset(preset: AppThemePreset) {
        _selectedTheme.value = preset
        prefs.edit().putString("theme_id", preset.id).apply()
        showToast("Tema diubah ke ${preset.title}", preset.emoji)
    }

    fun setStoreEmoji(emoji: String) {
        _storeEmoji.value = emoji
        prefs.edit().putString("store_emoji", emoji).apply()
        showToast("Ikon toko diubah", emoji)
    }

    // Struk share via WhatsApp
    fun shareReceiptToWhatsApp(context: Context, trx: TransactionEntity) {
        val rawText = generateReceiptRawText(trx)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, rawText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Kirim Struk via WhatsApp")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            showToast("Gagal membagikan struk", "⚠️")
        }
    }

    // External Thermal & POS Printing
    fun printReceiptExternal(context: Context, trx: TransactionEntity) {
        val items = deserializeCartItems(trx.itemsJson)
        ReceiptPrinterHelper.printReceipt(
            context = context,
            storeName = _storeName.value,
            storeAddress = _storeAddress.value,
            storeEmoji = _storeEmoji.value,
            cashierName = _cashierName.value,
            trx = trx,
            items = items
        )
        showToast("Membuka dialog cetak struk...", "🖨️")
    }

    fun printAtmReceiptExternal(context: Context, atm: AtmTransactionEntity) {
        ReceiptPrinterHelper.printAtmReceipt(
            context = context,
            storeName = _storeName.value,
            storeAddress = _storeAddress.value,
            storeEmoji = _storeEmoji.value,
            cashierName = _cashierName.value,
            atm = atm
        )
        showToast("Membuka dialog cetak bukti ATM...", "🖨️")
    }

    fun sendReceiptToThermalBluetoothApp(context: Context, trx: TransactionEntity) {
        val rawText = generateReceiptRawText(trx)
        ReceiptPrinterHelper.sendToThermalApp(context, rawText, "Kirim ke Printer Thermal / RawBT")
    }

    fun sendAtmReceiptToThermalBluetoothApp(context: Context, atm: AtmTransactionEntity) {
        val rawText = generateAtmReceiptRawText(atm)
        ReceiptPrinterHelper.sendToThermalApp(context, rawText, "Kirim Bukti ATM ke Printer Thermal")
    }

    fun generateReceiptRawText(trx: TransactionEntity): String {
        val items = deserializeCartItems(trx.itemsJson)
        val sb = StringBuilder()
        sb.appendLine("🧾 *STRUK BELANJA* 🧾")
        sb.appendLine("*${_storeEmoji.value} ${_storeName.value}*")
        sb.appendLine(_storeAddress.value)
        sb.appendLine("--------------------------------")
        sb.appendLine("No. Trx: ${trx.id}")
        sb.appendLine("Waktu  : ${trx.timeFormatted}")
        sb.appendLine("Kasir  : ${trx.cashierName.ifBlank { _cashierName.value }}")
        if (trx.customerName.isNotBlank() && trx.customerName != "Pelanggan Umum") {
            sb.appendLine("Pelanggan: ${trx.customerName}")
        }
        sb.appendLine("--------------------------------")
        items.forEach { item ->
            sb.appendLine(item.displayName)
            sb.appendLine("  ${item.qty} x ${Formatters.formatRupiah(item.price)} = ${Formatters.formatRupiah(item.total)}")
        }
        sb.appendLine("--------------------------------")
        sb.appendLine("Subtotal: ${Formatters.formatRupiah(trx.subtotal)}")
        if (trx.discount > 0) {
            sb.appendLine("Diskon  : -${Formatters.formatRupiah(trx.discount)}")
        }
        sb.appendLine("*TOTAL   : ${Formatters.formatRupiah(trx.totalAmount)}*")
        sb.appendLine("Metode  : ${trx.paymentMethod}")
        if (trx.paymentMethod == "Tunai") {
            sb.appendLine("Bayar   : ${Formatters.formatRupiah(trx.cashGiven)}")
            sb.appendLine("Kembali : ${Formatters.formatRupiah(trx.change)}")
        } else if (trx.paymentMethod == "Tunai + Kasbon") {
            sb.appendLine("Bayar Tunai : ${Formatters.formatRupiah(trx.cashGiven)}")
            sb.appendLine("Sisa Kasbon : ${Formatters.formatRupiah(trx.totalAmount - trx.cashGiven)}")
        } else if (trx.paymentMethod == "Kasbon") {
            sb.appendLine("Kasbon : ${Formatters.formatRupiah(trx.totalAmount)}")
        }
        sb.appendLine("================================")
        sb.appendLine("Terima Kasih Telah Berbelanja! 🙏")
        return sb.toString()
    }

    fun generateAtmReceiptRawText(atm: AtmTransactionEntity): String {
        val sb = StringBuilder()
        sb.appendLine("💳 *BUKTI TRANSAKSI MINI ATM* 💳")
        sb.appendLine("*${_storeEmoji.value} ${_storeName.value}*")
        sb.appendLine(_storeAddress.value)
        sb.appendLine("--------------------------------")
        sb.appendLine("No. Ref: ATM-${atm.id}")
        sb.appendLine("Waktu  : ${atm.timeFormatted}")
        sb.appendLine("Layanan: ${atm.serviceType}")
        sb.appendLine("Tujuan : ${atm.targetNumber}")
        sb.appendLine("Sumber : ${atm.sourceAccount}")
        sb.appendLine("--------------------------------")
        sb.appendLine("Nominal: ${Formatters.formatRupiah(atm.nominalAmount)}")
        sb.appendLine("Admin  : ${Formatters.formatRupiah(atm.adminFee)}")
        sb.appendLine("--------------------------------")
        sb.appendLine("*TOTAL  : ${Formatters.formatRupiah(atm.totalCharged)}*")
        sb.appendLine("Status : BERHASIL")
        sb.appendLine("Kasir  : ${_cashierName.value}")
        sb.appendLine("================================")
        sb.appendLine("Simpan struk ini sebagai bukti sah. 🙏")
        return sb.toString()
    }

    // Share Debt Reminder to WhatsApp (RECOMMENDED FEATURE)
    fun shareDebtReminderToWhatsApp(context: Context, debt: DebtEntity) {
        val sb = StringBuilder()
        sb.appendLine("📢 *PEMBERITAHUAN KASBON WARUNG*")
        sb.appendLine("*${_storeEmoji.value} ${_storeName.value}*")
        sb.appendLine("--------------------------------")
        sb.appendLine("Halo Kak *${debt.customerName}*,")
        sb.appendLine("Catatan kasbon belanja di warung:")
        sb.appendLine("• Rincian : ${debt.notes.ifBlank { "Belanjaan Warung" }}")
        sb.appendLine("• Total   : ${Formatters.formatRupiah(debt.totalDebt)}")
        sb.appendLine("• Sisa    : *${Formatters.formatRupiah(debt.remainingDebt)}*")
        if (debt.dueDate.isNotBlank()) {
            sb.appendLine("• Tempo   : ${debt.dueDate}")
        }
        sb.appendLine("--------------------------------")
        sb.appendLine("Mohon dapat dilunasi saat luang ya kak. Terima kasih banyak! 🙏")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Kirim Pengingat Kasbon")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            showToast("Gagal membagikan pengingat", "⚠️")
        }
    }

    // Helpers for simple serialization without heavy JSON libs
    private fun serializeCartItems(items: List<CartItem>): String {
        return items.joinToString("###") { item ->
            "${item.productId}||${escape(item.name)}||${escape(item.category)}||${item.price}||${item.costPrice}||${escape(item.spicyLevel)}||${escape(item.notes)}||${item.qty}"
        }
    }

    fun deserializeCartItems(raw: String): List<CartItem> {
        if (raw.isBlank()) return emptyList()
        return raw.split("###").mapNotNull { chunk ->
            val parts = chunk.split("||")
            if (parts.size >= 8) {
                CartItem(
                    productId = parts[0].toLongOrNull() ?: 0L,
                    name = unescape(parts[1]),
                    category = unescape(parts[2]),
                    price = parts[3].toDoubleOrNull() ?: 0.0,
                    costPrice = parts[4].toDoubleOrNull() ?: 0.0,
                    spicyLevel = unescape(parts[5]),
                    notes = unescape(parts[6]),
                    qty = parts[7].toIntOrNull() ?: 1
                )
            } else null
        }
    }

    private fun escape(str: String): String = str.replace("|", "%7C").replace("#", "%23")
    private fun unescape(str: String): String = str.replace("%7C", "|").replace("%23", "#")
}
