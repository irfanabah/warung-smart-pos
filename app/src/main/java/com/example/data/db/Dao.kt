package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET stock = MAX(0, stock - :quantity) WHERE id = :id")
    suspend fun reduceStock(id: Long, quantity: Int)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}

@Dao
interface AtmTransactionDao {
    @Query("SELECT * FROM atm_transactions ORDER BY timestamp DESC")
    fun getAllAtmTransactions(): Flow<List<AtmTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAtmTransaction(atmTransaction: AtmTransactionEntity)

    @Query("DELETE FROM atm_transactions")
    suspend fun clearAll()
}

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts ORDER BY isPaid ASC, createdAt DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity): Long

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Query("UPDATE debts SET remainingDebt = MAX(0.0, remainingDebt - :payAmount), isPaid = CASE WHEN (remainingDebt - :payAmount) <= 0 THEN 1 ELSE 0 END WHERE id = :id")
    suspend fun payDebtInstallment(id: Long, payAmount: Double)

    @Query("DELETE FROM debts WHERE id = :id")
    suspend fun deleteDebtById(id: Long)
}

@Dao
interface HeldCartDao {
    @Query("SELECT * FROM held_carts ORDER BY timestamp DESC")
    fun getAllHeldCarts(): Flow<List<HeldCartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeldCart(cart: HeldCartEntity): Long

    @Query("DELETE FROM held_carts WHERE id = :id")
    suspend fun deleteHeldCartById(id: Long)
}
