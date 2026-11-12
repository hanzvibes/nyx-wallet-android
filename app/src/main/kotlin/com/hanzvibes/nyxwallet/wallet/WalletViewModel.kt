package com.hanzvibes.nyxwallet.wallet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hanzvibes.nyxwallet.chain.Chains
import com.hanzvibes.nyxwallet.chain.EvmChain
import com.hanzvibes.nyxwallet.data.EvmService
import com.hanzvibes.nyxwallet.data.LocalTx
import com.hanzvibes.nyxwallet.data.LocalTxLog
import com.hanzvibes.nyxwallet.data.PriceService
import com.hanzvibes.nyxwallet.security.SecureSeedStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

data class AssetRow(
    val chain: EvmChain,
    val balance: BigDecimal,
    val priceUsd: BigDecimal?,
) {
    val valueUsd: BigDecimal? get() = priceUsd?.let { balance.multiply(it) }
}

data class WalletUiState(
    val hasWallet: Boolean = false,
    val address: String = "",
    val testnetMode: Boolean = true,
    val assets: List<AssetRow> = emptyList(),
    val totalUsd: BigDecimal = BigDecimal.ZERO,
    val loading: Boolean = false,
    val error: String? = null,
    val lastTxHash: String? = null,
    val sentTxs: List<LocalTx> = emptyList(),
)

class WalletViewModel(app: Application) : AndroidViewModel(app) {
    private val seedStore = SecureSeedStore(app)
    private val txLog = LocalTxLog(app)
    private val evm = EvmService()
    private val prices = PriceService()
    private val prefs = app.getSharedPreferences("nyx_settings", android.content.Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(WalletUiState())
    val state: StateFlow<WalletUiState> = _state.asStateFlow()

    init {
        val testnet = prefs.getBoolean("testnet_mode", true)
        val mnemonic = seedStore.loadMnemonic()
        if (mnemonic != null) {
            _state.update {
                it.copy(
                    hasWallet = true,
                    address = WalletCrypto.evmAddress(mnemonic),
                    testnetMode = testnet,
                    sentTxs = txLog.all(),
                )
            }
            refresh()
        } else {
            _state.update { it.copy(testnetMode = testnet) }
        }
    }

    fun createWallet(): List<String> {
        val mnemonic = WalletCrypto.generateMnemonic()
        seedStore.saveMnemonic(mnemonic)
        _state.update {
            it.copy(
                hasWallet = true,
                address = WalletCrypto.evmAddress(mnemonic),
                sentTxs = txLog.all(),
            )
        }
        refresh()
        return mnemonic.trim().split(Regex("\\s+"))
    }

    fun importWallet(mnemonic: String): Boolean {
        if (!WalletCrypto.isValidMnemonic(mnemonic)) return false
        seedStore.saveMnemonic(mnemonic.trim())
        _state.update {
            it.copy(
                hasWallet = true,
                address = WalletCrypto.evmAddress(mnemonic),
                sentTxs = txLog.all(),
            )
        }
        refresh()
        return true
    }

    fun setTestnetMode(enabled: Boolean) {
        prefs.edit().putBoolean("testnet_mode", enabled).apply()
        _state.update { it.copy(testnetMode = enabled, assets = emptyList()) }
        refresh()
    }

    fun refresh() {
        val mnemonic = seedStore.loadMnemonic() ?: return
        val address = WalletCrypto.evmAddress(mnemonic)
        val testnet = _state.value.testnetMode
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val chains = Chains.activeChains(testnet)
                val priceMap = withContext(Dispatchers.IO) {
                    prices.prices(chains.map { it.coingeckoId }.distinct())
                }
                val rows = withContext(Dispatchers.IO) {
                    chains.map { chain ->
                        val bal = runCatching { evm.balance(chain, address) }
                            .getOrDefault(BigDecimal.ZERO)
                        AssetRow(chain, bal, priceMap[chain.coingeckoId])
                    }
                }
                val total = rows.fold(BigDecimal.ZERO) { acc, r -> acc + (r.valueUsd ?: BigDecimal.ZERO) }
                _state.update {
                    it.copy(assets = rows, totalUsd = total, loading = false, sentTxs = txLog.all())
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "Gagal memuat saldo") }
            }
        }
    }

    fun send(chain: EvmChain, to: String, amount: BigDecimal, onDone: (Boolean, String?) -> Unit) {
        val mnemonic = seedStore.loadMnemonic()
        if (mnemonic == null) { onDone(false, "Wallet belum dibuat"); return }
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { evm.send(chain, mnemonic, to.trim(), amount) }
                txLog.add(
                    LocalTx(
                        hash = result.hash,
                        chainId = chain.id,
                        to = to.trim(),
                        amount = amount.toPlainString(),
                        symbol = chain.symbol,
                        timestampMs = System.currentTimeMillis(),
                    ),
                )
                _state.update { it.copy(lastTxHash = result.hash, sentTxs = txLog.all()) }
                refresh()
                onDone(true, result.hash)
            } catch (e: Exception) {
                onDone(false, e.message ?: "Gagal mengirim")
            }
        }
    }

    fun revealMnemonic(): String? = seedStore.loadMnemonic()

    fun deleteWallet() {
        seedStore.clear()
        _state.update { WalletUiState(testnetMode = it.testnetMode) }
    }
}
