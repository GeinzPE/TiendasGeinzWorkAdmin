import android.content.Context
import android.util.Log

import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpContent
import com.google.api.client.http.HttpRequestFactory
import com.google.api.client.http.HttpResponse
import com.google.api.client.http.HttpTransport
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.InputStream
import okhttp3.Request

class NotificationRS {

    private val ACCESS_TOKEN_URL = "https://oauth2.googleapis.com/token"
    private val FCM_URL = "https://fcm.googleapis.com/v1/projects/geinzworkapp/messages:send"

    private val client = OkHttpClient()

    suspend fun sendNotification_sin_parametros(
        context: Context,
        token: String,
        title: String,
        body: String,
    ) {
        val accessToken = getAccessToken(context)
        println("el token es $accessToken")
        if (accessToken == null) {
            println("Error al obtener el token de acceso")
            return
        }

        val jsonPayload = JSONObject().apply {
            put("message", JSONObject().apply {
                put("token", token)
                put("notification", JSONObject().apply {
                    put("title", title)
                    put("body", body)
                })

            })
        }

        Log.d("json","obtenemos el $jsonPayload")

        val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(FCM_URL)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            println("Response code: ${response.code}")
            println("Response body: ${response.body?.string()}")
        }
    }

    suspend fun sendNotification_con_parametros(
        idEnviado1: String,
        idEnviado2: String,
        idEnviado3: String,
        v1:String, v2:String, v3:String,
        Vista: String,
        context: Context,
        token: String,
        title: String,
        body: String,
    ) {
        val accessToken = getAccessToken(context)
        println("el token es $accessToken")
        if (accessToken == null) {
            println("Error al obtener el token de acceso")
            return
        }

        val jsonPayload = JSONObject().apply {
            put("message", JSONObject().apply {
                put("token", token)
                put("notification", JSONObject().apply {
                    put("title", title)
                    put("body", body)
                })

                put("data", JSONObject().apply {
                    put(idEnviado1, v1)
                    put(idEnviado2, v2)
                    put(idEnviado3, v3)
                    put("click_action", Vista)
                })
                put("android", JSONObject().apply {
                    put("notification", JSONObject().apply {
                        put("click_action", Vista)
                    })
                })

            })
        }

        val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(FCM_URL)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            println("Response code: ${response.code}")
            println("Response body: ${response.body?.string()}")
        }
    }

    suspend fun getAccessToken(context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("service-account-file.json")
                val googleCredentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
                googleCredentials.refreshIfExpired()
                googleCredentials.accessToken.tokenValue
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
