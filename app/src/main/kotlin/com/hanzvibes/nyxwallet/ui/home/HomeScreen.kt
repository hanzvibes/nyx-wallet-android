package com.hanzvibes.nyxwallet.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hanzvibes.nyxwallet.data.Format
import com.hanzvibes.nyxwallet.ui.theme.NyxCardShape
import com.hanzvibes.nyxwallet.ui.theme.NyxColors
import com.hanzvibes.nyxwallet.wallet.WalletUiState

@Composable
fun HomeScreen(
    state: WalletUiState,
    onRefresh: () -> Unit,
    onSend: (String) -> Unit,
    onReceive: () -> Unit,
    onSettings: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Nyx Wallet", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NyxColors.Text)
                Row {
                    IconButton(onClick = onRefresh) { Icon(Icons.Default.Refresh, "Refresh", tint = NyxColors.Text) }
                    IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Settings", tint = NyxColors.Text) }
                }
            }
            Text(
                if (state.testnetMode) "TESTNET — dana percobaan" else "MAINNET — dana beneran",
                color = if (state.testnetMode) NyxColors.Amber else NyxColors.Red,
            )
            Text(Format.shortAddress(state.address), color = NyxColors.TextDim)
            Spacer(Modifier.height(8.dp))
            Card(
                shape = NyxCardShape,
                colors = CardDefaults.cardColors(containerColor = NyxColors.Card),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Total Saldo", color = NyxColors.TextDim)
                    if (state.loading) {
                        CircularProgressIndicator(color = NyxColors.Violet)
                    } else {
                        Text(Format.usd(state.totalUsd), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = NyxColors.Text)
                    }
                    state.error?.let { Text(it, color = NyxColors.Red) }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { state.assets.firstOrNull()?.let { onSend(it.chain.id) } }, modifier = Modifier.weight(1f)) {
                    Text("Kirim")
                }
                OutlinedButton(onClick = onReceive, modifier = Modifier.weight(1f)) { Text("Terima") }
            }
        }
        item { Text("Aset", fontWeight = FontWeight.Bold, color = NyxColors.Text, fontSize = 18.sp) }
        items(state.assets) { asset ->
            Card(
                shape = NyxCardShape,
                colors = CardDefaults.cardColors(containerColor = NyxColors.Card),
                modifier = Modifier.fillMaxWidth().clickable { onSend(asset.chain.id) },
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(asset.chain.name, color = NyxColors.Text, fontWeight = FontWeight.Bold)
                        Text(asset.chain.symbol, color = NyxColors.TextDim)
                    }
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                        Text("${Format.amount(asset.balance)} ${asset.chain.symbol}", color = NyxColors.Text)
                        Text(asset.valueUsd?.let { Format.usd(it) } ?: "—", color = NyxColors.TextDim)
                    }
                }
            }
        }
        if (state.sentTxs.isNotEmpty()) {
            item { Text("Terkirim dari wallet ini", fontWeight = FontWeight.Bold, color = NyxColors.Text, fontSize = 18.sp) }
            items(state.sentTxs) { tx ->
                Card(
                    shape = NyxCardShape,
                    colors = CardDefaults.cardColors(containerColor = NyxColors.Card),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("${tx.amount} ${tx.symbol} → ${Format.shortAddress(tx.to)}", color = NyxColors.Text)
                        Text(Format.shortAddress(tx.hash), color = NyxColors.TextDim)
                    }
                }
            }
        }
    }
}
