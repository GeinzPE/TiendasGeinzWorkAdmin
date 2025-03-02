package com.geinzTienda.tiendasgeinzadmin.Fragmentos

import NotificationRS
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.tiendasgeinzadmin.Horarios_tienda
//import com.example.tiendasgeinzadmin.FCM.NotificationRS
import com.example.tiendasgeinzadmin.agregarQrPagos
import com.example.tiendasgeinzadmin.constantes.constantes_permisos
import com.example.tiendasgeinzadmin.editarPerfil
import com.geinzTienda.tiendasgeinzadmin.databinding.FragmentPerfilFrBinding
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class perfil_fr : Fragment() {
    private lateinit var binding: FragmentPerfilFrBinding
    private lateinit var mcontex: Context
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onAttach(context: Context) {
        mcontex = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPerfilFrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constantes_permisos.SolicitarPermisoNotificacion(mcontex, permisoNotificaion)
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        obtenerDatosFirestore(firebaseAuth.uid.toString())
        binding.agreagrQR.setOnClickListener {
            startActivity(Intent(mcontex, agregarQrPagos::class.java))
        }

        binding.enviarnoti.setOnClickListener {
            nifyCliente("hla pueva")
        }
        binding.horarios.setOnClickListener {
            startActivity(Intent(mcontex, Horarios_tienda::class.java))
        }

        binding.editarPerfil.setOnClickListener {
            startActivity(Intent(mcontex, editarPerfil::class.java))
        }
        pasamosToken()

        listNotificationChannels(mcontex)
        cleanUpNotificationChannels(mcontex)
    }

    private fun nifyCliente(s: String) {
        lifecycleScope.launch {
            val accessToken = getAccessToken(requireContext())
            println("Access Token: $accessToken")
            if (accessToken != null) {
                sendNotification(requireContext(), s, accessToken) // Pasa el accessToken aquí
            } else {
                Toast.makeText(requireContext(), "Error obteniendo el token", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    fun cleanUpNotificationChannels(context: Context) {
        // Primero, lista todos los canales para verificar
        listAllNotificationChannels(context)

        // Luego, elimina todos los canales excepto 'Tiendas'
        deleteAllNotificationChannelsExcept(context, "Tiendas")
    }

    fun listAllNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channels = notificationManager.notificationChannels
            for (channel in channels) {
                println("Channel ID existente: ${channel.id}")
            }
        }
    }

    fun deleteAllNotificationChannelsExcept(context: Context, keepChannelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channels = notificationManager.notificationChannels

            for (channel in channels) {
                if (channel.id != keepChannelId) {
                    notificationManager.deleteNotificationChannel(channel.id)
                    println("Canal eliminado: ${channel.id}")
                }
            }
        }
    }

    fun listNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channels = notificationManager.notificationChannels
            for (channel in channels) {
                println("Channel ID existentes: ${channel.id}")
            }
        }
    }


    suspend fun sendNotification(context: Context, mensaje: String, accessToken: String) {
        val notification = JSONObject().apply {
            put("title", "holaaaa")
            put("body", mensaje)
            put("sound", "default")
        }

        val messagePayload = JSONObject().apply {
            put(
                "token",
                "f4V0OzFsSImrCx5XxkmkjA:APA91bHKPWNauw9QGzlqRhpsOUc0l5SPCg_iAYOQ8YHo0KKsEWy6POHeVFQQ4T0Ye101vPMsPydIGTOJhhxO38EJKCn2KyySNSzPlMb2KLPDuzEjgpdFsR-QP_gVHnXfSh8-Ogz6q1jQ"
            )
            put("notification", notification)
        }

        val url = "https://fcm.googleapis.com/v1/projects/geinzworkapp/messages:send"

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, messagePayload,
            { response ->
                // Maneja la respuesta exitosa aquí
                println("Response: $response")
                Toast.makeText(context, "Notificación enviada exitosamente", Toast.LENGTH_SHORT)
                    .show()
            },
            { error ->
                // Maneja el error aquí
                println("Error al enviar la noti: ${error.message}")
                Toast.makeText(context, "Error al enviar la notificación", Toast.LENGTH_SHORT)
                    .show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf(
                    "Authorization" to "Bearer $accessToken",
                    "Content-Type" to "application/json; charset=UTF-8"
                )
            }
        }

        Volley.newRequestQueue(context).add(jsonObjectRequest)
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


    private fun obtenerDatosFirestore(id: String) {
        val db = FirebaseFirestore.getInstance().collection("Tiendas").document(id)
        db.get()
            .addOnSuccessListener { res ->
                if (res.exists()) {
                    val data = res.data
                    val categoria = data?.get("categoria") as? String
                    val descripcion = data?.get("descripcion") as? String
                    val estado = data?.get("estado") as? String
                    val estrellas = data?.get("estrellas") as? String
                    val horario = data?.get("horario") as? String
                    val id = data?.get("id") as? String
                    val imgPerfil = data?.get("imgPerfil") as? String
                    val imgPortada = data?.get("imgPortada") as? String
                    val nombre = data?.get("nombre") as? String
                    val numero = data?.get("numero") as? String
                    val seguidores = data?.get("seguidores") as? String
                    val subcategorias = data?.get("subcategorias") as? String
                    val ubicacion = data?.get("ubicacion") as? String
                    val zona = data?.get("zona") as? String

                    binding.nombre.text = nombre
                    binding.categoria.text = categoria
                    binding.descripcion.text = descripcion
                    binding.estrellas.text = estrellas
                    binding.horario.text = horario
                    binding.estrellas.text = estado
                    binding.numero.text = numero
                    binding.seguidores.text = seguidores
                    binding.sucategoria.text = subcategorias
                    binding.ubicacion.text = ubicacion
                    binding.zona.text = zona

                    try {
                        Glide.with(mcontex)
                            .load(imgPerfil)
                            .into(binding.imgPerfilUser)

                        Glide.with(mcontex)
                            .load(imgPortada)
                            .into(binding.portada)
                    } catch (e: Exception) {
                        println("no se pudo setear las imagenes")
                    }
                }

            }
            .addOnFailureListener {
                println("error al obtener los datos del usuario")
            }
    }

    private fun pasamosToken() {
        val muid = "${firebaseAuth.uid}"
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                val token = hashMapOf<String, Any>(
                    "token" to "$token"
                )
                val db = FirebaseFirestore.getInstance().collection("Tiendas").document(muid)

                db.update(token)
                    .addOnSuccessListener {
                        println("token agregado correctamente")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(mcontex, "error $e", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(mcontex, "error $e", Toast.LENGTH_SHORT).show()
            }
    }

    val permisoNotificaion =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { esConcedido -> }

}