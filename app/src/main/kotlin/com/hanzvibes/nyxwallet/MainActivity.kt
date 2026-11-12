package com.hanzvibes.nyxwallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hanzvibes.nyxwallet.ui.home.HomeScreen
import com.hanzvibes.nyxwallet.ui.onboarding.OnboardingScreen
import com.hanzvibes.nyxwallet.ui.receive.ReceiveScreen
import com.hanzvibes.nyxwallet.ui.send.SendScreen
import com.hanzvibes.nyxwallet.ui.settings.SettingsScreen
import com.hanzvibes.nyxwallet.ui.theme.NyxTheme
import com.hanzvibes.nyxwallet.wallet.WalletViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NyxTheme {
                val vm: WalletViewModel = viewModel()
                val state by vm.state.collectAsState()
                val nav = rememberNavController()
                val start = if (state.hasWallet) "home" else "onboarding"
                NavHost(navController = nav, startDestination = start) {
                    composable("onboarding") {
                        OnboardingScreen(
                            onDone = { nav.navigate("home") { popUpTo("onboarding") { inclusive = true } } },
                            vm = vm,
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            state = state,
                            onRefresh = { vm.refresh() },
                            onSend = { chainId -> nav.navigate("send/$chainId") },
                            onReceive = { nav.navigate("receive") },
                            onSettings = { nav.navigate("settings") },
                        )
                    }
                    composable("send/{chainId}") { backStack ->
                        val chainId = backStack.arguments?.getString("chainId") ?: ""
                        SendScreen(chainId = chainId, vm = vm, onBack = { nav.popBackStack() })
                    }
                    composable("receive") { ReceiveScreen(state = state, onBack = { nav.popBackStack() }) }
                    composable("settings") {
                        SettingsScreen(state = state, vm = vm, onBack = { nav.popBackStack() },
                            onWalletDeleted = {
                                nav.navigate("onboarding") { popUpTo("home") { inclusive = true } }
                            })
                    }
                }
            }
        }
    }
}
