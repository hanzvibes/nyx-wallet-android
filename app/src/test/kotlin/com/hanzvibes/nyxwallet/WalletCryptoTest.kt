package com.hanzvibes.nyxwallet

import com.hanzvibes.nyxwallet.wallet.WalletCrypto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletCryptoTest {
    // Standard BIP39 test mnemonic. m/44'/60'/0'/0/0 must give this address.
    private val testMnemonic =
        "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"

    @Test
    fun `known mnemonic derives known ETH address`() {
        assertEquals(
            "0x9858EfFD232B4033E47d90003D41EC34EcaEda94",
            WalletCrypto.evmAddress(testMnemonic),
        )
    }

    @Test
    fun `generated mnemonic is 12 valid words`() {
        val mnemonic = WalletCrypto.generateMnemonic()
        assertEquals(12, mnemonic.trim().split(Regex("\\s+")).size)
        assertTrue(WalletCrypto.isValidMnemonic(mnemonic))
        val address = WalletCrypto.evmAddress(mnemonic)
        assertTrue(address.startsWith("0x"))
        assertEquals(42, address.length)
    }

    @Test
    fun `invalid mnemonics are rejected`() {
        assertFalse(WalletCrypto.isValidMnemonic("hello world"))
        assertFalse(WalletCrypto.isValidMnemonic("abandon abandon abandon"))
        assertFalse(WalletCrypto.isValidMnemonic(""))
        // Valid words but wrong checksum word.
        assertFalse(
            WalletCrypto.isValidMnemonic(
                "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon",
            ),
        )
    }
}
