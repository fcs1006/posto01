package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class FuelTransactionEntity(
    @PrimaryKey(autoGenerate = true)
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
    val status: String,
    val pointsEarned: Int,
    val cashbackEarned: Double
)

@Entity(tableName = "saved_cards")
data class PaymentCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val holderName: String,
    val last4: String,
    val brand: String,
    val expiry: String,
    val isDefault: Boolean,
    val type: String
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val timeAgo: String,
    val isRead: Boolean,
    val type: String
)

@Entity(tableName = "loyalty_profile")
data class LoyaltyProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val cpf: String,
    val name: String,
    val email: String,
    val phone: String,
    val tier: String,
    val pointsBalance: Int,
    val cashbackBalance: Double,
    val monthlySavings: Double,
    val vehiclePlate: String,
    val vehicleModel: String,
    val habitualFuel: String,
    val isBlackMember: Boolean
)
