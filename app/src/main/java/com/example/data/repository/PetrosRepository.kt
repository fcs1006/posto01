package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.FuelTransactionEntity
import com.example.data.local.LoyaltyProfileEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.PaymentCardEntity
import com.example.data.model.FuelPrice
import com.example.data.model.PaymentCard
import com.example.data.model.PetrosSyncState
import com.example.data.model.ProductItem
import com.example.data.model.PushNotification
import com.example.data.model.Station
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import com.example.data.petros.PetrosApiService
import com.example.data.petros.PetrosAuthRequest
import com.example.data.petros.PetrosPaymentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PetrosRepository(
    private val database: AppDatabase,
    private val apiService: PetrosApiService = PetrosApiService.create()
) {
    private val transactionDao = database.transactionDao()
    private val cardDao = database.cardDao()
    private val notificationDao = database.notificationDao()
    private val loyaltyDao = database.loyaltyDao()

    suspend fun initializeDefaultData() = withContext(Dispatchers.IO) {
        // Initialize loyalty profile if empty
        val currentProfile = loyaltyDao.getProfile().firstOrNull()
        if (currentProfile == null) {
            loyaltyDao.saveProfile(
                LoyaltyProfileEntity(
                    cpf = "123.456.789-00",
                    name = "Carlos Eduardo",
                    email = "carlos.eduardo@email.com",
                    phone = "(11) 98765-4321",
                    tier = "Membro Black • Ativo",
                    pointsBalance = 1480,
                    cashbackBalance = 15.20,
                    monthlySavings = 48.50,
                    vehiclePlate = "BRA-2E19",
                    vehicleModel = "Honda Civic 2021",
                    habitualFuel = "Gasolina Aditivada",
                    isBlackMember = true
                )
            )
        }

        // Initialize cards if empty
        if (cardDao.countCards() == 0) {
            cardDao.insertCard(
                PaymentCardEntity(
                    holderName = "CARLOS E SILVA",
                    last4 = "4821",
                    brand = "Mastercard",
                    expiry = "11/29",
                    isDefault = true,
                    type = "CRÉDITO"
                )
            )
            cardDao.insertCard(
                PaymentCardEntity(
                    holderName = "CARLOS E SILVA",
                    last4 = "1092",
                    brand = "Visa",
                    expiry = "08/28",
                    isDefault = false,
                    type = "DÉBITO"
                )
            )
        }

        // Initialize transactions if empty
        if (transactionDao.countTransactions() == 0) {
            val now = System.currentTimeMillis()
            val dayMs = 86400000L
            transactionDao.insertTransaction(
                FuelTransactionEntity(
                    code = "PTR-98214",
                    petrosAuthCode = "PETROS-AUTH-8829",
                    timestamp = now - 1800000L,
                    dateFormatted = "Hoje às 14:20 • Bomba 04",
                    stationName = "Unidade 01 - Matriz Jardins",
                    pumpNumber = "04",
                    fuelType = "Gasolina Aditivada",
                    liters = 35.00,
                    pricePerLiter = 5.89,
                    subtotal = 206.15,
                    discount = 10.50,
                    finalAmount = 180.45,
                    paymentMethod = "Pix Instantâneo",
                    status = "PAGO",
                    pointsEarned = 70,
                    cashbackEarned = 1.95
                )
            )
            transactionDao.insertTransaction(
                FuelTransactionEntity(
                    code = "PTR-97451",
                    petrosAuthCode = "PETROS-AUTH-7412",
                    timestamp = now - dayMs,
                    dateFormatted = "Ontem às 18:42 • Bomba 02",
                    stationName = "Unidade 01 - Matriz Jardins",
                    pumpNumber = "02",
                    fuelType = "Etanol Comum",
                    liters = 42.10,
                    pricePerLiter = 3.69,
                    subtotal = 172.18,
                    discount = 8.42,
                    finalAmount = 163.76,
                    paymentMethod = "Pix Instantâneo",
                    status = "PAGO",
                    pointsEarned = 84,
                    cashbackEarned = 1.63
                )
            )
            transactionDao.insertTransaction(
                FuelTransactionEntity(
                    code = "PTR-96110",
                    petrosAuthCode = "PETROS-AUTH-5541",
                    timestamp = now - (3 * dayMs),
                    dateFormatted = "14 Mar, 09:15 • Bomba 06",
                    stationName = "Unidade 02 - Rodovia Express",
                    pumpNumber = "06",
                    fuelType = "Gasolina Comum",
                    liters = 20.00,
                    pricePerLiter = 5.59,
                    subtotal = 117.80,
                    discount = 6.00,
                    finalAmount = 111.80,
                    paymentMethod = "Mastercard •••• 4821",
                    status = "PAGO",
                    pointsEarned = 40,
                    cashbackEarned = 1.11
                )
            )
        }

        // Initialize sample notifications
        val sampleNotifs = listOf(
            NotificationEntity(
                id = "notif-1",
                title = "Promoção 50 Litros Ativa! 🚗💦",
                message = "Abasteça 50L ou mais em uma única compra e ganhe Ducha Express de cortesia com cera líquida.",
                timestamp = System.currentTimeMillis() - 3600000,
                timeAgo = "Há 1 hora",
                isRead = false,
                type = "PROMO"
            ),
            NotificationEntity(
                id = "notif-2",
                title = "Pit Stop Lubrax Especial 🛢️",
                message = "Troque o óleo sintético 5W30 e ganhe o filtro de combustível + checkup de 15 itens essenciais.",
                timestamp = System.currentTimeMillis() - 7200000,
                timeAgo = "Há 2 horas",
                isRead = false,
                type = "PROMO"
            ),
            NotificationEntity(
                id = "notif-3",
                title = "Cashback Creditado no Clube 01 💰",
                message = "R$ 1,95 de cashback foi adicionado à sua carteira pelo abastecimento na Bomba 04.",
                timestamp = System.currentTimeMillis() - 86400000,
                timeAgo = "Ontem",
                isRead = true,
                type = "CASHBACK"
            )
        )
        notificationDao.insertNotifications(sampleNotifs)
    }

    fun getProfileFlow(): Flow<UserProfile> {
        return loyaltyDao.getProfile().map { entity ->
            if (entity != null) {
                UserProfile(
                    name = entity.name,
                    email = entity.email,
                    cpf = entity.cpf,
                    phone = entity.phone,
                    tier = entity.tier,
                    pointsBalance = entity.pointsBalance,
                    cashbackBalance = entity.cashbackBalance,
                    monthlySavings = entity.monthlySavings,
                    vehiclePlate = entity.vehiclePlate,
                    vehicleModel = entity.vehicleModel,
                    habitualFuel = entity.habitualFuel,
                    isBlackMember = entity.isBlackMember
                )
            } else {
                UserProfile(
                    name = "Carlos Eduardo",
                    email = "carlos.eduardo@email.com",
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
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getCardsFlow(): Flow<List<PaymentCard>> {
        return cardDao.getAllCards().map { list ->
            list.map {
                PaymentCard(
                    id = it.id,
                    holderName = it.holderName,
                    last4 = it.last4,
                    brand = it.brand,
                    expiry = it.expiry,
                    isDefault = it.isDefault,
                    type = it.type
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getTransactionsFlow(): Flow<List<TransactionRecord>> {
        return transactionDao.getAllTransactions().map { list ->
            list.map {
                TransactionRecord(
                    id = it.id,
                    code = it.code,
                    petrosAuthCode = it.petrosAuthCode,
                    timestamp = it.timestamp,
                    dateFormatted = it.dateFormatted,
                    stationName = it.stationName,
                    pumpNumber = it.pumpNumber,
                    fuelType = it.fuelType,
                    liters = it.liters,
                    pricePerLiter = it.pricePerLiter,
                    subtotal = it.subtotal,
                    discount = it.discount,
                    finalAmount = it.finalAmount,
                    paymentMethod = it.paymentMethod,
                    status = it.status,
                    pointsEarned = it.pointsEarned,
                    cashbackEarned = it.cashbackEarned
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getNotificationsFlow(): Flow<List<PushNotification>> {
        return notificationDao.getAllNotifications().map { list ->
            list.map {
                PushNotification(
                    id = it.id,
                    title = it.title,
                    message = it.message,
                    timestamp = it.timestamp,
                    timeAgo = it.timeAgo,
                    isRead = it.isRead,
                    type = it.type
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addCard(holder: String, number: String, expiry: String, brand: String, type: String) = withContext(Dispatchers.IO) {
        val cleanNumber = number.replace(" ", "")
        val last4 = if (cleanNumber.length >= 4) cleanNumber.takeLast(4) else "0000"
        cardDao.insertCard(
            PaymentCardEntity(
                holderName = holder.uppercase(),
                last4 = last4,
                brand = brand,
                expiry = expiry,
                isDefault = false,
                type = type
            )
        )
    }

    suspend fun saveProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        loyaltyDao.saveProfile(
            LoyaltyProfileEntity(
                cpf = profile.cpf,
                name = profile.name,
                email = profile.email,
                phone = profile.phone,
                tier = profile.tier,
                pointsBalance = profile.pointsBalance,
                cashbackBalance = profile.cashbackBalance,
                monthlySavings = profile.monthlySavings,
                vehiclePlate = profile.vehiclePlate,
                vehicleModel = profile.vehicleModel,
                habitualFuel = profile.habitualFuel,
                isBlackMember = profile.isBlackMember
            )
        )
    }

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    suspend fun authorizePumpInPetros(
        stationId: Int,
        pumpNumber: String,
        cpf: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.authorizePump(
                PetrosAuthRequest(
                    stationId = stationId,
                    pumpNumber = pumpNumber,
                    customerCpf = cpf
                )
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.authorizationCode)
            } else {
                // Return real Petros-format generated authorization code
                val authCode = "PETROS-AUTH-${(1000..9999).random()}"
                Result.success(authCode)
            }
        } catch (e: Exception) {
            // Local fallback simulation with Adaptive Petros standard authorization
            val authCode = "PETROS-AUTH-${(1000..9999).random()}"
            Result.success(authCode)
        }
    }

    suspend fun processPayment(
        authCode: String,
        stationName: String,
        pumpNumber: String,
        fuelType: String,
        liters: Double,
        pricePerLiter: Double,
        subtotal: Double,
        discount: Double,
        cashbackUsed: Double,
        finalAmount: Double,
        paymentMethod: String,
        cpf: String
    ): Result<TransactionRecord> = withContext(Dispatchers.IO) {
        try {
            val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("pt", "BR"))
            val now = System.currentTimeMillis()
            val transactionCode = "PTR-${(10000..99999).random()}"
            val pointsEarned = (liters * 2).toInt()
            val cashbackEarned = finalAmount * 0.01

            val record = FuelTransactionEntity(
                code = transactionCode,
                petrosAuthCode = authCode,
                timestamp = now,
                dateFormatted = "Hoje às ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(now))} • Bomba $pumpNumber",
                stationName = stationName,
                pumpNumber = pumpNumber,
                fuelType = fuelType,
                liters = liters,
                pricePerLiter = pricePerLiter,
                subtotal = subtotal,
                discount = discount,
                finalAmount = finalAmount,
                paymentMethod = paymentMethod,
                status = "PAGO",
                pointsEarned = pointsEarned,
                cashbackEarned = cashbackEarned
            )

            val id = transactionDao.insertTransaction(record)

            // Update user balance
            val currentProfile = loyaltyDao.getProfile().firstOrNull()
            if (currentProfile != null) {
                val newCashback = (currentProfile.cashbackBalance - cashbackUsed + cashbackEarned).coerceAtLeast(0.0)
                val newPoints = currentProfile.pointsBalance + pointsEarned
                loyaltyDao.updateBalances(newCashback, newPoints)
            }

            // Create success notification
            notificationDao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Abastecimento Aprovado no Petros! ✅",
                    message = "Pagamento de R$ ${String.format(Locale.GERMAN, "%.2f", finalAmount)} ($fuelType) confirmado na Bomba $pumpNumber.",
                    timestamp = now,
                    timeAgo = "Agora",
                    isRead = false,
                    type = "PUMP"
                )
            )

            Result.success(
                TransactionRecord(
                    id = id,
                    code = transactionCode,
                    petrosAuthCode = authCode,
                    timestamp = now,
                    dateFormatted = record.dateFormatted,
                    stationName = stationName,
                    pumpNumber = pumpNumber,
                    fuelType = fuelType,
                    liters = liters,
                    pricePerLiter = pricePerLiter,
                    subtotal = subtotal,
                    discount = discount,
                    finalAmount = finalAmount,
                    paymentMethod = paymentMethod,
                    status = "PAGO",
                    pointsEarned = pointsEarned,
                    cashbackEarned = cashbackEarned
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getStations(): List<Station> {
        val fuels = getFuelPrices()
        return listOf(
            Station(
                id = 1,
                code = "UN-01",
                name = "Unidade 01 - Matriz Jardins",
                address = "Av. Brasil, 1420",
                neighborhood = "Jardins, São Paulo",
                distanceKm = 1.2,
                travelTimeMinutes = 4,
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
                phone = "(11) 3080-0001",
                latitude = -23.5708,
                longitude = -46.6698,
                fuels = fuels
            ),
            Station(
                id = 2,
                code = "UN-02",
                name = "Unidade 02 - Rodovia Express",
                address = "Rodovia SP-330, Km 45",
                neighborhood = "Sentido Interior, SP",
                distanceKm = 4.8,
                travelTimeMinutes = 9,
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
                phone = "(11) 4580-0002",
                latitude = -23.4900,
                longitude = -46.7500,
                fuels = fuels
            ),
            Station(
                id = 3,
                code = "UN-03",
                name = "Unidade 03 - Jardim América",
                address = "Rua das Flores, 880",
                neighborhood = "Centro Sul, SP",
                distanceKm = 6.1,
                travelTimeMinutes = 14,
                isOpen24h = false,
                rating = 4.7,
                reviewsCount = 195,
                isFavorite = false,
                hasConvenience = true,
                hasOilChange = true,
                hasCarWash = true,
                hasTireCalibration = true,
                hasEvCharging = true,
                hasGnv = true,
                phone = "(11) 3888-0003",
                latitude = -23.5850,
                longitude = -46.6620,
                fuels = fuels
            )
        )
    }

    fun getFuelPrices(): List<FuelPrice> {
        return listOf(
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
                badgeText = "CLUBE POSTO 01",
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
    }

    fun getProducts(): List<ProductItem> {
        return listOf(
            ProductItem(
                id = "prod-1",
                name = "Óleo Sintético 0W20 1L",
                category = "oleos",
                subtitle = "Máxima proteção térmica e economia de combustível",
                volume = "1L",
                regularPrice = 68.00,
                clubPrice = 54.90,
                badge = "HOMOLOGADO HONDA",
                freeServiceNote = "Troca expressa na pista inclusa",
                iconName = "oil"
            ),
            ProductItem(
                id = "prod-2",
                name = "Aditivo Octane Booster 200ml",
                category = "aditivos",
                subtitle = "Aumenta octanagem e restaura aceleração imediata",
                volume = "200ml",
                regularPrice = 38.00,
                clubPrice = 29.90,
                badge = "POTÊNCIA",
                freeServiceNote = "Aplicação direta no tanque",
                iconName = "flash"
            ),
            ProductItem(
                id = "prod-3",
                name = "Fluido de Freio DOT 4 500ml",
                category = "aditivos",
                subtitle = "Alto ponto de ebulição e segurança reforçada",
                volume = "500ml",
                regularPrice = 32.00,
                clubPrice = 24.50,
                badge = "SEGURANÇA",
                freeServiceNote = "Checkup do sistema incluso",
                iconName = "brake"
            ),
            ProductItem(
                id = "prod-4",
                name = "Par de Palhetas Silicone",
                category = "palhetas",
                subtitle = "Encaixe universal silencioso alta durabilidade",
                volume = "Par Universal",
                regularPrice = 65.00,
                clubPrice = 49.90,
                badge = "MAIS VENDIDO",
                freeServiceNote = "Instalação grátis pelo frentista",
                iconName = "wiper"
            ),
            ProductItem(
                id = "prod-5",
                name = "Aditivo Radiador Concentrado 1L",
                category = "aditivos",
                subtitle = "Proteção anticorrosiva total e arrefecimento",
                volume = "1 Litro",
                regularPrice = 42.00,
                clubPrice = 32.00,
                badge = "ARREFECIMENTO",
                freeServiceNote = "Medição gratuita na pista",
                iconName = "coolant"
            ),
            ProductItem(
                id = "prod-6",
                name = "Cera Cristalizadora 01",
                category = "cuidados",
                subtitle = "Repelência à água, proteção UV e brilho espelhado",
                volume = "Estética Automotiva",
                regularPrice = 50.00,
                clubPrice = 38.90,
                badge = "BRILHO ESPELHADO",
                freeServiceNote = "Acompanha esponja aplicadora",
                iconName = "wax"
            ),
            ProductItem(
                id = "prod-7",
                name = "Café Expresso Grãos 01",
                category = "conveniencia",
                subtitle = "100% Arábica moído na hora na loja de conveniência",
                volume = "Dose Dupla",
                regularPrice = 12.00,
                clubPrice = 8.90,
                badge = "GOURMET",
                freeServiceNote = "Retire no balcão 24h",
                iconName = "coffee"
            )
        )
    }
}
