package com.example.ui

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.FuelPrice
import com.example.data.model.PaymentCard
import com.example.data.model.PetrosSyncState
import com.example.data.model.ProductItem
import com.example.data.model.PushNotification
import com.example.data.model.Station
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import com.example.data.repository.PetrosRepository
import com.example.notification.NotificationHelper
import com.example.security.BiometricHelper
import com.example.security.GoogleAuthHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val userProfile: UserProfile = UserProfile(
        name = "Carlos Eduardo",
        email = "carlos.eduardo@gmail.com",
        cpf = "123.456.789-00",
        phone = "(11) 98765-4321",
        tier = "Membro Black • Ativo",
        pointsBalance = 1480,
        cashbackBalance = 15.20,
        monthlySavings = 48.50,
        vehiclePlate = "BRA-2E19",
        vehicleModel = "Honda Civic 2021",
        habitualFuel = "Gasolina Aditivada",
        isBlackMember = true
    ),
    val fuelPrices: List<FuelPrice> = emptyList(),
    val stations: List<Station> = emptyList(),
    val selectedStation: Station? = null,
    val selectedPumpNumber: String = "04",
    val selectedFuel: FuelPrice? = null,
    val volumeLiters: Double = 35.00,
    val useCashback: Boolean = true,
    val selectedPaymentMethod: String = "PIX", // "PIX", "CREDIT_4821", "DEBIT_1092"
    val savedCards: List<PaymentCard> = emptyList(),
    val transactions: List<TransactionRecord> = emptyList(),
    val notifications: List<PushNotification> = emptyList(),
    val products: List<ProductItem> = emptyList(),
    val cartItems: Map<String, Int> = emptyMap(),
    val activeProductCategory: String = "all",
    val petrosSyncState: PetrosSyncState = PetrosSyncState(),
    val isProcessingPayment: Boolean = false,
    val showPixSheet: Boolean = false,
    val showReceiptDialog: Boolean = false,
    val currentReceipt: TransactionRecord? = null,
    val showStationDetailsSheet: Boolean = false,
    val detailedStation: Station? = null,
    val showAddCardDialog: Boolean = false,
    val showNotificationsSheet: Boolean = false,
    val showQrScannerDialog: Boolean = false,
    val showPetrosSyncDialog: Boolean = false,
    val showAlcoholGasCalculator: Boolean = false,
    val showBookingDialog: Boolean = false,
    val bookingServiceName: String = "Pit Stop Troca de Óleo",
    val toastMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = PetrosRepository(database)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        NotificationHelper.createNotificationChannels(application)
        loadInitialData()
        observeDatabase()
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
                _uiState.update { it.copy(userProfile = profile) }
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

    fun setShowPetrosSyncDialog(show: Boolean) {
        _uiState.update { it.copy(showPetrosSyncDialog = show) }
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
                name = name.ifBlank { "Carlos Eduardo" },
                cpf = cpf.ifBlank { "123.456.789-00" },
                phone = phone.ifBlank { "(11) 98765-4321" },
                vehiclePlate = plate.ifBlank { "BRA-2E19" }.uppercase(),
                vehicleModel = model.ifBlank { "Honda Civic 2021" },
                habitualFuel = fuel
            )
            repository.saveProfile(updated)
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

    fun triggerGoogleLogin(context: Context) {
        viewModelScope.launch {
            val result = GoogleAuthHelper.signInWithGoogle(context)
            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null) {
                    val updated = _uiState.value.userProfile.copy(
                        name = data.displayName,
                        email = data.email
                    )
                    repository.saveProfile(updated)
                    showToast("Conectado com sucesso via Google (${data.email})")
                }
            }
        }
    }

    fun syncWithPetros() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    petrosSyncState = it.petrosSyncState.copy(
                        syncStatus = "Sincronizando com Adaptive Petros...",
                        isConnected = true
                    )
                )
            }
            delay(1200)
            _uiState.update {
                it.copy(
                    petrosSyncState = PetrosSyncState(
                        isConnected = true,
                        lastSyncTimestamp = System.currentTimeMillis(),
                        serverHost = "adaptive.petros.autoposto01.com.br",
                        pingMs = (28..49).random(),
                        activePumpsCount = 8,
                        syncStatus = "Sincronizado via Petros API • Tempo Real"
                    )
                )
            }
            showToast("Sincronização com o sistema Petros concluída!")
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
        val cardName = if (state.selectedPaymentMethod.contains("CREDIT")) {
            "Mastercard •••• 4821"
        } else {
            "Visa Débito •••• 1092"
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
