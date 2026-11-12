package com.hanzvibes.nyxwallet.ui.receive

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hanzvibes.nyxwallet.ui.qrBitmap
import com.hanzvibes.nyxwallet.ui.theme.NyxColors
import com.hanzvibes.nyxwallet.wallet.WalletUiState

@Composable
fun ReceiveScreen(state: WalletUiState, onBack: () -> Unit) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Terima", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NyxColors.Text)
        Text(
            "Satu alamat untuk semua chain EVM (ETH, BNB, MATIC).",
            color = NyxColors.TextDim,
        )
        Spacer(Modifier.height(24.dp))
        Image(
            bitmap = qrBitmap(state.address),
            contentDescription = "QR alamat wallet",
            modifier = Modifier.size(256.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(state.address, color = NyxColors.Text)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("Nyx address", state.address))
                copied = true
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text(if (copied) "Disalin ✓" else "Salin Alamat") }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Kembali") }
    }
}
