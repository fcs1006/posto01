package com.example.security

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class GoogleUserData(
    val id: String,
    val email: String,
    val displayName: String,
    val profilePictureUri: String? = null,
    val firebaseAuthenticated: Boolean = false
)

object GoogleAuthHelper {

    /**
     * OAuth 2.0 Web Client ID (Google Cloud Console / Firebase console).
     *
     * Preencha com o "Web client ID" do seu projeto Firebase para habilitar a
     * verificação do ID token no backend e o login via Firebase Auth.
     * Ex.: "1234567890-xxxxxxxxxxxxxxxx.apps.googleusercontent.com"
     *
     * Enquanto vazio, o Credential Manager usa a audiência padrão e o login no
     * Firebase é degradado (e-mail/nome ainda são extraídos do ID token).
     */
    private const val SERVER_CLIENT_ID = "466576787682-81u7fgs529jjfv70hul57uojgifadkg0.apps.googleusercontent.com"

    suspend fun signInWithGoogle(
        context: Context,
        serverClientId: String? = null
    ): Result<GoogleUserData> = withContext(Dispatchers.IO) {
        try {
            val credentialManager = CredentialManager.create(context)
            val builder = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)

            val clientId = serverClientId?.takeIf { it.isNotBlank() }
                ?: SERVER_CLIENT_ID.takeIf { it.isNotBlank() }
            if (clientId != null) {
                builder.setServerClientId(clientId)
            }

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(builder.build())
                .build()

            val response: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                // 1) Autentica no Firebase Auth (fonte de verdade da sessão).
                val firebaseUser = signInToFirebase(context, idToken)

                // 2) Extrai e-mail/nome do ID token (robusto mesmo sem Firebase configurado).
                val claims = decodeIdTokenClaims(idToken)
                val email = firebaseUser?.email
                    ?: claims["email"]?.takeIf { it.isNotBlank() }
                    ?: ""
                val displayName = firebaseUser?.displayName
                    ?: claims["name"]?.takeIf { it.isNotBlank() }
                    ?: googleIdTokenCredential.displayName
                    ?: email.substringBefore("@")

                Result.success(
                    GoogleUserData(
                        id = firebaseUser?.uid ?: claims["sub"] ?: googleIdTokenCredential.id,
                        email = email,
                        displayName = displayName,
                        profilePictureUri = firebaseUser?.photoUrl?.toString()
                            ?: googleIdTokenCredential.profilePictureUri?.toString(),
                        firebaseAuthenticated = firebaseUser != null
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

    private suspend fun signInToFirebase(context: Context, idToken: String): FirebaseUser? {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                Log.w("GoogleAuthHelper", "FirebaseApp não inicializado (google-services.json ausente). Login Firebase ignorado.")
                null
            } else {
                val auth = FirebaseAuth.getInstance()
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await().user
            }
        } catch (e: Exception) {
            Log.e("GoogleAuthHelper", "Falha no sign-in Firebase: ${e.message}", e)
            null
        }
    }

    private fun decodeIdTokenClaims(idToken: String): Map<String, String> {
        return try {
            val parts = idToken.split(".")
            if (parts.size != 3) return emptyMap()
            val payloadBytes = Base64.decode(
                parts[1],
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            )
            val json = JSONObject(String(payloadBytes, Charsets.UTF_8))
            mapOf(
                "email" to json.optString("email"),
                "name" to json.optString("name"),
                "sub" to json.optString("sub"),
                "picture" to json.optString("picture")
            )
        } catch (e: Exception) {
            Log.w("GoogleAuthHelper", "Falha ao decodificar ID token: ${e.message}")
            emptyMap()
        }
    }
}
