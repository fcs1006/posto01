package com.example.data.model

data class FuelPrice(
    val id: String = "",
    val name: String = "",
    val pumpPrice: Double = 0.0,
    val clubPrice: Double = 0.0,
    val discountPerLiter: Double = 0.0,
    val fuelType: String = "",
    val badgeText: String = "",
    val iconType: String = "fuel",
    val updatedAt: Long = System.currentTimeMillis()
)

data class PromotionItem(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val tag: String = "",
    val description: String = "",
    val badge: String = "",
    val buttonText: String = "Aproveitar",
    val actionType: String = "BOOKING",
    val actionTarget: String = "",
    val discountText: String = "",
    val iconName: String = "car_wash",
    val isActive: Boolean = true,
    val order: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

enum class PumpStatus {
    IDLE,
    AUTHORIZED,
    FUELING,
    COMPLETED,
    OFFLINE
}

data class Pump(
    val id: String,
    val number: String,
    val nozzleNumber: String,
    val stationName: String,
    val fuelName: String,
    val fuelType: String,
    val regularPrice: Double,
    val clubPrice: Double,
    val discountPerLiter: Double,
    val status: PumpStatus = PumpStatus.IDLE,
    val volumeLiters: Double = 0.0,
    val currentTotal: Double = 0.0
)

data class Station(
    val id: Int,
    val code: String,
    val name: String,
    val address: String,
    val neighborhood: String,
    val distanceKm: Double,
    val travelTimeMinutes: Int,
    val isOpen24h: Boolean,
    val rating: Double,
    val reviewsCount: Int,
    val isFavorite: Boolean = false,
    val hasConvenience: Boolean = true,
    val hasOilChange: Boolean = true,
    val hasCarWash: Boolean = false,
    val hasTireCalibration: Boolean = true,
    val hasEvCharging: Boolean = false,
    val hasGnv: Boolean = false,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val fuels: List<FuelPrice> = emptyList(),
    val imageUrl: String = ""
)

data class TransactionRecord(
    val id: Long = 0,
    val code: String,
    val petrosAuthCode: String,
    val timestamp: Long,
    val dateFormatted: String,
    val stationName: String,
    val pumpNumber: String,
    val fuelType: String,
    val liters: Double,
    val pricePerLiter: Double,
    val subtotal: Double,
    val discount: Double,
    val finalAmount: Double,
    val paymentMethod: String,
    val status: String = "PAGO",
    val pointsEarned: Int = 0,
    val cashbackEarned: Double = 0.0
)

data class UserProfile(
    val name: String,
    val email: String,
    val cpf: String,
    val phone: String,
    val tier: String, // "Prata", "Ouro", "Membro Black"
    val pointsBalance: Int,
    val cashbackBalance: Double,
    val monthlySavings: Double,
    val vehiclePlate: String,
    val vehicleModel: String,
    val habitualFuel: String,
    val isBlackMember: Boolean = true,
    val photoUrl: String = "",
    val birthDate: String = ""
)

data class PaymentCard(
    val id: Long = 0,
    val holderName: String,
    val last4: String,
    val brand: String, // "Mastercard", "Visa", "Elo"
    val expiry: String,
    val isDefault: Boolean = false,
    val type: String = "CRÉDITO" // "CRÉDITO", "DÉBITO"
)

data class ProductItem(
    val id: String,
    val name: String,
    val category: String, // "oleos", "aditivos", "palhetas", "cuidados", "conveniencia"
    val subtitle: String,
    val volume: String,
    val regularPrice: Double,
    val clubPrice: Double,
    val badge: String = "",
    val freeServiceNote: String = "",
    val iconName: String = "oil"
)

data class PushNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val timeAgo: String,
    val isRead: Boolean = false,
    val type: String = "PROMO" // "PROMO", "PUMP", "CASHBACK", "VOUCHER"
)

data class PetrosSyncState(
    val isConnected: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val serverHost: String = "adaptive.petros.autoposto01.com.br",
    val pingMs: Int = 42,
    val activePumpsCount: Int = 8,
    val syncStatus: String = "Sincronizado via Petros API"
)
