package com.hanzvibes.nyxwallet.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hanzvibes.nyxwallet.ui.theme.NyxColors
import com.hanzvibes.nyxwallet.wallet.WalletUiState
import com.hanzvibes.nyxwallet.wallet.WalletViewModel

@Composable
fun SettingsScreen(
    state: WalletUiState,
    vm: WalletViewModel,
    onBack: () -> Unit,
    onWalletDeleted: () -> Unit,
) {
    var showSeed by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Pengaturan", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NyxColors.Text)
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Mode Testnet", color = NyxColors.Text, fontWeight = FontWeight.Bold)
                Text("Matiin ini = mainnet, dana beneran. Jangan dulu.", color = NyxColors.TextDim)
            }
            Switch(checked = state.testnetMode, onCheckedChange = { vm.setTestnetMode(it) })
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { showSeed = vm.revealMnemonic() },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Lihat Seed Phrase") }
        showSeed?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = NyxColors.Red)
            Text("Jangan pernah kirim kata-kata ini ke siapa pun, termasuk ke gue.", color = NyxColors.TextDim)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = { confirmDelete = true },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Hapus Wallet dari HP ini") }
        Spacer(Modifier.height(24.dp))
        Text("Nyx Wallet 0.1.0 — Batch 1 (EVM)", color = NyxColors.TextDim)
        Text("Bitcoin + Solana: Batch 2. WalletConnect + Swap: Batch 4.", color = NyxColors.TextDim)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Kembali") }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Hapus wallet?") },
            text = { Text("Seed phrase di HP ini akan dihapus. Pastikan kamu sudah mencatat 12 katanya, kalau tidak dananya hilang selamanya.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    vm.deleteWallet()
                    onWalletDeleted()
                }) { Text("Hapus", color = NyxColors.Red) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Batal") } },
        )
    }
}
