package com.hanzvibes.nyxwallet.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class LocalTx(
    val hash: String,
    val chainId: String,
    val to: String,
    val amount: String,
    val symbol: String,
    val timestampMs: Long,
)

/** Sent transactions, kept on-device only so the user can see what they sent. */
class LocalTxLog(context: Context) {
    private val prefs = context.getSharedPreferences("nyx_tx_log", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun all(): List<LocalTx> {
        val raw = prefs.getString(KEY, null) ?: return emptyList()
        return try { json.decodeFromString<List<LocalTx>>(raw) } catch (_: Exception) { emptyList() }
    }

    fun add(tx: LocalTx) {
        val updated = (listOf(tx) + all()).take(50)
        prefs.edit().putString(KEY, json.encodeToString(updated)).apply()
    }

    companion object { private const val KEY = "txs_v1" }
}
