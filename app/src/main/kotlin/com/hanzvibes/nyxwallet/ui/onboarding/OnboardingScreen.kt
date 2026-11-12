package com.hanzvibes.nyxwallet.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hanzvibes.nyxwallet.ui.theme.NyxCardShape
import com.hanzvibes.nyxwallet.ui.theme.NyxColors
import com.hanzvibes.nyxwallet.wallet.WalletViewModel

@Composable
fun OnboardingScreen(onDone: () -> Unit, vm: WalletViewModel) {
    var words by remember { mutableStateOf<List<String>?>(null) }
    var importText by remember { mutableStateOf("") }
    var importError by remember { mutableStateOf<String?>(null) }
    var showImport by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Nyx Wallet", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = NyxColors.Text)
        Text(
            "Dompet crypto non-custodial. Kunci cuma ada di HP kamu.",
            color = NyxColors.TextDim,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Mode testnet aktif. Jangan pakai dana beneran dulu.",
            color = NyxColors.Amber,
        )
        Spacer(Modifier.height(32.dp))

        if (words == null && !showImport) {
            Button(
                onClick = { words = vm.createWallet() },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Buat Wallet Baru") }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showImport = true },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Import Wallet (12 kata)") }
        }

        if (words != null) {
            Text("Catat 12 kata ini. Siapa pun yang punya kata ini bisa ambil dana kamu.", color = NyxColors.Red)
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.height(220.dp)) {
                itemsIndexed(words!!) { i, w ->
                    Card(
                        shape = NyxCardShape,
                        colors = CardDefaults.cardColors(containerColor = NyxColors.Card),
                        modifier = Modifier.padding(4.dp),
                    ) {
                        Text("${i + 1}. $w", modifier = Modifier.padding(8.dp), color = NyxColors.Text)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text("Saya Sudah Mencatatnya")
            }
        }

        if (showImport && words == null) {
            OutlinedTextField(
                value = importText,
                onValueChange = { importText = it; importError = null },
                label = { Text("12 kata seed phrase") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
            )
            importError?.let { Text(it, color = NyxColors.Red) }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (vm.importWallet(importText)) onDone()
                    else importError = "Seed phrase tidak valid. Cek lagi 12 katanya."
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Import") }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { showImport = false }, modifier = Modifier.fillMaxWidth()) {
                Text("Kembali")
            }
        }
    }
}
