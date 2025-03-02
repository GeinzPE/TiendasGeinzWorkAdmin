package com.example.tiendasgeinzadmin

import android.app.Application
import com.example.tiendasgeinzadmin.FCM.volleyHelper

class TiendasGeinzAplication : Application() {
    companion object{
        lateinit var VolleyHelper: volleyHelper
    }

    override fun onCreate() {
        super.onCreate()
        VolleyHelper= volleyHelper.getInstance(this)

    }
}