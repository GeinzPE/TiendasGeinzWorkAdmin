package com.geinzTienda.tiendasgeinzadmin.Fragmentos.vistas

import NotificationRS
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.geinzTienda.tiendasgeinzadmin.DataClass.dataclassitem_pedidos
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.databinding.ActivityCambarEstadoDesignarDeliveryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class cambar_estado_designar_delivery : AppCompatActivity() {
    private lateinit var binding: ActivityCambarEstadoDesignarDeliveryBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val listaItems = mutableListOf<dataclassitem_pedidos>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCambarEstadoDesignarDeliveryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val idUser = intent.getStringExtra("idUser").toString()
        val idPedido = intent.getStringExtra("idPedido").toString()
        val tipoPedido = intent.getStringExtra("tipoPedido").toString()
        setearProcutos(idUser, idPedido, firebaseAuth.uid.toString(), tipoPedido)
    }

    private fun setearProcutos(
        idUsuario: String,
        idPedido: String,
        idTienda: String,
        tipo: String,
    ) {
        val compraTiendaRef =
            FirebaseDatabase.getInstance().getReference("CompraTienda").child(idTienda)
                .child(idUsuario).child(tipo).child(idPedido)
        compraTiendaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val nombreTienda =
                        dataSnapshot.child("nombreTienda").getValue(String::class.java) ?: ""
                    val metodoEntrega =
                        dataSnapshot.child("metodoEntrega").getValue(String::class.java)
                    val metodoPago =
                        dataSnapshot.child("metodoPago").getValue(String::class.java) ?: ""
                    val direccion =
                        dataSnapshot.child("direccion").getValue(String::class.java) ?: ""
                    val comentario_adicional =
                        dataSnapshot.child("comentario_adicional").getValue(String::class.java)
                    val numerotTienda =
                        dataSnapshot.child("numerotTienda").getValue(String::class.java) ?: ""
                    val tiendaDireccion =
                        dataSnapshot.child("tiendaDireccion").getValue(String::class.java) ?: ""
                    val fecha = dataSnapshot.child("fecha").getValue(String::class.java) ?: ""
                    val estado = dataSnapshot.child("estado").getValue(String::class.java) ?: ""
                    val totalCancelar =
                        dataSnapshot.child("totalCancelar").getValue(String::class.java) ?: ""

                    val data = dataclassitem_pedidos(
                        estado = estado,
                        fecha = fecha,
                        tipo = tipo,
                        codgigoPedido = idPedido,
                        nombreTienda = nombreTienda,
                        metodEntrega = metodoEntrega,
                        metodoPago = metodoPago,
                        direccion = direccion,
                        comentario = comentario_adicional,
                        tiendaNumero = numerotTienda,
                        tiendaDireccion = tiendaDireccion,
                        total = totalCancelar
                    )

                    binding.codigoPedido.text = data.codgigoPedido
                    binding.nombreTienda.text = data.codgigoPedido
                    binding.metodoEntrega.text = data.metodEntrega
                    binding.metodoPago.text = data.metodoPago
                    binding.direccion.text = data.direccion
                    binding.comentario.text = data.comentario
                    binding.tiendaNombre.text = data.nombreTienda
                    binding.tiendaNumero.text = data.tiendaNumero
                    binding.TiendaDireccion.text = data.tiendaDireccion
                    binding.totalPagar.text = data.total
                    binding.aceptar.setOnClickListener {
                        aceptarPedido(data, idUsuario, firebaseAuth.uid.toString(), idPedido, tipo)
                    }
                } else {
                    Log.d(
                        "ARTICULO",
                        "No se encontraron datos para el artículo con ID"
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(
                    "ARTICULO",
                    "Error al leer datos del artículo: ${databaseError.message}"
                )
            }
        })

    }


    private fun aceptarPedido(
        data: dataclassitem_pedidos,
        idUsuario: String,
        idTienda: String,
        idPedido: String,
        tipo: String,
    ) {
        val compraTiendaRef =
            FirebaseDatabase.getInstance().getReference("CompraTienda").child(idTienda)
                .child(idUsuario).child(tipo).child(idPedido)
        val pedidoUserRef =
            FirebaseDatabase.getInstance().getReference("PedidosUsuario").child(idUsuario)
                .child("tiendas").child(idTienda).child(tipo).child(idPedido)

        actualizarDatosRealTime(compraTiendaRef, pedidoUserRef, data, idUsuario, idTienda)
    }

    private fun actualizarDatosRealTime(
        compraTiendaRef: DatabaseReference,
        pedidoUserRef: DatabaseReference,
        data: dataclassitem_pedidos,
        idUsuario: String,
        idTienda: String,
    ) {

        val hashMapEstado = hashMapOf(
            "estado" to "Aceptado"
        )
        compraTiendaRef.updateChildren(hashMapEstado as Map<String, Any>)
            .addOnSuccessListener {
                when (binding.metodoEntrega.text.toString()) {
                    "Recojo en tienda" -> {
                        Log.d("pedido", "pedido agregado correctamente")
                        enviarNotificacion(
                            idUsuario,
                            data,
                            "Tu pedido fue aceptado. Puedes pasar a recogerlo dentro del horario de atención."
                        )
                    }

                    "Delivery" -> {
                        asignarDriver(
                            { idDriver, nombreDriver ->
                                Log.d("driver", "driver escojido correctamente")
                                enviarNotificacion(
                                    idUsuario,
                                    data,
                                    "Tu pedido fue aceptado. Estará listo en el tiempo estimado. Haz clic para ver más información sobre tu pedido."
                                )

                                val hasmapDriverRealtime = hashMapOf(
                                    "idDriver" to idDriver
                                )
                                compraTiendaRef.updateChildren(hasmapDriverRealtime as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Log.d("driver", "llenado idCorrectamente1")
                                        pedidoUserRef.updateChildren(hashMapEstado as Map<String, Any>)
                                            .addOnSuccessListener {
                                                Log.d(
                                                    "ACTUALIZACIÓN",
                                                    "Estado actualizado correctamente a 'Aceptado' en PedidosUsuario"
                                                )
                                            }
                                            .addOnFailureListener { error ->
                                                Log.e(
                                                    "ACTUALIZACIÓN",
                                                    "Error al actualizar el estado en PedidosUsuario: ${error.message}"
                                                )
                                            }
                                        pedidoUserRef.updateChildren(hasmapDriverRealtime as Map<String, Any>)
                                            .addOnSuccessListener {
                                                Log.d("driver", "llenado idCorrectament2")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.d(
                                                    "driver_error",
                                                    "error al pasar el id del drive1"
                                                )
                                            }
                                    }
                                    .addOnFailureListener {
                                        Log.d("driver_error", "error al pasar el id del drive2")
                                    }


                                val listaDriver = FirebaseFirestore.getInstance()
                                    .collection("Trabajadores_Usuarios_Drivers").document("drivers")
                                    .collection("drivers").document(idDriver)
                                    .collection("pendiente")

                                val hashMap = hashMapOf<String, Any>(
                                    "direccion" to data.direccion!!,
                                    "estado" to data.estado!!,
                                    "fecha" to data.fecha!!,
                                    "idDriver" to idDriver!!,
                                    "idPedido" to data.codgigoPedido!!,
                                    "idTienda" to idTienda!!,
                                    "idUSer" to idUsuario!!,
                                    "nombreTienda" to data.nombreTienda!!,
                                    "tipo" to data.tipo!!
                                )

                                listaDriver.add(hashMap)
                                    .addOnSuccessListener { documentReference ->
                                        val newDocId = documentReference.id
                                        val updateHash = hashMapOf<String, Any>(
                                            "idDocumento" to newDocId
                                        )

                                        listaDriver.document(newDocId).update(updateHash)
                                            .addOnSuccessListener {
                                                Log.d("idDriver", "id collection succes")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.d(
                                                    "driver_error",
                                                    "Error al agregar el ID al documento"
                                                )
                                            }

                                    }
                                    .addOnFailureListener { e ->
                                        Log.d(
                                            "driver_error",
                                            "Error al agregar el ID al documento ${e.message}"
                                        )
                                    }
                            }, { ocupado ->
                                if (!ocupado) {
                                    Toast.makeText(
                                        this,
                                        "No se pudo asignar un driver, todos están ocupados.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }
                }
            }
    }


    private fun enviarNotificacion(idUSer: String, data: dataclassitem_pedidos, msj: String) {
        obtenerTokenUsuario(idUSer) { token ->
            GlobalScope.launch {
                val notificationRS = NotificationRS()
                notificationRS.sendNotification_sin_parametros(
                    this@cambar_estado_designar_delivery,
                    token.toString(),
                    "Pedido aceptado de ${data.nombreTienda}!",
                    msj
                )
            }

        }
    }

    private fun asignarDriver(nombre: (String, String) -> Unit, ocupados: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance().collection("Trabajadores_Usuarios_Drivers")
            .document("drivers").collection("drivers")

        db.whereEqualTo("disponible", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    println("No hay drivers disponibles en la colección.")
                    Toast.makeText(this, "Todos los drivers están ocupados", Toast.LENGTH_SHORT)
                        .show()
                    ocupados(false)
                    return@addOnSuccessListener
                }

                val randomIndex = Random.nextInt(querySnapshot.size())
                val randomDriver = querySnapshot.documents[randomIndex]
                val idDocumento = randomDriver.id
                val nombreDriver = randomDriver.getString("nombre") ?: "Driver desconocido"

                val dbUpdate =
                    FirebaseFirestore.getInstance().collection("Trabajadores_Usuarios_Drivers")
                        .document("drivers").collection("drivers").document(idDocumento)

                val dbPendienteDriver =
                    FirebaseFirestore.getInstance().collection("Trabajadores_Usuarios_Drivers")
                        .document("drivers").collection("drivers").document(idDocumento)
                        .collection("pendiente")

                dbPendienteDriver.get().addOnSuccessListener { querySnapshot ->
                    val count = querySnapshot.size()
                    if (count >= 12) {
                        val hashMap = hashMapOf(
                            "disponible" to false
                        )
                        dbUpdate.update(hashMap as Map<String, Any>)
                            .addOnSuccessListener {
                                Log.d(
                                    "ACTUALIZACIÓN",
                                    "Estado del driver actualizado correctamente."
                                )
                                ocupados(true)
                            }
                            .addOnFailureListener { e ->
                                Log.e(
                                    "ACTUALIZACIÓN",
                                    "Error al actualizar estado del driver: $e"
                                )
                                ocupados(false)
                            }
                    }
                    Log.d("Document Count", "Hay $count registros en este documento.")

                }.addOnFailureListener { exception ->
                    Log.w("Error", "Error obteniendo los datos", exception)
                }

                nombre(idDocumento, nombreDriver)
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error al obtener drivers disponibles: $exception")
                Toast.makeText(this, "Error al obtener drivers disponibles", Toast.LENGTH_SHORT)
                    .show()
                ocupados(false)
            }
    }

    private fun obtenerTokenUsuario(idUser: String, callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance().collection("Trabajadores_Usuarios_Drivers")
            .document("usuarios").collection("usuarios")
            .document(idUser)

        val db2 = FirebaseFirestore.getInstance().collection("Trabajadores_Usuarios_Drivers")
            .document("trabajadores").collection("trabajadores")
            .document(idUser)

        db.get().addOnSuccessListener { res ->
            if (res.exists()) {
                val data = res.data
                val token = data?.get("token") as? String ?: ""
                if (token.isNotEmpty()) {
                    callback(token)
                } else {
                    db2.get().addOnSuccessListener { res2 ->
                        if (res2.exists()) {
                            val data2 = res2.data
                            val token2 = data2?.get("token") as? String ?: ""
                            if (token2.isNotEmpty()) {
                                callback(token2)
                            } else {
                                callback(null)
                            }
                        } else {
                            callback(null)
                        }
                    }.addOnFailureListener {
                        callback(null)
                    }
                }
            } else {
                db2.get().addOnSuccessListener { res2 ->
                    if (res2.exists()) {
                        val data2 = res2.data
                        val token2 = data2?.get("token") as? String ?: ""
                        if (token2.isNotEmpty()) {
                            callback(token2)
                        } else {
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                }.addOnFailureListener {
                    callback(null)
                }
            }
        }.addOnFailureListener {
            callback(null)
        }
    }
}