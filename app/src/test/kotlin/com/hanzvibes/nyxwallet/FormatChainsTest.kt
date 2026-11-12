package com.hanzvibes.nyxwallet

import com.hanzvibes.nyxwallet.chain.Chains
import com.hanzvibes.nyxwallet.data.Format
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class FormatChainsTest {
    @Test
    fun `amount formatting trims trailing zeros`() {
        assertEquals("0.1235", Format.amount(BigDecimal("0.123456")))
        assertEquals("1.5", Format.amount(BigDecimal("1.5000")))
        assertEquals("0", Format.amount(BigDecimal.ZERO))
    }

    @Test
    fun `short address keeps start and end`() {
        assertEquals("0x9858…da94", Format.shortAddress("0x9858EfFD232B4033E47d90003D41EC34EcaEda94"))
    }

    @Test
    fun `usd formatting`() {
        assertEquals("$1,234.50", Format.usd(BigDecimal("1234.5")))
    }

    @Test
    fun `testnet mode returns only testnets`() {
        val testnets = Chains.activeChains(true)
        assertEquals(3, testnets.size)
        assertTrue(testnets.all { it.isTestnet })
        val mainnets = Chains.activeChains(false)
        assertEquals(3, mainnets.size)
        assertFalse(mainnets.any { it.isTestnet })
    }

    @Test
    fun `chain lookup by id`() {
        assertEquals("Ethereum", Chains.byId("eth")?.name)
        assertEquals(11155111L, Chains.byId("sepolia")?.chainId)
    }
}
