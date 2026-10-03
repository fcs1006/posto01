package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Início", Icons.Default.LocalGasStation)
    object Stations : Screen("stations", "Postos", Icons.Default.NearMe)
    object Store : Screen("store", "Loja", Icons.Default.Storefront)
    object Clube : Screen("clube", "Clube", Icons.Default.CardMembership)
    object Wallet : Screen("wallet", "Carteira", Icons.Default.AccountBalanceWallet)
    object History : Screen("history", "Histórico", Icons.AutoMirrored.Filled.ReceiptLong)
}

val BottomNavItems = listOf(
    Screen.Home,
    Screen.Stations,
    Screen.Store,
    Screen.Clube,
    Screen.Wallet
)
