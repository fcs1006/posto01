package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.AddCardDialog
import com.example.ui.components.AlcoholVsGasCalculatorDialog
import com.example.ui.components.AutoPostoBottomBar
import com.example.ui.components.AutoPostoTopBar
import com.example.ui.components.BookingServiceDialog
import com.example.ui.components.GoogleSignInDialog
import com.example.ui.components.NotificationsSheet
import com.example.ui.components.PetrosSyncDialog
import com.example.ui.components.PixPaymentSheet
import com.example.ui.components.ReceiptDetailsDialog
import com.example.ui.navigation.Screen
import com.example.ui.screens.ClubeScreen
import com.example.ui.screens.CompleteProfileScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.StationsMapScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.screens.WalletPaymentScreen
import com.example.ui.theme.AutoPosto01Theme
import com.example.ui.theme.OnPrimaryEmerald
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.PrimaryContainerEmerald
import com.example.ui.theme.PrimaryEmerald
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceDark
import kotlinx.coroutines.delay

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.saveable.rememberSaveable

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            AutoPosto01Theme {
                MainAppRoot(activity = this)
            }
        }
    }
}

@Composable
fun MainAppRoot(
    activity: FragmentActivity,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val hasUserInDatabase = uiState.userProfile.name.isNotBlank() && (uiState.userProfile.cpf.isNotBlank() || uiState.userProfile.email.isNotBlank())
    var currentRoute by rememberSaveable {
        mutableStateOf(if (uiState.isAuthenticated) Screen.Home.route else Screen.Login.route)
    }
    val context = LocalContext.current

    if (uiState.isSessionRestoring) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryEmerald)
        }
        return
    }

    // Lock non-authenticated users to LoginScreen (No guest mode)
    LaunchedEffect(uiState.isAuthenticated, uiState.needsProfileCompletion) {
        if (!uiState.isAuthenticated) {
            currentRoute = Screen.Login.route
        } else if (uiState.needsProfileCompletion) {
            currentRoute = Screen.CompleteProfile.route
        } else if (currentRoute == Screen.Login.route || currentRoute == Screen.CompleteProfile.route) {
            currentRoute = Screen.Home.route
        }
    }

    BackHandler(enabled = true) {
        if (!uiState.isAuthenticated) {
            activity.finish()
        } else if (currentRoute == Screen.CompleteProfile.route) {
            viewModel.showToast("Conclua seu cadastro para continuar.")
        } else {
            currentRoute = when (currentRoute) {
                Screen.History.route -> Screen.Wallet.route
                Screen.Home.route -> {
                    activity.finish()
                    Screen.Home.route
                }
                else -> Screen.Home.route
            }
        }
    }

    val unreadNotifs = uiState.notifications.count { !it.isRead }

    // Auto-clear toast after 2.5 seconds
    LaunchedEffect(uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            delay(2500)
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            if (currentRoute != Screen.Login.route && currentRoute != Screen.CompleteProfile.route) {
                val title = when (currentRoute) {
                    Screen.Stations.route -> "Postos"
                    Screen.Store.route -> "Loja"
                    Screen.Clube.route -> "Clube De Vantagens"
                    Screen.Wallet.route -> "Carteira"
                    Screen.History.route -> "Histórico"
                    else -> "Início"
                }

                AutoPostoTopBar(
                    title = title,
                    unreadNotifCount = unreadNotifs,
                    showBackButton = currentRoute == Screen.History.route,
                    onBackClick = { currentRoute = Screen.Wallet.route },
                    onNotificationsClick = { viewModel.setShowNotificationsSheet(true) },
                    onProfileClick = { currentRoute = Screen.Clube.route }
                )
            }
        },
        bottomBar = {
            if (currentRoute != Screen.Login.route && currentRoute != Screen.CompleteProfile.route) {
                AutoPostoBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route -> currentRoute = route }
                )
            }
        },
        containerColor = SurfaceDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Screen Routing
            when (currentRoute) {
                Screen.Home.route -> {
                    HomeScreen(
                        userProfile = uiState.userProfile,
                        fuelPrices = uiState.fuelPrices,
                        promotions = uiState.promotions,
                        currentStation = uiState.selectedStation,
                        onNavigateToWallet = { currentRoute = Screen.Wallet.route },
                        onNavigateToStations = { currentRoute = Screen.Stations.route },
                        onNavigateToStore = { currentRoute = Screen.Store.route },
                        onNavigateToClube = { currentRoute = Screen.Clube.route },
                        onOpenCalculator = { viewModel.setShowAlcoholGasCalculator(true) },
                        onOpenBooking = { service -> viewModel.openBookingDialog(service) }
                    )
                }

                Screen.Stations.route -> {
                    StationsMapScreen(
                        stations = uiState.stations,
                        selectedStation = uiState.selectedStation,
                        onStationSelect = { stationId ->
                            viewModel.selectStation(stationId)
                        },
                        onNavigateToWallet = { currentRoute = Screen.Wallet.route },
                        onOpenBooking = { service -> viewModel.openBookingDialog(service) }
                    )
                }

                Screen.Store.route -> {
                    StoreScreen(
                        userProfile = uiState.userProfile,
                        products = uiState.products,
                        cartItems = uiState.cartItems,
                        activeCategory = uiState.activeProductCategory,
                        onCategoryChange = { cat -> viewModel.setProductCategory(cat) },
                        onAddToCart = { id -> viewModel.addToCart(id) },
                        onRemoveFromCart = { id -> viewModel.removeFromCart(id) },
                        onCheckoutCart = {
                            viewModel.showToast("Pedido adicionado à retirada expressa na Pista 03!")
                        }
                    )
                }

                Screen.Clube.route -> {
                    ClubeScreen(
                        userProfile = uiState.userProfile,
                        onSaveProfile = { name, cpf, phone, plate, model, fuel ->
                            viewModel.saveClubeProfile(name, cpf, phone, plate, model, fuel)
                        },
                        onGoogleSignIn = {
                            viewModel.triggerGoogleLogin(context)
                        },
                        onLogout = {
                            viewModel.logout()
                            currentRoute = Screen.Login.route
                        }
                    )
                }

                Screen.Login.route -> {
                    LoginScreen(
                        canUseBiometrics = hasUserInDatabase,
                        onLoginSuccess = { name, cpf, email ->
                            viewModel.loginUser(name, cpf, email) {
                                currentRoute = Screen.Home.route
                            }
                        },
                        onGoogleSignInClick = {
                            viewModel.triggerGoogleLogin(context) {
                                currentRoute = Screen.Home.route
                            }
                        },
                        onBiometricClick = {
                            viewModel.triggerBiometricLogin(activity) {
                                currentRoute = Screen.Home.route
                            }
                        }
                    )
                }

                Screen.CompleteProfile.route -> {
                    CompleteProfileScreen(
                        userProfile = uiState.userProfile,
                        onComplete = { name, phone, cpf, birthDate, password ->
                            viewModel.completeProfile(name, phone, cpf, birthDate, password) {
                                currentRoute = Screen.Home.route
                            }
                        },
                        onExit = {
                            viewModel.logout()
                            currentRoute = Screen.Login.route
                        }
                    )
                }

                Screen.Wallet.route -> {
                    WalletPaymentScreen(
                        userProfile = uiState.userProfile,
                        pumpNumber = uiState.selectedPumpNumber,
                        fuelPrice = uiState.selectedFuel,
                        volumeLiters = uiState.volumeLiters,
                        useCashback = uiState.useCashback,
                        selectedPaymentMethod = uiState.selectedPaymentMethod,
                        savedCards = uiState.savedCards,
                        transactions = uiState.transactions,
                        isProcessingPayment = uiState.isProcessingPayment,
                        onPumpNumberChange = { num -> viewModel.setPumpNumber(num) },
                        onUseCashbackChange = { use -> viewModel.toggleUseCashback(use) },
                        onSelectPaymentMethod = { m -> viewModel.selectPaymentMethod(m) },
                        onScanQrClick = { viewModel.setShowQrScannerDialog(true) },
                        onAddNewCardClick = { viewModel.setShowAddCardDialog(true) },
                        onAuthorizePayment = {
                            viewModel.startPaymentFlow(activity)
                        },
                        onViewReceipt = { record ->
                            viewModel.setShowReceiptDialog(true, record)
                        },
                        onViewAllHistory = { currentRoute = Screen.History.route }
                    )
                }

                Screen.History.route -> {
                    HistoryScreen(
                        transactions = uiState.transactions,
                        onBackClick = { currentRoute = Screen.Wallet.route },
                        onReceiptClick = { record ->
                            viewModel.setShowReceiptDialog(true, record)
                        }
                    )
                }
            }

            // Toast Floating Notification
            AnimatedVisibility(
                visible = uiState.toastMessage != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { 40 }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceContainerHighest,
                    tonalElevation = 10.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.3f)),
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.toastMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs & Sheets
    if (uiState.showPixSheet) {
        val fuel = uiState.selectedFuel ?: uiState.fuelPrices.first()
        val subtotal = uiState.volumeLiters * fuel.pumpPrice
        val discount = uiState.volumeLiters * fuel.discountPerLiter
        val totalWithDiscount = subtotal - discount
        val cashback = if (uiState.useCashback) uiState.userProfile.cashbackBalance.coerceAtMost(totalWithDiscount) else 0.0
        val finalAmount = (totalWithDiscount - cashback).coerceAtLeast(0.0)

        PixPaymentSheet(
            totalAmount = finalAmount,
            customerCpf = uiState.userProfile.cpf,
            pumpNumber = uiState.selectedPumpNumber,
            fuelName = fuel.name,
            onConfirmPaid = { viewModel.confirmPixPayment() },
            onDismiss = { viewModel.setShowPixSheet(false) }
        )
    }

    if (uiState.showReceiptDialog && uiState.currentReceipt != null) {
        ReceiptDetailsDialog(
            receipt = uiState.currentReceipt!!,
            onDismiss = { viewModel.setShowReceiptDialog(false) }
        )
    }

    if (uiState.showAddCardDialog) {
        AddCardDialog(
            onAddCard = { holder, number, expiry, brand, type ->
                viewModel.addNewCard(holder, number, expiry, brand, type)
            },
            onDismiss = { viewModel.setShowAddCardDialog(false) }
        )
    }

    if (uiState.showNotificationsSheet) {
        NotificationsSheet(
            notifications = uiState.notifications,
            onDismiss = { viewModel.setShowNotificationsSheet(false) }
        )
    }

    if (uiState.showPetrosSyncDialog) {
        PetrosSyncDialog(
            syncState = uiState.petrosSyncState,
            onForceSync = { viewModel.syncWithPetros() },
            onDismiss = { viewModel.setShowPetrosSyncDialog(false) }
        )
    }

    if (uiState.showAlcoholGasCalculator) {
        AlcoholVsGasCalculatorDialog(
            gasPrice = 5.89,
            ethanolPrice = 3.69,
            onDismiss = { viewModel.setShowAlcoholGasCalculator(false) }
        )
    }

    if (uiState.showBookingDialog) {
        BookingServiceDialog(
            serviceName = uiState.bookingServiceName,
            vehiclePlate = uiState.userProfile.vehiclePlate,
            onConfirm = { day, time ->
                viewModel.confirmBooking(day, time)
            },
            onDismiss = { viewModel.closeBookingDialog() }
        )
    }

    if (uiState.showQrScannerDialog) {
        // Quick QR Scanner Simulator Dialog
        androidx.compose.ui.window.Dialog(onDismissRequest = { viewModel.setShowQrScannerDialog(false) }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceContainerHighest,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Leitor de QR Code Petros",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Aponte a câmera para o QR Code fixado no bico de abastecimento da bomba.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark)
                            .border(2.dp, PrimaryEmerald, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.setPumpNumber("04")
                            viewModel.setShowQrScannerDialog(false)
                            currentRoute = Screen.Wallet.route
                            viewModel.showToast("Bomba 04 identificada via QR Code!")
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryContainerEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Simular Leitura (Bomba 04)", color = OnPrimaryEmerald, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (uiState.showGoogleSignInDialog) {
        GoogleSignInDialog(
            initialEmail = uiState.userProfile.email,
            onConfirm = { name, email ->
                viewModel.loginWithGoogleAccount(name, email)
            },
            onDismiss = { viewModel.setShowGoogleSignInDialog(false) }
        )
    }
}
