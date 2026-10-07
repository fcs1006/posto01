package com.example.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.FirestoreService
import com.example.data.local.AppDatabase
import com.example.data.model.FuelPrice
import com.example.data.model.PaymentCard
import com.example.data.model.ProductItem
import com.example.data.model.PromotionItem
import com.example.data.model.PushNotification
import com.example.data.model.Station
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import com.example.data.repository.PetrosRepository
import com.example.notification.NotificationHelper
import com.example.security.BiometricHelper
import com.example.security.GoogleAuthHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class MainUiState(
    val userProfile: UserProfile = UserProfile(
        name = "",
        email = "",
        cpf = "",
        phone = "",
        tier = "Visitante",
        pointsBalance = 0,
        cashbackBalance = 0.0,
        monthlySavings = 0.0,
        vehiclePlate = "",
        vehicleModel = "",
        habitualFuel = "Gasolina Comum",
        isBlackMember = false
    ),
    val fuelPrices: List<FuelPrice> = emptyList(),
    val stations: List<Station> = emptyList(),
    val selectedStation: Station? = null,
    val selectedPumpNumber: String = "01",
    val selectedFuel: FuelPrice? = null,
    val volumeLiters: Double = 20.00,
    val useCashback: Boolean = false,
    val selectedPaymentMethod: String = "PIX",
    val savedCards: List<PaymentCard> = emptyList(),
    val transactions: List<TransactionRecord> = emptyList(),
    val notifications: List<PushNotification> = emptyList(),
    val products: List<ProductItem> = emptyList(),
    val cartItems: Map<String, Int> = emptyMap(),
    val activeProductCategory: String = "all",
    val isProcessingPayment: Boolean = false,
    val showPixSheet: Boolean = false,
    val showReceiptDialog: Boolean = false,
    val currentReceipt: TransactionRecord? = null,
    val showStationDetailsSheet: Boolean = false,
    val detailedStation: Station? = null,
    val showAddCardDialog: Boolean = false,
    val showNotificationsSheet: Boolean = false,
    val showQrScannerDialog: Boolean = false,
    val showAlcoholGasCalculator: Boolean = false,
    val showBookingDialog: Boolean = false,
    val bookingServiceName: String = "Pit Stop Troca de Óleo",
    val promotions: List<PromotionItem> = emptyList(),
    val showGoogleSignInDialog: Boolean = false,
    val isAuthenticated: Boolean = false,
    val needsProfileCompletion: Boolean = false,
    val isSessionRestoring: Boolean = true,
    val toastMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = PetrosRepository(database)
    private val firestoreService = FirestoreService(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        NotificationHelper.createNotificationChannels(application)
        loadInitialData()
        observeDatabase()
        observeFirestore()
        restoreFromFirestore()
    }

    private fun currentUid(): String? = try {
        FirebaseAuth.getInstance().currentUser?.uid
    } catch (e: Exception) {
        null
    }

    private fun persistToFirestore(profile: UserProfile) {
        val uid = currentUid() ?: return
        viewModelScope.launch {
            firestoreService.saveUserProfile(uid, profile)
        }
    }

    private fun restoreFromFirestore() {
        viewModelScope.launch {
            val uid = currentUid() ?: return@launch
            val remote = firestoreService.getUserProfileOnce(uid) ?: return@launch
            if (remote.name.isNotBlank() || remote.email.isNotBlank()) {
                repository.saveProfile(remote)
            }
        }
    }

    private fun observeFirestore() {
        viewModelScope.launch {
            firestoreService.fuelPricesFlow.collect { prices ->
                if (prices.isNotEmpty()) {
                    _uiState.update { state ->
                        val selected = state.selectedFuel?.let { curr ->
                            prices.find { it.id == curr.id }
                        } ?: prices.firstOrNull { it.id == "gas-aditivada" } ?: prices.firstOrNull()

                        val updatedStations = state.stations.map { it.copy(fuels = prices) }
                        val updatedSelectedStation = state.selectedStation?.copy(fuels = prices)

                        state.copy(
                            fuelPrices = prices,
                            selectedFuel = selected,
                            stations = updatedStations,
                            selectedStation = updatedSelectedStation
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            firestoreService.promotionsFlow.collect { promos ->
                _uiState.update { it.copy(promotions = promos) }
            }
        }

        viewModelScope.launch {
            firestoreService.stationsFlow.collect { stations ->
                if (stations.isNotEmpty()) {
                    _uiState.update { state ->
                        val selected = state.selectedStation?.let { curr ->
                            stations.find { it.id == curr.id }
                        } ?: stations.firstOrNull()

                        state.copy(
                            stations = stations,
                            selectedStation = selected
                        )
                    }
                }
            }
        }
    }

    private fun loadInitialData() {
        val fuels = repository.getFuelPrices()
        val stations = repository.getStations()
        val products = repository.getProducts()

        _uiState.update { state ->
            state.copy(
                fuelPrices = fuels,
                stations = stations,
                selectedStation = stations.firstOrNull(),
                selectedFuel = fuels.firstOrNull { it.id == "gas-aditivada" } ?: fuels.firstOrNull(),
                products = products
            )
        }

        viewModelScope.launch {
            repository.initializeDefaultData()
        }
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            repository.getProfileFlow().collect { profile ->
                val isAuth = profile.name.isNotBlank() && (profile.cpf.isNotBlank() || profile.email.isNotBlank())
                val needsCompletion = isAuth && profile.cpf.isBlank()
                _uiState.update {
                    it.copy(
                        userProfile = profile,
                        isAuthenticated = isAuth,
                        needsProfileCompletion = needsCompletion,
                        isSessionRestoring = false
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getCardsFlow().collect { cards ->
                _uiState.update { it.copy(savedCards = cards) }
            }
        }

        viewModelScope.launch {
            repository.getTransactionsFlow().collect { history ->
                _uiState.update { it.copy(transactions = history) }
            }
        }

        viewModelScope.launch {
            repository.getNotificationsFlow().collect { notifs ->
                _uiState.update { it.copy(notifications = notifs) }
            }
        }
    }

    fun selectStation(stationId: Int) {
        val station = _uiState.value.stations.find { it.id == stationId }
        if (station != null) {
            _uiState.update { it.copy(selectedStation = station) }
        }
    }

    fun openStationDetails(station: Station) {
        _uiState.update {
            it.copy(
                detailedStation = station,
                showStationDetailsSheet = true
            )
        }
    }

    fun closeStationDetails() {
        _uiState.update { it.copy(showStationDetailsSheet = false) }
    }

    fun setPumpNumber(number: String) {
        val clean = number.filter { it.isDigit() }.take(2)
        _uiState.update { it.copy(selectedPumpNumber = clean) }
    }

    fun selectFuel(fuelId: String) {
        val fuel = _uiState.value.fuelPrices.find { it.id == fuelId }
        if (fuel != null) {
            _uiState.update { it.copy(selectedFuel = fuel) }
        }
    }

    fun setVolumeLiters(liters: Double) {
        _uiState.update { it.copy(volumeLiters = liters) }
    }

    fun toggleUseCashback(use: Boolean) {
        _uiState.update { it.copy(useCashback = use) }
    }

    fun selectPaymentMethod(method: String) {
        _uiState.update { it.copy(selectedPaymentMethod = method) }
    }

    fun setProductCategory(category: String) {
        _uiState.update { it.copy(activeProductCategory = category) }
    }

    fun addToCart(productId: String) {
        val current = _uiState.value.cartItems.toMutableMap()
        current[productId] = (current[productId] ?: 0) + 1
        _uiState.update { it.copy(cartItems = current) }
        val item = _uiState.value.products.find { it.id == productId }
        showToast("${item?.name ?: "Item"} adicionado ao carrinho!")
    }

    fun removeFromCart(productId: String) {
        val current = _uiState.value.cartItems.toMutableMap()
        val count = current[productId] ?: 0
        if (count > 1) {
            current[productId] = count - 1
        } else {
            current.remove(productId)
        }
        _uiState.update { it.copy(cartItems = current) }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyMap()) }
    }

    fun setShowAddCardDialog(show: Boolean) {
        _uiState.update { it.copy(showAddCardDialog = show) }
    }

    fun setShowNotificationsSheet(show: Boolean) {
        _uiState.update { it.copy(showNotificationsSheet = show) }
        if (show) {
            viewModelScope.launch {
                repository.markAllNotificationsRead()
            }
        }
    }

    fun setShowQrScannerDialog(show: Boolean) {
        _uiState.update { it.copy(showQrScannerDialog = show) }
    }

    fun setShowAlcoholGasCalculator(show: Boolean) {
        _uiState.update { it.copy(showAlcoholGasCalculator = show) }
    }

    fun openBookingDialog(serviceName: String) {
        _uiState.update {
            it.copy(
                bookingServiceName = serviceName,
                showBookingDialog = true
            )
        }
    }

    fun closeBookingDialog() {
        _uiState.update { it.copy(showBookingDialog = false) }
    }

    fun confirmBooking(day: String, time: String) {
        closeBookingDialog()
        showToast("Agendamento de ${_uiState.value.bookingServiceName} confirmado para $day às $time! Box reservado.")
        NotificationHelper.showPromoNotification(
            getApplication(),
            "Agendamento Confirmado! 📅",
            "Seu atendimento para ${_uiState.value.bookingServiceName} foi agendado para $day às $time na Unidade 01."
        )
    }

    fun setShowPixSheet(show: Boolean) {
        _uiState.update { it.copy(showPixSheet = show) }
    }

    fun setShowReceiptDialog(show: Boolean, receipt: TransactionRecord? = null) {
        _uiState.update {
            it.copy(
                showReceiptDialog = show,
                currentReceipt = receipt ?: it.currentReceipt
            )
        }
    }

    fun saveClubeProfile(
        name: String,
        cpf: String,
        phone: String,
        plate: String,
        model: String,
        fuel: String
    ) {
        viewModelScope.launch {
            val updated = _uiState.value.userProfile.copy(
                name = name.trim(),
                cpf = cpf.trim(),
                phone = phone.trim(),
                vehiclePlate = plate.trim().uppercase(),
                vehicleModel = model.trim(),
                habitualFuel = fuel,
                tier = if (name.isNotBlank() || cpf.isNotBlank()) "Membro Clube 01" else "Visitante"
            )
            repository.saveProfile(updated)
            persistToFirestore(updated)
            showToast("Cadastro no Clube 01 atualizado! Descontos liberados na bomba.")
            NotificationHelper.showPromoNotification(
                getApplication(),
                "Clube 01 Ativado! ⭐",
                "Seu CPF e placa ${updated.vehiclePlate} estão autorizados com desconto direto no bico."
            )
        }
    }

    fun addNewCard(holder: String, number: String, expiry: String, brand: String, type: String) {
        viewModelScope.launch {
            repository.addCard(holder, number, expiry, brand, type)
            setShowAddCardDialog(false)
            showToast("Cartão $brand final ${number.takeLast(4)} adicionado com sucesso!")
        }
    }

    fun triggerGoogleLogin(context: Context, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val result = GoogleAuthHelper.signInWithGoogle(context)
            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null) {
                    val updated = _uiState.value.userProfile.copy(
                        name = data.displayName,
                        email = data.email,
                        photoUrl = data.profilePictureUri ?: "",
                        tier = "Membro Clube 01",
                        pointsBalance = if (_uiState.value.userProfile.pointsBalance == 0) 250 else _uiState.value.userProfile.pointsBalance
                    )
                    repository.saveProfile(updated)
                    persistToFirestore(updated)
                    val needsCompletion = updated.cpf.isBlank() || updated.birthDate.isBlank() || updated.phone.isBlank()
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true,
                            needsProfileCompletion = needsCompletion,
                            userProfile = updated,
                            showGoogleSignInDialog = false
                        )
                    }
                    if (needsCompletion) {
                        showToast("Complete seu cadastro para continuar.")
                    } else {
                        showToast(if (data.firebaseAuthenticated) "Conectado com sucesso via Google (${data.email})" else "Google conectado localmente.")
                    }
                    onComplete()
                }
            } else {
                val reason = result.exceptionOrNull()?.message ?: "erro desconhecido"
                showToast("Google não abriu: $reason")
                _uiState.update { it.copy(showGoogleSignInDialog = true) }
            }
        }
    }

    fun loginWithGoogleAccount(displayName: String, email: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val currentPoints = _uiState.value.userProfile.pointsBalance
            val currentCashback = _uiState.value.userProfile.cashbackBalance
            val updated = _uiState.value.userProfile.copy(
                name = displayName.ifBlank { email.substringBefore("@") },
                email = email,
                tier = "Membro Clube 01",
                pointsBalance = if (currentPoints == 0) 250 else currentPoints,
                cashbackBalance = currentCashback
            )
            repository.saveProfile(updated)
            _uiState.update {
                it.copy(
                    isAuthenticated = true,
                    userProfile = updated,
                    showGoogleSignInDialog = false
                )
            }
            showToast("Login com Google ($email) concluído com sucesso!")
            onComplete()
        }
    }

    fun setShowGoogleSignInDialog(show: Boolean) {
        _uiState.update { it.copy(showGoogleSignInDialog = show) }
    }

    fun completeProfile(
        name: String,
        phone: String,
        cpf: String,
        birthDate: String,
        password: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            // Define/atualiza a senha no Firebase Auth (melhor esforço) para permitir
            // login futuro por e-mail + senha usando o mesmo e-mail do Google.
            try {
                FirebaseAuth.getInstance().currentUser?.updatePassword(password)?.await()
            } catch (e: Exception) {
                Log.w("MainViewModel", "Não foi possível definir a senha: ${e.message}")
            }

            val updated = _uiState.value.userProfile.copy(
                name = name.trim(),
                phone = phone.trim(),
                cpf = cpf.trim(),
                birthDate = birthDate.trim()
            )
            repository.saveProfile(updated)
            persistToFirestore(updated)
            _uiState.update {
                it.copy(
                    userProfile = updated,
                    isAuthenticated = true,
                    needsProfileCompletion = false
                )
            }
            showToast("Cadastro concluído! Bem-vindo ao Clube 01, ${updated.name}!")
            onComplete()
        }
    }

    fun triggerGoogleSignUp(context: Context, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val result = GoogleAuthHelper.signInWithGoogle(context)
            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null) {
                    val currentPoints = _uiState.value.userProfile.pointsBalance
                    val currentCashback = _uiState.value.userProfile.cashbackBalance
                    val updated = _uiState.value.userProfile.copy(
                        name = data.displayName,
                        email = data.email,
                        tier = "Membro Clube 01",
                        pointsBalance = currentPoints + 500,
                        cashbackBalance = currentCashback + 5.0
                    )
                    repository.saveProfile(updated)
                    _uiState.update { it.copy(isAuthenticated = true, userProfile = updated, showGoogleSignInDialog = false) }
                    showToast("Conta criada via Google! +500 Pontos e R$ 5,00 de Cashback creditados.")
                    onComplete()
                }
            } else {
                _uiState.update { it.copy(showGoogleSignInDialog = true) }
            }
        }
    }

    fun triggerBiometricLogin(activity: FragmentActivity, onComplete: () -> Unit = {}) {
        BiometricHelper.authenticate(
            activity = activity,
            title = "Acesso Seguro Clube 01",
            subtitle = "Autentique com sua digital ou reconhecimento facial",
            description = "Acesse sua carteira e descontos exclusivos",
            onSuccess = {
                _uiState.update { it.copy(isAuthenticated = true) }
                showToast("Autenticação biométrica confirmada!")
                onComplete()
            },
            onError = { err ->
                showToast(err)
            }
        )
    }

    fun loginUser(name: String, cpf: String, email: String, isNewAccount: Boolean = false, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val currentPoints = _uiState.value.userProfile.pointsBalance
            val currentCashback = _uiState.value.userProfile.cashbackBalance
            val updated = _uiState.value.userProfile.copy(
                name = name,
                cpf = cpf,
                email = email,
                tier = "Membro Clube 01",
                pointsBalance = if (isNewAccount) currentPoints + 500 else currentPoints,
                cashbackBalance = if (isNewAccount) currentCashback + 5.0 else currentCashback
            )
            repository.saveProfile(updated)
            persistToFirestore(updated)
            _uiState.update { it.copy(isAuthenticated = true, userProfile = updated) }
            showToast(if (isNewAccount) "Bem-vindo ao Clube 01, $name! +500 Pontos creditados." else "Bem-vindo de volta ao Clube 01, $name!")
            onComplete()
        }
    }

    fun logout() {
        viewModelScope.launch {
            try { FirebaseAuth.getInstance().signOut() } catch (e: Exception) { Log.w("MainViewModel", "signOut: ${e.message}") }
            repository.clearAllMockData()
            _uiState.update {
                it.copy(
                    isAuthenticated = false,
                    needsProfileCompletion = false,
                    userProfile = UserProfile(
                        name = "",
                        email = "",
                        cpf = "",
                        phone = "",
                        tier = "Visitante",
                        pointsBalance = 0,
                        cashbackBalance = 0.0,
                        monthlySavings = 0.0,
                        vehiclePlate = "",
                        vehicleModel = "",
                        habitualFuel = "Gasolina Comum",
                        isBlackMember = false
                    ),
                    savedCards = emptyList(),
                    transactions = emptyList()
                )
            }
            showToast("Dados limpos. Você saiu da sua conta.")
        }
    }

    fun startPaymentFlow(activity: FragmentActivity) {
        val state = _uiState.value
        val fuel = state.selectedFuel ?: return
        val station = state.selectedStation ?: state.stations.first()

        val subtotal = state.volumeLiters * fuel.pumpPrice
        val discount = state.volumeLiters * fuel.discountPerLiter
        val totalWithDiscount = subtotal - discount
        val cashbackToUse = if (state.useCashback) {
            state.userProfile.cashbackBalance.coerceAtMost(totalWithDiscount)
        } else 0.0
        val finalAmount = (totalWithDiscount - cashbackToUse).coerceAtLeast(0.0)

        if (state.selectedPaymentMethod == "PIX") {
            // Biometric authorization before displaying Pix or instant checkout
            BiometricHelper.authenticate(
                activity = activity,
                title = "Autorizar Abastecimento Petros",
                subtitle = "Bomba ${state.selectedPumpNumber} • ${fuel.name}",
                description = "Total: R$ ${String.format(java.util.Locale.GERMAN, "%.2f", finalAmount)}",
                onSuccess = {
                    _uiState.update { it.copy(showPixSheet = true) }
                },
                onError = { err ->
                    showToast(err)
                }
            )
        } else {
            // Credit or Debit Card
            BiometricHelper.authenticate(
                activity = activity,
                title = "Autorizar Pagamento de Cartão",
                subtitle = "Bomba ${state.selectedPumpNumber} • ${fuel.name}",
                description = "Total: R$ ${String.format(java.util.Locale.GERMAN, "%.2f", finalAmount)}",
                onSuccess = {
                    executeCardPayment(
                        station = station,
                        fuel = fuel,
                        subtotal = subtotal,
                        discount = discount,
                        cashbackUsed = cashbackToUse,
                        finalAmount = finalAmount
                    )
                },
                onError = { err ->
                    showToast(err)
                }
            )
        }
    }

    fun confirmPixPayment() {
        val state = _uiState.value
        val fuel = state.selectedFuel ?: return
        val station = state.selectedStation ?: state.stations.first()

        val subtotal = state.volumeLiters * fuel.pumpPrice
        val discount = state.volumeLiters * fuel.discountPerLiter
        val totalWithDiscount = subtotal - discount
        val cashbackToUse = if (state.useCashback) {
            state.userProfile.cashbackBalance.coerceAtMost(totalWithDiscount)
        } else 0.0
        val finalAmount = (totalWithDiscount - cashbackToUse).coerceAtLeast(0.0)

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPayment = true) }
            delay(1500) // realistic gateway handshake

            val authResult = repository.authorizePumpInPetros(
                stationId = station.id,
                pumpNumber = state.selectedPumpNumber,
                cpf = state.userProfile.cpf
            )
            val authCode = authResult.getOrDefault("PETROS-AUTH-9021")

            val payResult = repository.processPayment(
                authCode = authCode,
                stationName = station.name,
                pumpNumber = state.selectedPumpNumber,
                fuelType = fuel.name,
                liters = state.volumeLiters,
                pricePerLiter = fuel.pumpPrice,
                subtotal = subtotal,
                discount = discount,
                cashbackUsed = cashbackToUse,
                finalAmount = finalAmount,
                paymentMethod = "Pix Instantâneo",
                cpf = state.userProfile.cpf
            )

            _uiState.update {
                it.copy(
                    isProcessingPayment = false,
                    showPixSheet = false
                )
            }

            if (payResult.isSuccess) {
                val transaction = payResult.getOrNull()
                _uiState.update {
                    it.copy(
                        currentReceipt = transaction,
                        showReceiptDialog = true
                    )
                }
                NotificationHelper.showPumpNotification(
                    getApplication(),
                    "Bico Liberado na Bomba ${state.selectedPumpNumber}! ⛽",
                    "Pagamento via Pix confirmado. O bico está liberado para abastecimento."
                )
            } else {
                showToast("Erro ao processar pagamento. Tente novamente.")
            }
        }
    }

    private fun executeCardPayment(
        station: Station,
        fuel: FuelPrice,
        subtotal: Double,
        discount: Double,
        cashbackUsed: Double,
        finalAmount: Double
    ) {
        val state = _uiState.value
        val cardId = state.selectedPaymentMethod.removePrefix("CARD_").toLongOrNull()
        val selectedCard = state.savedCards.firstOrNull { it.id == cardId }
        val cardName = if (selectedCard != null) {
            "${selectedCard.brand} •••• ${selectedCard.last4}"
        } else if (state.selectedPaymentMethod == "CIELO_POS") {
            "Cielo Smart POS (Pista)"
        } else {
            "Cartão Cadastrado"
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPayment = true) }
            delay(1500)

            val authResult = repository.authorizePumpInPetros(
                stationId = station.id,
                pumpNumber = state.selectedPumpNumber,
                cpf = state.userProfile.cpf
            )
            val authCode = authResult.getOrDefault("PETROS-AUTH-7734")

            val payResult = repository.processPayment(
                authCode = authCode,
                stationName = station.name,
                pumpNumber = state.selectedPumpNumber,
                fuelType = fuel.name,
                liters = state.volumeLiters,
                pricePerLiter = fuel.pumpPrice,
                subtotal = subtotal,
                discount = discount,
                cashbackUsed = cashbackUsed,
                finalAmount = finalAmount,
                paymentMethod = cardName,
                cpf = state.userProfile.cpf
            )

            _uiState.update { it.copy(isProcessingPayment = false) }

            if (payResult.isSuccess) {
                val transaction = payResult.getOrNull()
                _uiState.update {
                    it.copy(
                        currentReceipt = transaction,
                        showReceiptDialog = true
                    )
                }
                NotificationHelper.showPumpNotification(
                    getApplication(),
                    "Bico Liberado na Bomba ${state.selectedPumpNumber}! ⛽",
                    "Pagamento no cartão $cardName aprovado. Pode abastecer agora."
                )
            } else {
                showToast("Falha na autorização do cartão. Verifique seus dados.")
            }
        }
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
