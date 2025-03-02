package com.example.tiendasgeinzadmin.Fragmentos.vistas

import NotificationRS
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiendasgeinzadmin.DataClass.dataclasPedidos
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.databinding.ActivityAceptarRechazarPedidosGeneralesBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class aceptar_rechazar_pedidos_generales : AppCompatActivity() {
    private lateinit var binding: ActivityAceptarRechazarPedidosGeneralesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAceptarRechazarPedidosGeneralesBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val idUSer = intent.getStringExtra("idUSer").toString()
        binding.AceptarPedididoReserva.setOnClickListener {

        }
        binding.RechazarPedididoReserva.setOnClickListener {
//            enviarNoticacion(
//                idUSer, "","","Se rechazó el pedido",
//                "Se rechazó el pedido realizado por el usuario"
//            )
        }
    }

//    fun enviarNoticacion(idUSer:String,enviado1: String, vista: String, Titulo: String, texto: String) {
//        GlobalScope.launch {
//            val notificacionRS = NotificationRS()
//            obtenerTokeUSer(idUSer){tkn->
//                GlobalScope.launch {
//                    try {
//                        notificacionRS.sendNotification_con_parametros(
//                            "idUSer",
//                            idUSer,
//                            "REPORTE",
//                            this@aceptar_rechazar_pedidos_generales,
//                            tkn,
//                            Titulo,
//                            texto
//                        )
//                    } catch (e: Exception) {
//                        println("Error sending notification: ${e.message}")
//                    }
//                }
//            }
//        }
//
//    }

    private fun obtenerTokeUSer(idUSer: String, tokenUser: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
            .collection("Trabajadores_Usuarios_Drivers")
            .document("tokens")
            .collection("tokens")
            .document(idUSer)

        db.get().addOnSuccessListener { res ->
            if (res.exists()) {
                val data = res.data
                val token = data?.get("token") as? String ?: ""
                tokenUser(token)
            } else {
                tokenUser("")
            }
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            tokenUser("") // En caso de error, devolvemos un string vacío
        }
    }

}