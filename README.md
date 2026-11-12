# Nyx Wallet

Nyx Wallet — Full Multi-Chain non-custodial crypto wallet for Android.
Kotlin + Jetpack Compose. Seed phrase BIP39, kunci disimpan terenkripsi
di perangkat (Android Keystore + EncryptedSharedPreferences), tidak pernah
dikirim ke server mana pun.

> ⚠️ Versi awal (0.1.0) jalan di **testnet dulu** (Sepolia, BSC Testnet,
> Polygon Amoy). Jangan pakai dana mainnet beneran sampai hardening
> Batch 5 selesai dan Rehan yang memutuskan.

## Chain (Batch 1: EVM)
- Ethereum — mainnet + Sepolia testnet
- BNB Smart Chain — mainnet + BSC Testnet
- Polygon — mainnet + Amoy testnet

Batch 2: Bitcoin + Solana. Batch 3: portfolio, chart, NFT.
Batch 4: WalletConnect + Swap. Batch 5: multi-wallet + hardening + APK testnet.

## Build
```sh
./gradlew testDebugUnitTest :app:assembleDebug --no-daemon -Pkotlin.compiler.execution.strategy=in-process
```
Butuh JDK 21, Android SDK 35, Gradle 8.14. CI GitHub menjalankan
unit test + assembleDebug di setiap push/PR ke main.

## Keamanan
- Seed phrase (12 kata) hanya ada di perangkat, dienkripsi.
- Kirim transaksi selalu minta konfirmasi + PIN/biometrik.
- Tidak ada analytics, tidak ada server kustodian.
