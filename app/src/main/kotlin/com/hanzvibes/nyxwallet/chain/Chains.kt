package com.hanzvibes.nyxwallet.chain

/** EVM chain definition. Batch 1: EVM only. BTC + Solana land in Batch 2. */
data class EvmChain(
    val id: String,
    val name: String,
    val symbol: String,
    val chainId: Long,
    val rpcUrl: String,
    val explorerUrl: String,
    val coingeckoId: String,
    val isTestnet: Boolean,
)

object Chains {
    val ETHEREUM = EvmChain("eth", "Ethereum", "ETH", 1, "https://eth.llamarpc.com", "https://etherscan.io", "ethereum", false)
    val SEPOLIA = EvmChain("sepolia", "Sepolia", "ETH", 11155111, "https://rpc.sepolia.org", "https://sepolia.etherscan.io", "ethereum", true)
    val BSC = EvmChain("bsc", "BNB Smart Chain", "BNB", 56, "https://bsc-dataseed.binance.org", "https://bscscan.com", "binancecoin", false)
    val BSC_TESTNET = EvmChain("bsc-testnet", "BSC Testnet", "BNB", 97, "https://data-seed-prebsc-1-s1.binance.org:8545", "https://testnet.bscscan.com", "binancecoin", true)
    val POLYGON = EvmChain("polygon", "Polygon", "MATIC", 137, "https://polygon-rpc.com", "https://polygonscan.com", "matic-network", false)
    val AMOY = EvmChain("amoy", "Polygon Amoy", "MATIC", 80002, "https://rpc-amoy.polygon.technology", "https://amoy.polygonscan.com", "matic-network", true)

    val mainnets = listOf(ETHEREUM, BSC, POLYGON)
    val testnets = listOf(SEPOLIA, BSC_TESTNET, AMOY)

    /** Default mode is testnet until Batch 5 hardening is done. */
    fun activeChains(testnetMode: Boolean): List<EvmChain> =
        if (testnetMode) testnets else mainnets

    fun byId(id: String): EvmChain? = (mainnets + testnets).firstOrNull { it.id == id }
}
