package com.hanzvibes.nyxwallet.data

import com.hanzvibes.nyxwallet.chain.EvmChain
import com.hanzvibes.nyxwallet.wallet.WalletCrypto
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.utils.Convert
import java.math.BigDecimal

data class TxResult(val hash: String)

class EvmService {
    private fun web3j(chain: EvmChain): Web3j =
        Web3j.build(HttpService(chain.rpcUrl))

    fun balance(chain: EvmChain, address: String): BigDecimal {
        val web3j = web3j(chain)
        return try {
            val ethBalance = web3j
                .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send()
            Convert.fromWei(ethBalance.balance.toString(), Convert.Unit.ETHER)
        } finally {
            web3j.shutdown()
        }
    }

    /**
     * Sends native currency (ETH / BNB / MATIC) and returns the tx hash.
     * Caller must have shown the user the amount, destination and chain
     * and received an explicit confirmation first.
     */
    fun send(chain: EvmChain, mnemonic: String, to: String, amount: BigDecimal): TxResult {
        val web3j = web3j(chain)
        try {
            val credentials = WalletCrypto.evmCredentials(mnemonic)
            val nonce = web3j.ethGetTransactionCount(
                credentials.address, DefaultBlockParameterName.LATEST,
            ).send().transactionCount
            val gasPrice = web3j.ethGasPrice().send().gasPrice
            val gasLimit = DefaultGasProvider.GAS_LIMIT
            val valueWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigIntegerExact()
            val rawTx = org.web3j.crypto.RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, to, valueWei,
            )
            val signed = org.web3j.crypto.TransactionEncoder.signMessage(
                rawTx, chain.chainId, credentials,
            )
            val hex = org.web3j.utils.Numeric.toHexString(signed)
            val resp: EthSendTransaction = web3j.ethSendRawTransaction(hex).send()
            if (resp.hasError()) error("Send failed: ${resp.error.message}")
            return TxResult(resp.transactionHash)
        } finally {
            web3j.shutdown()
        }
    }
}
