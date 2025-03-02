package com.example.tiendasgeinzadmin.constantes

import NotificationRS
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object obtener_enviar_notification {
    private lateinit var firebaseAuth: FirebaseAuth
    fun obtnerTokenSeguidores(
        txt_Mostrador: TextView,
        hora: TextView,
        fecha: TextView,
        tituloNotificacionED: EditText,
        descripcionNotificacionED: EditText,
        context: Context,
        idTienda: String,
        idNoticia: String,
        vista: String,
        id1_p: String,
        id2_p: String,
        idp_3: String,
        v1: String,
        v2: String,
        v3: String,
    ) {
        firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("seguidores")

        db.get().addOnSuccessListener { result ->
            val tokens = mutableListOf<String>()
            val tasks = mutableListOf<Task<DocumentSnapshot>>()
            for (document in result) {
                val id = document.getString("id") ?: ""
                val notificaciont = document.getBoolean("notificaciones") ?: false
                if (id.isNotEmpty() && notificaciont) {
                    val token1Task = FirebaseFirestore.getInstance()
                        .collection("Trabajadores_Usuarios_Drivers")
                        .document("trabajadores")
                        .collection("trabajadores")
                        .document(id)
                        .get()

                    val token2Task = FirebaseFirestore.getInstance()
                        .collection("Trabajadores_Usuarios_Drivers")
                        .document("usuarios")
                        .collection("usuarios")
                        .document(id)
                        .get()

                    tasks.add(token1Task)
                    tasks.add(token2Task)
                }
            }

            Tasks.whenAllComplete(tasks).addOnSuccessListener {
                for (task in tasks) {
                    val res = task.result
                    if (res != null && res.exists()) {
                        val token = res.getString("token")
                        if (token != null) {
                            tokens.add(token)
                        }
                    } else {
                        Log.e("No encontrado", "Token no encontrado en alguna de las colecciones")
                    }
                }
//                enviarNotificaciones(
//                    tituloNotificacionED,
//                    descripcionNotificacionED,
//                    context,
//                    vista, id1_p, id2_p, idp_3, v1, v2, v3,
//                    tokens
//                )
//                subirContadorNoti(
//                    txt_Mostrador,
//                    tituloNotificacionED,
//                    descripcionNotificacionED,
//                    hora,
//                    fecha
//                )
            }.addOnFailureListener { e ->
                Log.e("Error", "Error al obtener tokens: ${e.message}")
            }
        }
    }

    fun obtenerTokenUsuario(

        tituloNotificacionED: String,
        descripcionNotificacionED: String,
        context: Context,
        idTienda: String,
        idNoticia: String,
        vista: String,
        idUsuario: String, // Cambié el parámetro a idUsuario para especificar un solo usuario
        v1: String,
        v2: String,
        v3: String
    ) {
        firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("seguidores")

        // Obtener el token del usuario específico
        val token1Task = FirebaseFirestore.getInstance()
            .collection("Trabajadores_Usuarios_Drivers")
            .document("trabajadores")
            .collection("trabajadores")
            .document(idUsuario)
            .get()

        val token2Task = FirebaseFirestore.getInstance()
            .collection("Trabajadores_Usuarios_Drivers")
            .document("usuarios")
            .collection("usuarios")
            .document(idUsuario)
            .get()

        val tasks = mutableListOf(token1Task, token2Task)

        Tasks.whenAllComplete(tasks).addOnSuccessListener {
            var token: String? = null
            for (task in tasks) {
                val res = task.result
                if (res != null && res.exists()) {
                    token = res.getString("token")
                    break  // Solo necesitamos un token
                }
            }

            if (token != null) {
                // Enviar la notificación solo al usuario con el token encontrado
                val tokens = listOf(token)  // Usamos una lista con solo un token

                // Enviar la notificación
                enviarNotificaciones(
                    tituloNotificacionED,
                    descripcionNotificacionED,
                    context,
                    vista, idTienda, idNoticia, idUsuario, v1, v2, v3,
                    tokens
                )

//                // Subir el contador de notificaciones
//                subirContadorNoti(
//                    txt_Mostrador,
//                    tituloNotificacionED,
//                    descripcionNotificacionED,
//                    hora,
//                    fecha
//                )
            } else {
                Log.e("No encontrado", "Token no encontrado para el usuario con id: $idUsuario")
            }
        }.addOnFailureListener { e ->
            Log.e("Error", "Error al obtener token: ${e.message}")
        }
    }


    private fun enviarNotificaciones(
        tituloNotificacionED: String,
        descripcionNotificacionED: String,
        context: Context,
        vista: String,
        id1_p: String,
        id2_p: String,
        idp_3: String,
        v1: String,
        v2: String,
        v3: String,
        tokens: List<String>,
    ) {
        GlobalScope.launch {
            tokens.forEach { token ->
                try {
                    val noti = NotificationRS()
                    noti.sendNotification_con_parametros(
                        id1_p,
                        id2_p,
                        idp_3,
                        v1,
                        v2,
                        v3,
                        vista,
                        context,
                        token,
                        "${tituloNotificacionED}",
                        "${descripcionNotificacionED}"
                    )
                    println("se mando la noticiona")
                } catch (e: Exception) {
                    println("Error sending notification for token $token: ${e.message}")
                }
            }
        }
    }

//    private fun subirContadorNoti(
//        txt_Mostrador: TextView,
//        tituloNotificacionED: EditText,
//        descripcionNotificacionED: EditText,
//        hora: TextView,
//        fecha: TextView,
//    ) {
//        firebaseAuth = FirebaseAuth.getInstance()
//        val subidasString = txt_Mostrador.text.toString()
//        val subidasInt = subidasString.toIntOrNull() ?: 0
//        val subidasIncrementado = subidasInt + 1
//        txt_Mostrador.text = subidasIncrementado.toString()
//        val db = FirebaseFirestore.getInstance().collection("Tiendas")
//            .document(firebaseAuth.uid.toString()).collection("notificaciones")
//        val hashMap = hashMapOf<String, Any>(
//            "titulo" to tituloNotificacionED.text.toString(),
//            "contenido" to descripcionNotificacionED.text.toString(),
//            "hora_subida" to hora.text.toString(),
//            "fecha_subida" to fecha.text.toString(),
//        )
//
//        db.add(hashMap).addOnSuccessListener {
//            Log.d("Notificacion_db", "Notificacion guardad a al db")
//        }
//            .addOnFailureListener { e ->
//                Log.e("Notificacion_db", "error al guardar en la db $e")
//            }
//    }

    @SuppressLint("SetTextI18n")
    fun contadorNotificaciones(txt_Mostrador: TextView) {
        firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("notificaciones")
        db.get().addOnSuccessListener { res ->
            val documentCount = res.size()
            txt_Mostrador.text = "$documentCount"
        }.addOnFailureListener { exception ->
            Log.e("ContadorNotificaciones", "Error al contar las notificaciones", exception)
        }
    }

    fun isCamposLlenos(
        nombreServicioED: EditText,
        descripcionServicioED: EditText,
        tituloNotificacionED: EditText,
        descripcionNotificacionED: EditText,
    ): Boolean {
        val text1 = nombreServicioED.text.toString().trim()
        val text2 = descripcionServicioED.text.toString().trim()

        if (text1.isNotEmpty() && text2.isNotEmpty()) {
            tituloNotificacionED.setText(text1)
            descripcionNotificacionED.setText(text2)
        }
        return text1.isNotEmpty() && text2.isNotEmpty()
    }

}