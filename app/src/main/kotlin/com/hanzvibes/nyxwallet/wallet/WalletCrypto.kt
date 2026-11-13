package com.hanzvibes.nyxwallet.wallet

import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.crypto.MnemonicUtils
import java.security.SecureRandom

/**
 * Pure BIP39 / BIP32 / BIP44 helpers for EVM chains.
 * No Android APIs here, so this is unit-testable on the JVM.
 *
 * Derivation: m/44'/60'/0'/0/0 — the same path Ethereum, BSC and Polygon
 * all use, so one seed gives one address across every EVM chain.
 */
object WalletCrypto {
    private val HARDENED = Bip32ECKeyPair.HARDENED_BIT

    fun generateMnemonic(): String {
        // web3j 4.12: generateMnemonic takes raw entropy bytes, not a
        // SecureRandom. 16 bytes = 128-bit entropy = 12 words.
        val entropy = ByteArray(16)
        SecureRandom().nextBytes(entropy)
        return MnemonicUtils.generateMnemonic(entropy)
    }

    fun isValidMnemonic(mnemonic: String): Boolean {
        val words = mnemonic.trim().split(Regex("\\s+"))
        if (words.size !in setOf(12, 15, 18, 21, 24)) return false
        // web3j 4.12: validateMnemonic returns Boolean (no exception) and
        // checks words + checksum — the return value must be used.
        return MnemonicUtils.validateMnemonic(mnemonic.trim())
    }

    /** EVM address (0x…) derived from a mnemonic. Same address on all EVM chains. */
    fun evmAddress(mnemonic: String): String {
        val seed = MnemonicUtils.generateSeed(mnemonic.trim(), "")
        val master = Bip32ECKeyPair.generateKeyPair(seed)
        val derived = Bip32ECKeyPair.deriveKeyPair(
            master,
            intArrayOf(44 or HARDENED, 60 or HARDENED, 0 or HARDENED, 0, 0),
        )
        // EIP-55 checksummed address (mixed case), not the raw lowercase
        // Credentials.address — matches the standard display form.
        return Keys.toChecksumAddress(Credentials.create(derived).address)
    }

    fun evmCredentials(mnemonic: String): Credentials {
        val seed = MnemonicUtils.generateSeed(mnemonic.trim(), "")
        val master = Bip32ECKeyPair.generateKeyPair(seed)
        val derived = Bip32ECKeyPair.deriveKeyPair(
            master,
            intArrayOf(44 or HARDENED, 60 or HARDENED, 0 or HARDENED, 0, 0),
        )
        return Credentials.create(derived)
    }
}
