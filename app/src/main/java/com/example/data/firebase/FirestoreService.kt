package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.model.FuelPrice
import com.example.data.model.PromotionItem
import com.example.data.model.Station
import com.example.data.model.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Service responsible for real-time synchronization of fuel prices and promotions
 * via Firebase Firestore. Enables immediate dynamic updates without requiring app redeploys.
 */
class FirestoreService(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var firestoreInstance: FirebaseFirestore? = null

    // In-memory fallback and cache to guarantee instant responsiveness and offline operation
    private val _fuelPricesFlow = MutableStateFlow(defaultFuelPrices())
    val fuelPricesFlow = _fuelPricesFlow.asStateFlow()

    private val _promotionsFlow = MutableStateFlow(defaultPromotions())
    val promotionsFlow = _promotionsFlow.asStateFlow()

    private val _stationsFlow = MutableStateFlow(defaultStations())
    val stationsFlow = _stationsFlow.asStateFlow()

    private var fuelsListenerRegistration: ListenerRegistration? = null
    private var promotionsListenerRegistration: ListenerRegistration? = null
    private var stationsListenerRegistration: ListenerRegistration? = null

    init {
        initializeFirestore()
        startRealtimeSync()
    }

    private fun initializeFirestore() {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firestoreInstance = FirebaseFirestore.getInstance()
                Log.d("FirestoreService", "Firebase Firestore initialized successfully.")
            } else {
                Log.w("FirestoreService", "FirebaseApp not yet initialized; using robust fallback cache.")
            }
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error initializing Firebase Firestore: ${e.message}", e)
            firestoreInstance = null
        }
    }

    private fun startRealtimeSync() {
        val db = firestoreInstance ?: return

        // 1. Observe Fuel Prices in Real-Time
        try {
            fuelsListenerRegistration?.remove()
            fuelsListenerRegistration = db.collection(COLLECTION_FUELS)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Log.w("FirestoreService", "Listen error on fuels collection: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshots != null && !snapshots.isEmpty) {
                        val fuelsList = snapshots.documents.mapNotNull { doc ->
                            try {
                                val pumpPrice = doc.getDouble("pumpPrice") ?: 0.0
                                val clubPrice = doc.getDouble("clubPrice") ?: 0.0
                                val discount = (pumpPrice - clubPrice).coerceAtLeast(0.0)

                                FuelPrice(
                                    id = doc.getString("id") ?: doc.id,
                                    name = doc.getString("name") ?: "",
                                    pumpPrice = pumpPrice,
                                    clubPrice = clubPrice,
                                    discountPerLiter = doc.getDouble("discountPerLiter") ?: discount,
                                    fuelType = doc.getString("fuelType") ?: "",
                                    badgeText = doc.getString("badgeText") ?: "CLUBE 01",
                                    iconType = doc.getString("iconType") ?: "fuel",
                                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                                )
                            } catch (e: Exception) {
                                Log.e("FirestoreService", "Error parsing fuel document ${doc.id}: ${e.message}")
                                null
                            }
                        }
                        if (fuelsList.isNotEmpty()) {
                            _fuelPricesFlow.value = fuelsList
                        }
                    } else if (snapshots != null && snapshots.isEmpty) {
                        // Empty collection: seed initial prices to Firestore
                        scope.launch {
                            seedInitialFuelsToFirestore()
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to attach fuels listener: ${e.message}")
        }

        // 2. Observe Promotions in Real-Time
        try {
            promotionsListenerRegistration?.remove()
            promotionsListenerRegistration = db.collection(COLLECTION_PROMOTIONS)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        Log.w("FirestoreService", "Listen error on promotions collection: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshots != null && !snapshots.isEmpty) {
                        val promoList = snapshots.documents.mapNotNull { doc ->
                            try {
                                PromotionItem(
                                    id = doc.getString("id") ?: doc.id,
                                    title = doc.getString("title") ?: "",
                                    subtitle = doc.getString("subtitle") ?: "",
                                    tag = doc.getString("tag") ?: "",
                                    description = doc.getString("description") ?: "",
                                    badge = doc.getString("badge") ?: "",
                                    buttonText = doc.getString("buttonText") ?: "Aproveitar",
                                    actionType = doc.getString("actionType") ?: "BOOKING",
                                    actionTarget = doc.getString("actionTarget") ?: "",
                                    discountText = doc.getString("discountText") ?: "",
                                    iconName = doc.getString("iconName") ?: "car_wash",
                                    isActive = doc.getBoolean("isActive") ?: true,
                                    order = doc.getLong("order")?.toInt() ?: 0,
                                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                                )
                            } catch (e: Exception) {
                                Log.e("FirestoreService", "Error parsing promotion ${doc.id}: ${e.message}")
                                null
                            }
                        }.sortedBy { it.order }

                        if (promoList.isNotEmpty()) {
                            _promotionsFlow.value = promoList
                        }
                    } else if (snapshots != null && snapshots.isEmpty) {
                        // Empty collection: seed initial promotions to Firestore
                        scope.launch {
                            seedInitialPromotionsToFirestore()
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to attach promotions listener: ${e.message}")
        }


    }

    /**
     * Updates station coordinates or details dynamically in Firestore and updates the local state.
     */
    suspend fun updateStationCoordinates(
        stationId: Int,
        latitude: Double,
        longitude: Double
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentList = _stationsFlow.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == stationId }
            if (index != -1) {
                currentList[index] = currentList[index].copy(latitude = latitude, longitude = longitude)
                _stationsFlow.value = currentList
            }

            firestoreInstance?.let { db ->
                db.collection(COLLECTION_STATIONS).document("station_$stationId")
                    .update("latitude", latitude, "longitude", longitude)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error updating coordinates in Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Updates fuel prices dynamically in Firestore and updates the local state.
     */
    suspend fun updateFuelPrice(
        fuelId: String,
        newPumpPrice: Double,
        newClubPrice: Double
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val discount = (newPumpPrice - newClubPrice).coerceAtLeast(0.0)
            val now = System.currentTimeMillis()

            // Update in-memory flow immediately for instant UI responsiveness
            val currentList = _fuelPricesFlow.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == fuelId }
            if (index != -1) {
                currentList[index] = currentList[index].copy(
                    pumpPrice = newPumpPrice,
                    clubPrice = newClubPrice,
                    discountPerLiter = discount,
                    updatedAt = now
                )
                _fuelPricesFlow.value = currentList
            }

            // Sync with Firestore if available
            firestoreInstance?.let { db ->
                val fuelMap = mapOf(
                    "pumpPrice" to newPumpPrice,
                    "clubPrice" to newClubPrice,
                    "discountPerLiter" to discount,
                    "updatedAt" to now
                )
                db.collection(COLLECTION_FUELS).document(fuelId).set(fuelMap, SetOptions.merge()).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error updating fuel price in Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Publishes or modifies a promotion campaign in Firestore and updates local state.
     */
    suspend fun savePromotion(promotion: PromotionItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            val updatedPromo = promotion.copy(updatedAt = now)

            // Update in-memory flow immediately
            val currentList = _promotionsFlow.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == updatedPromo.id }
            if (index != -1) {
                currentList[index] = updatedPromo
            } else {
                currentList.add(updatedPromo)
            }
            _promotionsFlow.value = currentList.sortedBy { it.order }

            // Sync with Firestore
            firestoreInstance?.let { db ->
                db.collection(COLLECTION_PROMOTIONS).document(updatedPromo.id).set(updatedPromo).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error saving promotion in Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Toggles the active status of a promotion dynamically in real-time.
     */
    suspend fun togglePromotion(promoId: String, isActive: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentList = _promotionsFlow.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == promoId }
            if (index != -1) {
                currentList[index] = currentList[index].copy(isActive = isActive)
                _promotionsFlow.value = currentList
            }

            firestoreInstance?.let { db ->
                db.collection(COLLECTION_PROMOTIONS).document(promoId)
                    .update("isActive", isActive, "updatedAt", System.currentTimeMillis())
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error toggling promotion in Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Salva o perfil do usuário no Firestore (users/{uid}).
     * Permite recuperar o cadastro em outro aparelho.
     */
    suspend fun saveUserProfile(uid: String, profile: UserProfile): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val db = firestoreInstance ?: return@withContext Result.failure(Exception("Firestore indisponível"))
            val data = mapOf(
                "name" to profile.name,
                "email" to profile.email,
                "cpf" to profile.cpf,
                "phone" to profile.phone,
                "birthDate" to profile.birthDate,
                "tier" to profile.tier,
                "pointsBalance" to profile.pointsBalance,
                "cashbackBalance" to profile.cashbackBalance,
                "monthlySavings" to profile.monthlySavings,
                "vehiclePlate" to profile.vehiclePlate,
                "vehicleModel" to profile.vehicleModel,
                "habitualFuel" to profile.habitualFuel,
                "isBlackMember" to profile.isBlackMember,
                "photoUrl" to profile.photoUrl,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection(COLLECTION_USERS).document(uid).set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error saving user profile: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Carrega o perfil do usuário do Firestore (users/{uid}).
     * Retorna null se não existir ou se o Firestore estiver indisponível.
     */
    suspend fun getUserProfileOnce(uid: String): UserProfile? = withContext(Dispatchers.IO) {
        try {
            val db = firestoreInstance ?: return@withContext null
            val doc = db.collection(COLLECTION_USERS).document(uid).get().await()
            val data = doc.data ?: return@withContext null
            UserProfile(
                name = data["name"] as? String ?: "",
                email = data["email"] as? String ?: "",
                cpf = data["cpf"] as? String ?: "",
                phone = data["phone"] as? String ?: "",
                birthDate = data["birthDate"] as? String ?: "",
                tier = data["tier"] as? String ?: "Visitante",
                pointsBalance = (data["pointsBalance"] as? Number)?.toInt() ?: 0,
                cashbackBalance = (data["cashbackBalance"] as? Number)?.toDouble() ?: 0.0,
                monthlySavings = (data["monthlySavings"] as? Number)?.toDouble() ?: 0.0,
                vehiclePlate = data["vehiclePlate"] as? String ?: "",
                vehicleModel = data["vehicleModel"] as? String ?: "",
                habitualFuel = data["habitualFuel"] as? String ?: "Gasolina Comum",
                isBlackMember = data["isBlackMember"] as? Boolean ?: false,
                photoUrl = data["photoUrl"] as? String ?: ""
            )
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error loading user profile: ${e.message}")
            null
        }
    }

    private suspend fun seedInitialFuelsToFirestore() {
        val db = firestoreInstance ?: return
        try {
            val defaultFuels = defaultFuelPrices()
            for (fuel in defaultFuels) {
                db.collection(COLLECTION_FUELS).document(fuel.id).set(fuel).await()
            }
            Log.d("FirestoreService", "Seeded default fuel prices to Firestore.")
        } catch (e: Exception) {
            Log.w("FirestoreService", "Could not seed fuels to Firestore: ${e.message}")
        }
    }

    private suspend fun seedInitialPromotionsToFirestore() {
        val db = firestoreInstance ?: return
        try {
            val defaultPromos = defaultPromotions()
            for (promo in defaultPromos) {
                db.collection(COLLECTION_PROMOTIONS).document(promo.id).set(promo).await()
            }
            Log.d("FirestoreService", "Seeded default promotions to Firestore.")
        } catch (e: Exception) {
            Log.w("FirestoreService", "Could not seed promotions to Firestore: ${e.message}")
        }
    }

    private suspend fun seedInitialStationsToFirestore() {
        val db = firestoreInstance ?: return
        try {
            try {
                db.collection(COLLECTION_STATIONS).document("station_4").delete().await()
                db.collection(COLLECTION_STATIONS).document("station_5").delete().await()
            } catch (_: Exception) {}

            val defaultSts = defaultStations()
            for (st in defaultSts) {
                db.collection(COLLECTION_STATIONS).document("station_${st.id}").set(st).await()
            }
            _stationsFlow.value = defaultSts
            Log.d("FirestoreService", "Seeded Barreiras - BA stations to Firestore.")
        } catch (e: Exception) {
            Log.w("FirestoreService", "Could not seed stations to Firestore: ${e.message}")
        }
    }

    companion object {
        const val COLLECTION_FUELS = "fuel_prices"
        const val COLLECTION_PROMOTIONS = "promotions"
        const val COLLECTION_STATIONS = "stations"
        const val COLLECTION_USERS = "users"

        fun defaultStations(): List<Station> {
            val fuels = defaultFuelPrices()
            return listOf(
                Station(
                    id = 1,
                    code = "UN-01",
                    name = "Auto Posto 01 • Matriz Clériston Andrade",
                    address = "Av. Clériston Andrade",
                    neighborhood = "Vila Dulce, Barreiras - BA, 47800-358",
                    distanceKm = 0.8,
                    travelTimeMinutes = 2,
                    isOpen24h = true,
                    rating = 4.9,
                    reviewsCount = 428,
                    isFavorite = true,
                    hasConvenience = true,
                    hasOilChange = true,
                    hasCarWash = true,
                    hasTireCalibration = true,
                    hasEvCharging = true,
                    hasGnv = false,
                    phone = "(77) 3611-0101",
                    latitude = -12.145903771769904,
                    longitude = -44.992762813692174,
                    fuels = fuels,
                    imageUrl = ""
                ),
                Station(
                    id = 2,
                    code = "UN-02",
                    name = "Auto Posto 01 • Unidade José Bonifácio",
                    address = "Av. José Bonifácio",
                    neighborhood = "Vila Brasil, Barreiras - BA, 47801-230",
                    distanceKm = 1.4,
                    travelTimeMinutes = 4,
                    isOpen24h = true,
                    rating = 4.8,
                    reviewsCount = 312,
                    isFavorite = false,
                    hasConvenience = true,
                    hasOilChange = false,
                    hasCarWash = false,
                    hasTireCalibration = true,
                    hasEvCharging = false,
                    hasGnv = false,
                    phone = "(77) 3611-0102",
                    latitude = -12.141580583248611,
                    longitude = -44.990239621481614,
                    fuels = fuels,
                    imageUrl = ""
                ),
                Station(
                    id = 3,
                    code = "UN-03",
                    name = "Auto Posto 01 • Unidade Castelo Branco",
                    address = "Av. Castelo Branco, 301",
                    neighborhood = "Vila Brasil, Barreiras - BA, 47800-515",
                    distanceKm = 2.3,
                    travelTimeMinutes = 6,
                    isOpen24h = true,
                    rating = 4.9,
                    reviewsCount = 285,
                    isFavorite = false,
                    hasConvenience = true,
                    hasOilChange = true,
                    hasCarWash = true,
                    hasTireCalibration = true,
                    hasEvCharging = true,
                    hasGnv = false,
                    phone = "(77) 3611-0103",
                    latitude = -12.138640416584256,
                    longitude = -44.97708358834687,
                    fuels = fuels,
                    imageUrl = ""
                )
            )
        }

        fun defaultFuelPrices(): List<FuelPrice> = listOf(
            FuelPrice(
                id = "gas-aditivada",
                name = "Gasolina Aditivada",
                pumpPrice = 6.19,
                clubPrice = 5.89,
                discountPerLiter = 0.30,
                fuelType = "Max Clean",
                badgeText = "CLUBE 01",
                iconType = "speed"
            ),
            FuelPrice(
                id = "gas-comum",
                name = "Gasolina Comum",
                pumpPrice = 5.89,
                clubPrice = 5.59,
                discountPerLiter = 0.30,
                fuelType = "Comum",
                badgeText = "CLUBE 01",
                iconType = "water_drop"
            ),
            FuelPrice(
                id = "etanol",
                name = "Etanol Hidratado",
                pumpPrice = 3.89,
                clubPrice = 3.69,
                discountPerLiter = 0.20,
                fuelType = "Bio 100%",
                badgeText = "CLUBE 01",
                iconType = "eco"
            ),
            FuelPrice(
                id = "diesel-s10",
                name = "Diesel S10",
                pumpPrice = 5.99,
                clubPrice = 5.79,
                discountPerLiter = 0.20,
                fuelType = "Euro VI",
                badgeText = "CLUBE 01",
                iconType = "local_shipping"
            )
        )

        fun defaultPromotions(): List<PromotionItem> = listOf(
            PromotionItem(
                id = "promo-uber-taxi",
                tag = "CONVÊNIO OFICIAL PETROS",
                title = "Motoristas de App & Taxistas",
                subtitle = "Desconto exclusivo de R$ 0,15 por litro em todos os postos de Barreiras",
                description = "Tabela cadastrada diretamente no servidor Petros Desktop. Valide sua placa e informe o CPF na maquininha Cielo Smart do frentista.",
                badge = "-R$ 0,15/L DIRETO",
                discountText = "R$ 0,15 OFF",
                buttonText = "Ver no Clube",
                actionType = "WALLET",
                actionTarget = "WALLET",
                iconName = "speed",
                isActive = true,
                order = 0
            ),
            PromotionItem(
                id = "promo-ducha",
                tag = "PROMOÇÃO 50 LITROS",
                title = "Ducha Express Gratuita",
                subtitle = "Abasteça 50L ou mais e ganhe lavagem com cera líquida na pista",
                description = "Válido para qualquer combustível. O voucher digital é gerado automaticamente na sua Carteira.",
                badge = "DUCHA CORTESIA",
                discountText = "Grátis",
                buttonText = "Agendar Ducha",
                actionType = "BOOKING",
                actionTarget = "Ducha Express Gratuita",
                iconName = "car_wash",
                isActive = true,
                order = 1
            ),
            PromotionItem(
                id = "promo-oleo",
                tag = "PIT STOP CLUBE 01",
                title = "Troca de Óleo Lubrax",
                subtitle = "Compre 4L de óleo sintético e ganhe o filtro + checkup 15 itens essenciais",
                description = "Troca rápida sem fila com box exclusivo para membros do Clube 01.",
                badge = "FILTRO GRÁTIS",
                discountText = "Cortesia",
                buttonText = "Agendar Troca",
                actionType = "BOOKING",
                actionTarget = "Pit Stop Troca de Óleo",
                iconName = "oil",
                isActive = true,
                order = 2
            ),
            PromotionItem(
                id = "promo-quarta-etanol",
                tag = "QUARTA DO ETANOL",
                title = "Super Desconto no Etanol",
                subtitle = "Toda quarta-feira: desconto adicional de R$ 0,10/L para membros do Clube",
                description = "Preço especial direto no bico identificado pelo seu CPF cadastrado.",
                badge = "-R$ 0,30/L TOTAL",
                discountText = "Desconto Extra",
                buttonText = "Calcular Álcool vs Gasolina",
                actionType = "CALCULATOR",
                actionTarget = "",
                iconName = "eco",
                isActive = true,
                order = 3
            ),
            PromotionItem(
                id = "promo-pix",
                tag = "CASHBACK EM DOBRO",
                title = "Pagamento com Pix",
                subtitle = "Ganhe 2% de cashback direto na sua carteira pagando com Pix no totem",
                description = "Saldo creditado instantaneamente após a confirmação bancária.",
                badge = "2% CASHBACK",
                discountText = "2X Pontos",
                buttonText = "Abastecer Agora",
                actionType = "WALLET",
                actionTarget = "",
                iconName = "cashback",
                isActive = true,
                order = 4
            )
        )
    }
}
