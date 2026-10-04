package com.example.security

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class GoogleUserData(
    val id: String,
    val email: String,
    val displayName: String,
    val profilePictureUri: String? = null
)

object GoogleAuthHelper {

    suspend fun signInWithGoogle(
        context: Context,
        serverClientId: String? = null
    ): Result<GoogleUserData> = withContext(Dispatchers.IO) {
        try {
            val credentialManager = CredentialManager.create(context)
            val builder = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)

            if (!serverClientId.isNullOrBlank() && !serverClientId.contains("AUTO_POSTO_01")) {
                builder.setServerClientId(serverClientId)
            }

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(builder.build())
                .build()

            val response: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                try {
                    val firebaseCred = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(firebaseCred).await()
                } catch (ex: Exception) {
                    Log.w("GoogleAuthHelper", "Firebase sign-in with Google token: ${ex.message}")
                }

                Result.success(
                    GoogleUserData(
                        id = googleIdTokenCredential.id,
                        email = googleIdTokenCredential.id,
                        displayName = googleIdTokenCredential.displayName ?: googleIdTokenCredential.id.substringBefore("@"),
                        profilePictureUri = googleIdTokenCredential.profilePictureUri?.toString()
                    )
                )
            } else {
                Result.failure(Exception("Credencial inválida ou cancelada pelo usuário"))
            }
        } catch (e: Exception) {
            Log.w("GoogleAuthHelper", "CredentialManager Google error: ${e.message}")
            Result.failure(e)
        }
    }
}
