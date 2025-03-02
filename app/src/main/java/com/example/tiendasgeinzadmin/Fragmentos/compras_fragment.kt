package com.example.tiendasgeinzadmin.Fragmentos

import NotificationRS
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tiendasgeinzadmin.DataClass.dataClassFiltradoCompras
import com.example.tiendasgeinzadmin.DataClass.dataclasPedidos
import com.example.tiendasgeinzadmin.DataClass.dataclassProductosCompras
import com.example.tiendasgeinzadmin.adapter.adapterFiltradoCompras
import com.example.tiendasgeinzadmin.adapter.adapterProductosComprados
import com.example.tiendasgeinzadmin.adapter.adapter_obtener_pedidos
import com.example.tiendasgeinzadmin.constantes.obtener_enviar_notification
import com.geinzTienda.tiendasgeinzadmin.DataClass.dataclassitem_pedidos
import com.geinzTienda.tiendasgeinzadmin.databinding.BottomsheetAceptarRechazarPedidosGeneralBinding
import com.geinzTienda.tiendasgeinzadmin.databinding.FragmentComprasFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDragHandleView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.random.Random

class compras_fragment : Fragment() {
    private lateinit var binding: FragmentComprasFragmentBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mcontex: Context
    private lateinit var adapterFiltrado: adapterFiltradoCompras
    private lateinit var bottomSheet: BottomSheetDragHandleView
    private lateinit var adapterobtenerPedidos: adapter_obtener_pedidos
    private lateinit var adapterProductosCompras: adapterProductosComprados
    private lateinit var dialog: BottomSheetDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mcontex = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentComprasFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        setarDatosTienda()
        obtenerPedidosTiempoReal()
        inicializarCategoriaS()

    }

    private val lista: List<dataClassFiltradoCompras> = listOf(
        dataClassFiltradoCompras("Todos"),
        dataClassFiltradoCompras("Pendientes"),
        dataClassFiltradoCompras("Aceptados"),
        dataClassFiltradoCompras("Rechazados"),
        dataClassFiltradoCompras("Completados"),
        dataClassFiltradoCompras("Incompletos")
    )

    private fun inicializarCategoriaS() {
        val recicle = binding.categroiasFiltrado
        adapterFiltrado = adapterFiltradoCompras(lista) { item ->
            if (item.nombreCategoria.equals("Todos")) {
//                obtener_TodasPromociones()
//                binding.filtradoCategorias.isVisible = false
//                binding.textoNoEncontrado.isVisible = false
            } else {
//                binding.search.setText("")
//                binding.textoNoEncontrado.isVisible = false
//                binding.categoriaSelcionada.text = item.nombreCategoria
//                binding.filtradoCategorias.isVisible = true
//                binding.recicleView.isVisible = false
//                binding.prograsvar.isVisible = true
//                filtraResultado(item.nombreCategoria.toString().toLowerCase())
            }


        }
        recicle.layoutManager = LinearLayoutManager(mcontex, LinearLayoutManager.HORIZONTAL, false)
        recicle.adapter = adapterFiltrado
    }

    private fun setarDatosTienda() {
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString())
        db.get().addOnSuccessListener { res ->
            if (res.exists()) {
                val data = res.data
                val nombre = data?.get("nombre") as? String ?: ""
                val imgPerfil = data?.get("imgPerfil") as? String ?: ""
                binding.includeCabeazero.nombreTienda.text = nombre
                try {
                    Glide.with(mcontex)
                        .load(imgPerfil)
                        .into(binding.includeCabeazero.imgPerfilUser)
                } catch (e: Error) {
                    Log.e("error_img", e.toString())
                }
            } else {
                Log.e("error", "error al obtener los datos de la tienda")
            }
        }
    }


    private fun obtenerPedidosTiempoReal() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("CompraTienda")
        val idTienda = firebaseAuth.uid.toString()
        val comprasList = mutableListOf<dataclasPedidos>()

        databaseRef.child(idTienda).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                comprasList.clear()

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val comprasSnapshot = userSnapshot.child("compra")
                        if (comprasSnapshot.exists()) {
                            for (compra in comprasSnapshot.children) {
                                val tipoRealizado =
                                    compra.child("tipoRealizado").value as? String ?: ""
                                val idPedido = compra.child("idPedido").value as? String ?: ""
                                val nombre = compra.child("nombre").value as? String ?: ""
                                val totalCancelar =
                                    compra.child("totalCancelar").value as? String ?: ""
                                val metodoPago = compra.child("metodoPago").value as? String ?: ""
                                val estadoPedido =
                                    compra.child("estadoPedido").value as? String ?: ""
                                val totalDriver = compra.child("totalDriver").value as? String ?: ""
                                val totalProductos =
                                    compra.child("totalProductos").value as? String ?: ""
                                val localidad = compra.child("localidad").value as? String ?: ""
                                val direccion = compra.child("direccion").value as? String ?: ""
                                val metodoEntrega =
                                    compra.child("metodoEntrega").value as? String ?: ""
                                val numero = compra.child("numero").value as? String ?: ""
                                val idTienda = compra.child("idTienda").value as? String ?: ""
                                val idUSer = compra.child("idUSer").value as? String ?: ""
                                val fecha = compra.child("fecha").value as? String ?: ""
                                val hora = compra.child("hora").value as? String ?: ""
                                val nombreTienda = compra.child("nombreTienda").value as? String ?: ""

                                if (!tipoRealizado.isNullOrEmpty() &&
                                    !idPedido.isNullOrEmpty() &&
                                    !nombre.isNullOrEmpty() &&
                                    !totalCancelar.isNullOrEmpty() &&
                                    !metodoPago.isNullOrEmpty() &&
                                    !estadoPedido.isNullOrEmpty() &&
                                    !totalDriver.isNullOrEmpty() &&
                                    !totalProductos.isNullOrEmpty() &&
                                    !metodoEntrega.isNullOrEmpty() &&
                                    !numero.isNullOrEmpty()&& !nombreTienda.isNullOrEmpty()
                                ) {
                                    val compraObj = dataclasPedidos(
                                        numeroPedidos = idPedido,
                                        nombreUSer = nombre,
                                        total = totalCancelar,
                                        cantidadArticulos = totalProductos,
                                        metodoPago = metodoPago,
                                        estados = estadoPedido,
                                        numeroUser = numero,
                                        tipoEntrega = metodoEntrega,
                                        tototalDriver = totalDriver,
                                        totalProductos = totalProductos,
                                        ubicacionUser = direccion,
                                        localidadUSer = localidad,
                                        tipoPedido = tipoRealizado,
                                        fecha = fecha,
                                        hora = hora,
                                        idTienda = idTienda,
                                        idUSer = idUSer,
                                        nombreTienda=nombreTienda,"compra"
                                    )
                                    comprasList.add(compraObj)
                                }
                            }
                        }
                    }
                    inicializarRecycle(comprasList)

                    if (comprasList.isNotEmpty()) {
                        comprasList.forEach { compra ->
                            println("Pedido obtenido: $compra")
                        }
                    } else {
                        println("No se encontraron compras para esta tienda.")
                    }
                } else {
                    println("No se encontraron datos para esta tienda.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener las compras: ${error.message}")
            }
        })
    }

    private fun inicializarRecycle(lista: MutableList<dataclasPedidos>) {
        val recicle = binding.reciclePedidos
        adapterobtenerPedidos = adapter_obtener_pedidos(lista) { item ->
            dialog = BottomSheetDialog(mcontex)
            aceptarRechazarPedido(item)
            dialog.show()

        }

        recicle.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recicle.adapter = adapterobtenerPedidos
    }

    private fun aceptarRechazarPedido(
        item: dataclasPedidos
    ) {
        val bindingBottomShet =
            BottomsheetAceptarRechazarPedidosGeneralBinding.inflate(LayoutInflater.from(mcontex))
        val view = bindingBottomShet.root
        bottomSheet = bindingBottomShet.cerrar
        bindingBottomShet.datosUserRealizado.numeroPedido.text = item.numeroPedidos
        bindingBottomShet.datosUserRealizado.tipoEntrega.text = item.tipoEntrega
        bindingBottomShet.datosUserRealizado.linealUbiEntrega.isVisible = false
        bindingBottomShet.datosUserRealizado.linealCordenadasEntrega.isVisible = false
        if (item.tipoEntrega.equals("Delivery")) {
            bindingBottomShet.datosUserRealizado.linealUbiEntrega.isVisible = true
            bindingBottomShet.datosUserRealizado.linealCordenadasEntrega.isVisible = true
            bindingBottomShet.datosUserRealizado.ubicaionEntrega.text = item.ubicacionUser
            //aplicar las cordenadas para copiar
        }
        bindingBottomShet.datosUserRealizado.metodoPago.text = item.metodoPago
        bindingBottomShet.tipoVenta.text = item.tipoPedido
        bindingBottomShet.datosUserRealizado.nombreCompleto.text = item.nombreUSer
        bindingBottomShet.datosUserRealizado.numeroContacto.text = item.numeroUser
        bindingBottomShet.datosUserRealizado.LocalidadUSer.text = item.localidadUSer
        obtenerProductos(item.idUSer.toString(), item.numeroPedidos.toString(), bindingBottomShet)
        val tototalDriver = item.tototalDriver?.toFloatOrNull() ?: 0f
        val totalProductos = item.totalProductos?.toFloatOrNull() ?: 0f
        obtenerTotalCancelar(tototalDriver, totalProductos, bindingBottomShet)


        bindingBottomShet.AceptarPedididoReserva.setOnClickListener {
            aceptarPedido(item,item.idUSer.toString(),item.idTienda.toString(),item.numeroPedidos.toString(),item.tipoCompra_o_reserva.toString(),bindingBottomShet)
        }
        bindingBottomShet.RechazarPedididoReserva.setOnClickListener {
            notificarUSer(
                requireActivity(),
                item,
                "Se rechazó el pedido",
                "Se rechazó el pedido realizado por el usuario"
            )
        }
        dialog.setContentView(view)
    }

    private fun notificarUSer(
        context: Context,
        item: dataclasPedidos,
        tituloNotificacionED: String,
        descripcionNotificacionED: String
    ) {
        GlobalScope.launch {
            try {
                val noti = NotificationRS()
                // Asegurarse de que el contexto es una actividad
                (context as? Activity)?.let { activity ->
                    noti.sendNotification_sin_parametros(
                        activity,
                        item.idUSer.toString(),
                        "$tituloNotificacionED",
                        "$descripcionNotificacionED"
                    )
                    println("Se mandó la notificación")
                } ?: run {
                    println("Error: El contexto no es una actividad")
                }
            } catch (e: Exception) {
                println("Error enviando notificación para el token ${item.idUSer.toString()}: ${e.message}")
            }
        }
        obtener_enviar_notification.obtenerTokenUsuario(
            tituloNotificacionED,
            "$descripcionNotificacionED",
            mcontex,
            firebaseAuth.uid.toString(),
            "idPromo",
            "PROMO_SUGERIDAS",
            item.idUSer.toString(),
            "idTienda",
            "",
            "idPromo",
        )
    }


    private fun obtenerProductos(
        idUser: String,
        idPedidos: String,
        bindingBottomShet: BottomsheetAceptarRechazarPedidosGeneralBinding
    ) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("CompraTienda")
        val idTienda = FirebaseAuth.getInstance().uid ?: return
        Log.d("ruta", "$idTienda, $idUser, compra, $idPedidos")

        databaseRef.child(idTienda).child(idUser).child("compra").child(idPedidos)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val productosString =
                            snapshot.child("productos").getValue(String::class.java)

                        if (!productosString.isNullOrEmpty()) {
                            val productosComprados = mutableListOf<dataclassProductosCompras>()
                            val productosJSON = JSONObject(productosString)
                            val keys = productosJSON.keys()
                            var productosProcesados = 0 // Contador para manejar asincronía
                            val totalProductos = productosJSON.length()

                            while (keys.hasNext()) {
                                val idProducto = keys.next()
                                val productoData = productosJSON.getJSONObject(idProducto)
                                val cantidad = productoData.optInt("cantidad", 0)
                                val precio = productoData.optInt("precio", 0)

                                obtenerImgArticulosNombre(
                                    idProducto,
                                    idTienda
                                ) { img, NMB, descuentoBoolean, cantidadDescuento ->
                                    // Agregar producto a la lista cuando la info está disponible
                                    productosComprados.add(
                                        dataclassProductosCompras(
                                            img,
                                            NMB,
                                            idProducto,
                                            cantidad,
                                            precio, descuentoBoolean, cantidadDescuento
                                        )
                                    )
                                    productosProcesados++

                                    // Si todos los productos fueron procesados, inicializar la UI
                                    if (productosProcesados == totalProductos) {
                                        inicializarProductosComprados(
                                            productosComprados,
                                            bindingBottomShet
                                        )
                                    }
                                }
                            }
                        } else {
                            Log.d("Firebase", "No se encontraron productos en la compra.")
                        }
                    } else {
                        Log.d(
                            "Firebase",
                            "No se encontraron datos para la compra con ID: $idPedidos"
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error al obtener los datos: ${error.message}")
                }
            })
    }


    private fun obtenerImgArticulosNombre(
        idArticulo: String,
        idTienda: String,
        imgArtNMB: (String, String, Boolean, Number) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance().collection("Tiendas").document(idTienda)
            .collection("articulos").document(idArticulo)
        db.get().addOnSuccessListener { res ->
            if (res.exists()) {
                val data = res.data
                val imgArtSTR = data?.get("imgArticulo") as? String ?: ""
                val nombreART = data?.get("nombreArticulo") as? String ?: ""
                val descuentoBoolean = data?.get("descuento") as? Boolean ?: false
                val cantidadDescuento = data?.get("cantidadDescuento") as? Number ?: 0
                imgArtNMB(imgArtSTR, nombreART, descuentoBoolean, cantidadDescuento)
            } else {
                imgArtNMB("", "", false, 0)
            }
        }.addOnFailureListener { e ->
            println("error al obtener la img $e")
        }

    }

    private fun inicializarProductosComprados(
        lista: MutableList<dataclassProductosCompras>,
        bindingBottomShet: BottomsheetAceptarRechazarPedidosGeneralBinding
    ) {
        val recicle = bindingBottomShet.reciclePedidos
        adapterProductosCompras = adapterProductosComprados(lista)

        recicle.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recicle.adapter = adapterProductosCompras
    }

    @SuppressLint("SetTextI18n")
    private fun obtenerTotalCancelar(
        driver: Float,
        productosTotal: Float,
        bindingBottomShet: BottomsheetAceptarRechazarPedidosGeneralBinding
    ) {
        bindingBottomShet.totalCancelarComprasReservas.TotalCancelar.text =
            "S/ ${driver + productosTotal}"
        bindingBottomShet.totalCancelarComprasReservas.totalDriver.text = "S/ $driver"
        bindingBottomShet.totalCancelarComprasReservas.TotalProductos.text = "S/ $productosTotal"
    }

    private fun aceptarPedido(
        data: dataclasPedidos,
        idUsuario: String,
        idTienda: String,
        idPedido: String,
        tipo: String,
        bindingBottomShet: BottomsheetAceptarRechazarPedidosGeneralBinding
    ) {
        val compraTiendaRef =
            FirebaseDatabase.getInstance().getReference("CompraTienda").child(idTienda)
                .child(idUsuario).child(tipo).child(idPedido)
        val pedidoUserRef =
            FirebaseDatabase.getInstance().getReference("PedidosUsuario").child(idUsuario)
                .child("tiendas").child(idTienda).child(tipo).child(idPedido)

        actualizarDatosRealTime(compraTiendaRef, pedidoUserRef, data, idUsuario, idTienda,bindingBottomShet)
    }

    private fun actualizarDatosRealTime(
        compraTiendaRef: DatabaseReference,
        pedidoUserRef: DatabaseReference,
        data: dataclasPedidos,
        idUsuario: String,
        idTienda: String,
        bindingBottomShet: BottomsheetAceptarRechazarPedidosGeneralBinding
    ) {
        val hashMapEstado = hashMapOf(
            "estado" to "Aceptado"
        )
        compraTiendaRef.updateChildren(hashMapEstado as Map<String, Any>)
            .addOnSuccessListener {
                when (bindingBottomShet.datosUserRealizado.tipoEntrega.text.toString()) {
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
                                    "direccion" to data.ubicacionUser!!,
                                    "estado" to data.estados!!,
                                    "fecha" to data.fecha!!,
                                    "idDriver" to idDriver!!,
                                    "idPedido" to data.numeroPedidos!!,
                                    "idTienda" to idTienda!!,
                                    "idUSer" to idUsuario!!,
                                    "nombreTienda" to data.nombreTienda!!,
                                    "tipo" to data.tipoPedido!!
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
                                        mcontex,
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


    private fun enviarNotificacion(idUSer: String, data: dataclasPedidos, msj: String) {
        obtenerTokenUsuario(idUSer) { token ->
            GlobalScope.launch {
                val notificationRS = NotificationRS()
                notificationRS.sendNotification_sin_parametros(
                    mcontex,
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
                    Toast.makeText(mcontex, "Todos los drivers están ocupados", Toast.LENGTH_SHORT)
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
                Toast.makeText(mcontex, "Error al obtener drivers disponibles", Toast.LENGTH_SHORT)
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