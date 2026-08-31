package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    private val localeId = Locale("id", "ID")

    fun formatRupiah(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(localeId)
        return "Rp " + formatter.format(amount.toLong())
    }

    fun parseRupiah(input: String): Double {
        val clean = input.replace("[^0-9]".toRegex(), "")
        return clean.toDoubleOrNull() ?: 0.0
    }

    fun formatCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm", localeId)
        return sdf.format(Date())
    }

    fun formatFullDateTime(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", localeId)
        return sdf.format(Date())
    }
}
