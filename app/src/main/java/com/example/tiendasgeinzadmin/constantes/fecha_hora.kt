package com.geinzTienda.tiendasgeinzadmin.constantes

import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object fecha_hora {

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtnerfechaHora(hora: TextView, fecha: TextView) {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("hh:mm:ss a")
        val formattedTime = currentTime.format(formatter)

        val currentDate = LocalDate.now()
        val formatterfecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = currentDate.format(formatterfecha)

        fecha.text = formattedDate
        hora.text = formattedTime
    }

}