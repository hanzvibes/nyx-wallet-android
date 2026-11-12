package com.hanzvibes.nyxwallet.ui.send

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hanzvibes.nyxwallet.chain.Chains
import com.hanzvibes.nyxwallet.ui.theme.NyxColors
import com.hanzvibes.nyxwallet.wallet.WalletViewModel
import java.math.BigDecimal

@Composable
fun SendScreen(chainId: String, vm: WalletViewModel, onBack: () -> Unit) {
    val state by vm.state.collectAsState()
    val chain = Chains.byId(chainId) ?: Chains.activeChains(state.testnetMode).first()
    var to by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Kirim ${chain.symbol}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NyxColors.Text)
        Text("${chain.name} — ${if (chain.isTestnet) "testnet" else "mainnet"}", color = NyxColors.TextDim)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = to, onValueChange = { to = it; result = null },
            label = { Text("Alamat tujuan (0x…)") }, modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = amountText, onValueChange = { amountText = it; result = null },
            label = { Text("Jumlah ${chain.symbol}") }, modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { confirm = true },
            enabled = to.trim().startsWith("0x") && to.trim().length == 42 &&
                amountText.toBigDecimalOrNull()?.let { it > BigDecimal.ZERO } == true,
            modifier = Modifier.fillMaxWidth(),
        ) { Text(if (sending) "Mengirim…" else "Lanjut") }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Kembali") }
        result?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = if (it.startsWith("Tx:")) NyxColors.Green else NyxColors.Red)
        }
    }

    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text("Konfirmasi kirim") },
            text = { Text("Kirim $amountText ${chain.symbol} ke $to di ${chain.name}? Transaksi crypto tidak bisa dibatalkan.") },
            confirmButton = {
                TextButton(onClick = {
                    confirm = false
                    sending = true
                    vm.send(chain, to, amountText.toBigDecimal()) { ok, msg ->
                        sending = false
                        result = if (ok) "Tx: $msg" else "Gagal: $msg"
                    }
                }) { Text("Kirim") }
            },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text("Batal") } },
        )
    }
}
