package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val price: Double,
    val costPrice: Double = 0.0, // Harga modal / Beli (untuk hitung laba bersih)
    val stock: Int,
    val unit: String = "pcs",
    val barcode: String = "",
    val isSeblak: Boolean = false
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String, // e.g. TRX-1092
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String,
    val itemsJson: String, // serialized cart items
    val subtotal: Double,
    val discount: Double = 0.0,
    val totalAmount: Double,
    val totalProfit: Double = 0.0,
    val cashGiven: Double,
    val change: Double,
    val paymentMethod: String = "Tunai", // Tunai, QRIS, Transfer, Kasbon
    val customerName: String = "",
    val cashierName: String = "Budi"
)

@Entity(tableName = "atm_transactions")
data class AtmTransactionEntity(
    @PrimaryKey
    val id: String, // e.g. ATM-8821
    val serviceType: String, // Top-Up DANA, ShopeePay, SeaBank, Token PLN, Pulsa
    val targetNumber: String, // No HP / No Rekening / No Meteran
    val customerName: String = "",
    val nominalAmount: Double,
    val adminFee: Double,
    val totalCharged: Double,
    val sourceAccount: String = "DANA", // DANA, ShopeePay, SeaBank
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String,
    val status: String = "Berhasil"
)

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val customerPhone: String = "",
    val totalDebt: Double,
    val remainingDebt: Double,
    val notes: String = "",
    val dueDate: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isPaid: Boolean = false
)

@Entity(tableName = "held_carts")
data class HeldCartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val label: String, // Meja 1, Ibu Siti, dsb
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String,
    val itemsJson: String,
    val discount: Double = 0.0
)

data class CartItem(
    val productId: Long,
    val name: String,
    val category: String,
    val price: Double,
    val costPrice: Double,
    val spicyLevel: String = "",
    val notes: String = "",
    val qty: Int = 1
) {
    val total: Double get() = price * qty
    val profit: Double get() = (price - costPrice) * qty
    val displayName: String
        get() = buildString {
            append(name)
            val details = mutableListOf<String>()
            if (spicyLevel.isNotEmpty() && spicyLevel != "Tidak Pedas") {
                details.add(spicyLevel)
            }
            if (notes.isNotEmpty()) {
                details.add(notes)
            }
            if (details.isNotEmpty()) {
                append(" (${details.joinToString(", ")})")
            }
        }
}
