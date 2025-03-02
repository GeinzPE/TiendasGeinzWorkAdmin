package com.example.tiendasgeinzadmin.constantes

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object constantes_permisos {

    fun SolicitarPermisoNotificacion(
        contexto: Context,
        permisoNotificaion: ActivityResultLauncher<String>,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    contexto,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_DENIED
            ) {
                permisoNotificaion.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}