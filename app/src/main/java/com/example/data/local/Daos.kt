package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<FuelTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FuelTransactionEntity): Long

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun countTransactions(): Int

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()
}

@Dao
interface CardDao {
    @Query("SELECT * FROM saved_cards ORDER BY id DESC")
    fun getAllCards(): Flow<List<PaymentCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: PaymentCardEntity): Long

    @Query("DELETE FROM saved_cards WHERE id = :id")
    suspend fun deleteCard(id: Long)

    @Query("SELECT COUNT(*) FROM saved_cards")
    suspend fun countCards(): Int

    @Query("DELETE FROM saved_cards")
    suspend fun clearAllCards()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface LoyaltyDao {
    @Query("SELECT * FROM loyalty_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<LoyaltyProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: LoyaltyProfileEntity)

    @Query("UPDATE loyalty_profile SET cashbackBalance = :newCashback, pointsBalance = :newPoints WHERE id = 1")
    suspend fun updateBalances(newCashback: Double, newPoints: Int)

    @Query("DELETE FROM loyalty_profile")
    suspend fun clearProfile()
}
