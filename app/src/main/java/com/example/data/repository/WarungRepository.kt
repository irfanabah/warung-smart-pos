package com.example.data.repository

import com.example.data.db.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class WarungRepository(
    private val productDao: ProductDao,
    private val transactionDao: TransactionDao,
    private val atmTransactionDao: AtmTransactionDao,
    private val debtDao: DebtDao,
    private val heldCartDao: HeldCartDao
) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allAtmTransactions: Flow<List<AtmTransactionEntity>> = atmTransactionDao.getAllAtmTransactions()
    val allDebts: Flow<List<DebtEntity>> = debtDao.getAllDebts()
    val allHeldCarts: Flow<List<HeldCartEntity>> = heldCartDao.getAllHeldCarts()

    suspend fun getProductById(id: Long) = productDao.getProductById(id)
    suspend fun getProductByBarcode(barcode: String) = productDao.getProductByBarcode(barcode)
    suspend fun insertProduct(product: ProductEntity) = productDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)
    suspend fun deleteProduct(id: Long) = productDao.deleteProductById(id)
    suspend fun reduceProductStock(id: Long, quantity: Int) = productDao.reduceStock(id, quantity)

    suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insertTransaction(transaction)
    suspend fun clearTransactions() = transactionDao.clearAll()

    suspend fun insertAtmTransaction(atmTransaction: AtmTransactionEntity) = atmTransactionDao.insertAtmTransaction(atmTransaction)
    suspend fun clearAtmTransactions() = atmTransactionDao.clearAll()

    suspend fun insertDebt(debt: DebtEntity) = debtDao.insertDebt(debt)
    suspend fun updateDebt(debt: DebtEntity) = debtDao.updateDebt(debt)
    suspend fun payDebtInstallment(id: Long, amount: Double) = debtDao.payDebtInstallment(id, amount)
    suspend fun deleteDebt(id: Long) = debtDao.deleteDebtById(id)

    suspend fun insertHeldCart(cart: HeldCartEntity) = heldCartDao.insertHeldCart(cart)
    suspend fun deleteHeldCart(id: Long) = heldCartDao.deleteHeldCartById(id)
}
