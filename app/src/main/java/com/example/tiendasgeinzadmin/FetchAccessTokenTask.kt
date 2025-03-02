package com.example.tiendasgeinzadmin

import android.content.Context
import android.os.AsyncTask
import com.google.auth.oauth2.GoogleCredentials

class FetchAccessTokenTask(private val context: Context, private val callback: (String?) -> Unit) :
    AsyncTask<Void, Void, String?>() {

    override fun doInBackground(vararg params: Void?): String? {
        return try {
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

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        callback(result)
    }
}