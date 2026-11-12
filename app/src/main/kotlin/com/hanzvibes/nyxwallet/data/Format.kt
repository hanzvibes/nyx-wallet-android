package com.hanzvibes.nyxwallet.data

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

object Format {
    /** 0.123456 ETH -> "0.1235". Small dust stays readable, big values stay short. */
    fun amount(value: BigDecimal, maxDecimals: Int = 4): String {
        val scaled = value.setScale(maxDecimals, RoundingMode.HALF_UP).stripTrailingZeros()
        return scaled.toPlainString()
    }

    fun usd(value: BigDecimal): String =
        NumberFormat.getCurrencyInstance(Locale.US).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }.format(value)

    fun shortAddress(address: String): String =
        if (address.length > 13) "${address.take(6)}…${address.takeLast(4)}" else address

    fun explorerTxUrl(explorerUrl: String, txHash: String): String =
        "$explorerUrl/tx/$txHash"
}
