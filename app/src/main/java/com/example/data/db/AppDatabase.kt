package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        TransactionEntity::class,
        AtmTransactionEntity::class,
        DebtEntity::class,
        HeldCartEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
    abstract fun atmTransactionDao(): AtmTransactionDao
    abstract fun debtDao(): DebtDao
    abstract fun heldCartDao(): HeldCartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "warung_smart_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            suspend fun populateInitialData(db: AppDatabase) {
                val productDao = db.productDao()
                val debtDao = db.debtDao()

                val defaultProducts = listOf(
                    ProductEntity(
                        id = 1,
                        name = "Beras Premium 5kg",
                        category = "Sembako",
                        price = 65000.0,
                        costPrice = 58000.0,
                        stock = 25,
                        unit = "sak",
                        barcode = "8991",
                        isSeblak = false
                    ),
                    ProductEntity(
                        id = 2,
                        name = "Minyak Goreng 1L",
                        category = "Sembako",
                        price = 18000.0,
                        costPrice = 15500.0,
                        stock = 40,
                        unit = "pcs",
                        barcode = "8992",
                        isSeblak = false
                    ),
                    ProductEntity(
                        id = 3,
                        name = "Gula Pasir 1kg",
                        category = "Sembako",
                        price = 16500.0,
                        costPrice = 14000.0,
                        stock = 3,
                        unit = "kg",
                        barcode = "8993",
                        isSeblak = false
                    ),
                    ProductEntity(
                        id = 4,
                        name = "Seblak Original",
                        category = "Seblak",
                        price = 10000.0,
                        costPrice = 5500.0,
                        stock = 100,
                        unit = "porsi",
                        barcode = "SEB1",
                        isSeblak = true
                    ),
                    ProductEntity(
                        id = 5,
                        name = "Seblak Komplit (Ceker + Sosis + Bakso)",
                        category = "Seblak",
                        price = 18000.0,
                        costPrice = 9500.0,
                        stock = 100,
                        unit = "porsi",
                        barcode = "SEB2",
                        isSeblak = true
                    ),
                    ProductEntity(
                        id = 6,
                        name = "Es Teh Manis",
                        category = "Minuman",
                        price = 5000.0,
                        costPrice = 1500.0,
                        stock = 50,
                        unit = "gelas",
                        barcode = "MNM1",
                        isSeblak = false
                    ),
                    ProductEntity(
                        id = 7,
                        name = "Kopi Hitam Tubruk",
                        category = "Minuman",
                        price = 4000.0,
                        costPrice = 1200.0,
                        stock = 35,
                        unit = "cangkir",
                        barcode = "MNM2",
                        isSeblak = false
                    ),
                    ProductEntity(
                        id = 8,
                        name = "Indomie Goreng Telur",
                        category = "Makanan",
                        price = 12000.0,
                        costPrice = 6500.0,
                        stock = 45,
                        unit = "porsi",
                        barcode = "MKN1",
                        isSeblak = false
                    )
                )
                productDao.insertAll(defaultProducts)

                // Sample Kasbon / Hutang for initial preview
                debtDao.insertDebt(
                    DebtEntity(
                        customerName = "Ibu Siti (Tetangga Depan)",
                        customerPhone = "081298765432",
                        totalDebt = 85000.0,
                        remainingDebt = 35000.0,
                        notes = "Beli Beras 5kg & Minyak Goreng, sudah cicil 50rb",
                        dueDate = "Sabtu Depan",
                        isPaid = false
                    )
                )
            }
        }
    }
}
