package com.example.tiendasgeinzadmin.FCM

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class volleyHelper(context: Context) {
    companion object {
        @Volatile
        private var Instance: volleyHelper? = null
        fun getInstance(context: Context) = Instance ?: synchronized(this) {
            Instance ?: volleyHelper(context).also { Instance = it }
        }

    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addtoRequestQueue(req:Request<T>){
        requestQueue.add(req)
    }
}