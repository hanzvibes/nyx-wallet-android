package com.hanzvibes.nyxwallet.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

/** USD prices from CoinGecko's free simple-price endpoint (no API key). */
class PriceService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build(),
) {
    private val json = Json { ignoreUnknownKeys = true }

    /** Returns map coingeckoId -> USD price. Missing ids are simply absent. */
    fun prices(ids: List<String>): Map<String, BigDecimal> {
        if (ids.isEmpty()) return emptyMap()
        val url = "https://api.coingecko.com/api/v3/simple/price" +
            "?ids=${ids.joinToString(",")}&vs_currencies=usd"
        val req = Request.Builder().url(url).get().build()
        return client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return emptyMap()
            val body = resp.body?.string() ?: return emptyMap()
            val root = json.parseToJsonElement(body).jsonObject
            root.entries.mapNotNull { (id, el) ->
                val usd = el.jsonObject["usd"]?.jsonPrimitive?.doubleOrNull ?: return@mapNotNull null
                id to BigDecimal.valueOf(usd)
            }.toMap()
        }
    }
}
