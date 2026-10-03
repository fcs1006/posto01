package com.example.data.petros

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class PetrosAuthRequest(
    val stationId: Int,
    val pumpNumber: String,
    val customerCpf: String,
    val requestedLiters: Double? = null,
    val requestedAmount: Double? = null
)

data class PetrosAuthResponse(
    val success: Boolean,
    val authorizationCode: String,
    val pumpNumber: String,
    val nozzleNumber: String,
    val fuelType: String,
    val pricePerLiterClub: Double,
    val message: String
)

data class PetrosPaymentRequest(
    val authorizationCode: String,
    val customerCpf: String,
    val paymentMethod: String, // "PIX", "CREDIT_CARD", "DEBIT_CARD"
    val subtotal: Double,
    val discount: Double,
    val cashbackUsed: Double,
    val finalAmount: Double,
    val cardId: Long? = null
)

data class PetrosPaymentResponse(
    val success: Boolean,
    val transactionCode: String,
    val fiscalCouponNumber: String,
    val status: String,
    val pointsEarned: Int,
    val cashbackEarned: Double,
    val message: String,
    val pixQrPayload: String? = null
)

data class PetrosPriceDto(
    val fuelType: String,
    val name: String,
    val regularPrice: Double,
    val clubPrice: Double,
    val discount: Double
)

data class PetrosStationDto(
    val stationId: Int,
    val name: String,
    val address: String,
    val isOpen24h: Boolean,
    val prices: List<PetrosPriceDto>
)

interface PetrosApiService {
    @GET("api/v1/stations")
    suspend fun getStations(): Response<List<PetrosStationDto>>

    @GET("api/v1/stations/{stationId}/prices")
    suspend fun getStationPrices(@Path("stationId") stationId: Int): Response<List<PetrosPriceDto>>

    @POST("api/v1/pumps/authorize")
    suspend fun authorizePump(@Body request: PetrosAuthRequest): Response<PetrosAuthResponse>

    @POST("api/v1/transactions/pay")
    suspend fun processPayment(@Body request: PetrosPaymentRequest): Response<PetrosPaymentResponse>

    @GET("api/v1/customers/{cpf}/loyalty")
    suspend fun getCustomerLoyalty(@Path("cpf") cpf: String): Response<Map<String, Any>>

    companion object {
        private const val BASE_URL = "https://adaptive.petros.autoposto01.com.br/"

        fun create(): PetrosApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(PetrosApiService::class.java)
        }
    }
}
