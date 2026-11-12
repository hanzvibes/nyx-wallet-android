package com.hanzvibes.nyxwallet.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * The seed phrase lives ONLY here, encrypted by the Android Keystore
 * (AES256-GCM, hardware-backed where the device supports it).
 * It is never written to a plain file, never logged, never sent anywhere.
 * Clearing the app data destroys it — that is by design; the user must
 * keep the 12-word backup.
 */
class SecureSeedStore(context: Context) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "nyx_seed_store",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    fun hasWallet(): Boolean = prefs.contains(KEY_MNEMONIC)

    fun saveMnemonic(mnemonic: String) {
        prefs.edit().putString(KEY_MNEMONIC, mnemonic.trim()).apply()
    }

    fun loadMnemonic(): String? = prefs.getString(KEY_MNEMONIC, null)

    fun clear() {
        prefs.edit().remove(KEY_MNEMONIC).apply()
    }

    companion object {
        private const val KEY_MNEMONIC = "mnemonic_v1"
    }
}
