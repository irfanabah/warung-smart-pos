package com.example.util

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.data.model.AtmTransactionEntity
import com.example.data.model.CartItem
import com.example.data.model.TransactionEntity

object ReceiptPrinterHelper {

    /**
     * Cetak Struk POS ke printer eksternal (Bluetooth, WiFi, USB, atau PDF) menggunakan Android Print Service.
     */
    fun printReceipt(
        context: Context,
        storeName: String,
        storeAddress: String,
        storeEmoji: String,
        cashierName: String,
        trx: TransactionEntity,
        items: List<CartItem>
    ) {
        val htmlContent = generateReceiptThermalHtml(
            storeName = storeName,
            storeAddress = storeAddress,
            storeEmoji = storeEmoji,
            cashierName = cashierName,
            trx = trx,
            items = items
        )

        doPrintHtml(context, "Struk_${trx.id}", htmlContent)
    }

    /**
     * Cetak Bukti Transaksi Mini ATM / PPOB ke printer eksternal.
     */
    fun printAtmReceipt(
        context: Context,
        storeName: String,
        storeAddress: String,
        storeEmoji: String,
        cashierName: String,
        atm: AtmTransactionEntity
    ) {
        val htmlContent = generateAtmThermalHtml(
            storeName = storeName,
            storeAddress = storeAddress,
            storeEmoji = storeEmoji,
            cashierName = cashierName,
            atm = atm
        )

        doPrintHtml(context, "Struk_ATM_${atm.id}", htmlContent)
    }

    /**
     * Kirim teks struk raw ke aplikasi printer thermal bluetooth eksternal (seperti RawBT, ESC POS Bluetooth Print).
     */
    fun sendToThermalApp(context: Context, rawText: String, title: String = "Cetak Struk Thermal") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, rawText)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(sendIntent, title)
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak ada aplikasi printer thermal ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun doPrintHtml(context: Context, jobName: String, htmlContent: String) {
        try {
            val webView = WebView(context)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                    if (printManager == null) {
                        Toast.makeText(context, "Layanan Cetak tidak tersedia di perangkat ini", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val printAdapter = webView.createPrintDocumentAdapter(jobName)
                    val attributes = PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A6)
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                        .build()

                    printManager.print(jobName, printAdapter, attributes)
                }
            }
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal memulai cetak: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Template HTML Struk format kertas struk thermal 58mm / 80mm.
     */
    fun generateReceiptThermalHtml(
        storeName: String,
        storeAddress: String,
        storeEmoji: String,
        cashierName: String,
        trx: TransactionEntity,
        items: List<CartItem>
    ): String {
        val itemRows = StringBuilder()
        items.forEach { item ->
            itemRows.append("""
                <tr>
                    <td colspan="2" style="font-weight: bold; padding-top: 4px;">${escapeHtml(item.displayName)}</td>
                </tr>
                <tr>
                    <td style="color: #444; font-size: 11px; padding-bottom: 4px;">${item.qty} x ${Formatters.formatRupiah(item.price)}</td>
                    <td style="text-align: right; font-weight: bold; padding-bottom: 4px;">${Formatters.formatRupiah(item.total)}</td>
                </tr>
            """.trimIndent())
        }

        val discountRow = if (trx.discount > 0) {
            """
            <tr>
                <td style="padding: 2px 0;">Diskon:</td>
                <td style="text-align: right; color: #d9534f;">-${Formatters.formatRupiah(trx.discount)}</td>
            </tr>
            """.trimIndent()
        } else ""

        val paymentDetails = when (trx.paymentMethod) {
            "Tunai" -> """
                <tr>
                    <td style="padding: 2px 0;">Bayar Tunai:</td>
                    <td style="text-align: right;">${Formatters.formatRupiah(trx.cashGiven)}</td>
                </tr>
                <tr>
                    <td style="padding: 2px 0; font-weight: bold;">Kembalian:</td>
                    <td style="text-align: right; font-weight: bold;">${Formatters.formatRupiah(trx.change)}</td>
                </tr>
            """.trimIndent()
            "Tunai + Kasbon" -> """
                <tr>
                    <td style="padding: 2px 0;">Bayar Tunai:</td>
                    <td style="text-align: right;">${Formatters.formatRupiah(trx.cashGiven)}</td>
                </tr>
                <tr>
                    <td style="padding: 2px 0; font-weight: bold; color: #d9534f;">Sisa Kasbon:</td>
                    <td style="text-align: right; font-weight: bold; color: #d9534f;">${Formatters.formatRupiah(trx.totalAmount - trx.cashGiven)}</td>
                </tr>
            """.trimIndent()
            "Kasbon" -> """
                <tr>
                    <td style="padding: 2px 0; font-weight: bold; color: #d9534f;">Dicatat Kasbon:</td>
                    <td style="text-align: right; font-weight: bold; color: #d9534f;">${Formatters.formatRupiah(trx.totalAmount)}</td>
                </tr>
            """.trimIndent()
            else -> ""
        }

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                @page {
                    margin: 0;
                    size: 58mm auto;
                }
                body {
                    font-family: 'Courier New', Courier, monospace, sans-serif;
                    font-size: 12px;
                    line-height: 1.3;
                    color: #000;
                    margin: 0;
                    padding: 8px 12px;
                    width: 100%;
                    max-width: 320px;
                    box-sizing: border-box;
                    background: #fff;
                }
                .header {
                    text-align: center;
                    margin-bottom: 8px;
                }
                .store-title {
                    font-size: 16px;
                    font-weight: 900;
                    margin: 0;
                }
                .store-address {
                    font-size: 11px;
                    color: #444;
                    margin-top: 2px;
                }
                .divider {
                    border-top: 1px dashed #000;
                    margin: 6px 0;
                }
                .meta-info {
                    font-size: 11px;
                    margin-bottom: 6px;
                }
                .meta-row {
                    display: flex;
                    justify-content: space-between;
                }
                table {
                    width: 100%;
                    border-collapse: collapse;
                }
                .total-row {
                    font-size: 14px;
                    font-weight: 900;
                }
                .footer {
                    text-align: center;
                    margin-top: 12px;
                    font-size: 11px;
                }
                .barcode-box {
                    text-align: center;
                    margin: 8px 0 4px;
                    letter-spacing: 2px;
                    font-size: 12px;
                    font-weight: bold;
                }
            </style>
        </head>
        <body>
            <div class="header">
                <div class="store-title">$storeEmoji ${escapeHtml(storeName)}</div>
                <div class="store-address">${escapeHtml(storeAddress)}</div>
            </div>

            <div class="divider"></div>

            <div class="meta-info">
                <div class="meta-row"><span>No. Nota :</span><span>${trx.id}</span></div>
                <div class="meta-row"><span>Waktu    :</span><span>${trx.timeFormatted}</span></div>
                <div class="meta-row"><span>Kasir    :</span><span>${escapeHtml(trx.cashierName.ifBlank { cashierName })}</span></div>
                ${if (trx.customerName.isNotBlank() && trx.customerName != "Pelanggan Umum") "<div class='meta-row'><span>Pelanggan:</span><span>${escapeHtml(trx.customerName)}</span></div>" else ""}
            </div>

            <div class="divider"></div>

            <table>
                <tbody>
                    $itemRows
                </tbody>
            </table>

            <div class="divider"></div>

            <table>
                <tbody>
                    <tr>
                        <td style="padding: 2px 0;">Subtotal:</td>
                        <td style="text-align: right;">${Formatters.formatRupiah(trx.subtotal)}</td>
                    </tr>
                    $discountRow
                    <tr class="total-row">
                        <td style="padding: 4px 0;">TOTAL:</td>
                        <td style="text-align: right; padding: 4px 0;">${Formatters.formatRupiah(trx.totalAmount)}</td>
                    </tr>
                    <tr>
                        <td style="padding: 2px 0;">Metode Bayar:</td>
                        <td style="text-align: right; font-weight: bold;">${trx.paymentMethod}</td>
                    </tr>
                    $paymentDetails
                </tbody>
            </table>

            <div class="divider"></div>

            <div class="footer">
                <p style="margin: 0; font-weight: bold;">*** TERIMA KASIH ***</p>
                <p style="margin: 3px 0 0; font-size: 10px;">Barang yang sudah dibeli tidak dapat ditukar/dikembalikan.</p>
                <div class="barcode-box">* ${trx.id} *</div>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    /**
     * Template HTML Struk Mini ATM / PPOB untuk printer thermal 58mm / 80mm.
     */
    fun generateAtmThermalHtml(
        storeName: String,
        storeAddress: String,
        storeEmoji: String,
        cashierName: String,
        atm: AtmTransactionEntity
    ): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                @page { margin: 0; size: 58mm auto; }
                body {
                    font-family: 'Courier New', Courier, monospace, sans-serif;
                    font-size: 12px;
                    line-height: 1.3;
                    color: #000;
                    margin: 0;
                    padding: 8px 12px;
                    max-width: 320px;
                    background: #fff;
                }
                .header { text-align: center; margin-bottom: 8px; }
                .store-title { font-size: 15px; font-weight: 900; }
                .service-title { font-size: 13px; font-weight: bold; margin: 4px 0; }
                .divider { border-top: 1px dashed #000; margin: 6px 0; }
                .row { display: flex; justify-content: space-between; margin: 3px 0; font-size: 11px; }
                .total { font-size: 14px; font-weight: 900; }
                .footer { text-align: center; margin-top: 10px; font-size: 10px; }
                .status-badge { text-align: center; font-weight: bold; font-size: 12px; margin: 6px 0; }
            </style>
        </head>
        <body>
            <div class="header">
                <div class="store-title">$storeEmoji ${escapeHtml(storeName)}</div>
                <div style="font-size: 10px;">${escapeHtml(storeAddress)}</div>
                <div class="divider"></div>
                <div class="service-title">BUKTI TRANSAKSI MINI ATM</div>
            </div>

            <div class="row"><span>No. Ref:</span><span>ATM-${atm.id}</span></div>
            <div class="row"><span>Waktu:</span><span>${atm.timeFormatted}</span></div>
            <div class="row"><span>Layanan:</span><span style="font-weight: bold;">${atm.serviceType}</span></div>
            <div class="row"><span>No. Rek/HP:</span><span style="font-weight: bold;">${atm.targetNumber}</span></div>
            <div class="row"><span>Sumber:</span><span>${atm.sourceAccount}</span></div>

            <div class="divider"></div>

            <div class="row"><span>Nominal Transaksi:</span><span>${Formatters.formatRupiah(atm.nominalAmount)}</span></div>
            <div class="row"><span>Biaya Admin:</span><span>${Formatters.formatRupiah(atm.adminFee)}</span></div>
            <div class="divider"></div>
            <div class="row total"><span>TOTAL BAYAR:</span><span>${Formatters.formatRupiah(atm.totalCharged)}</span></div>

            <div class="status-badge">--- TRANSAKSI BERHASIL ---</div>

            <div class="footer">
                <p style="margin: 0;">Simpan struk ini sebagai bukti pembayaran yang sah.</p>
                <p style="margin: 2px 0 0;">Kasir: ${escapeHtml(cashierName)}</p>
                <p style="margin: 4px 0 0; font-weight: bold;">Terima Kasih 🙏</p>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}
