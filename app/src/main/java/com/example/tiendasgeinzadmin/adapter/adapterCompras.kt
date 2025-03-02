package com.geinzTienda.tiendasgeinzadmin.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.geinzTienda.tiendasgeinzadmin.DataClass.dataclassHistorial
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.databinding.ItemHistorialBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class adapterCompras(
    private var listaHistorial: MutableList<dataclassHistorial>,
    private val generarQR: (dataclassHistorial) -> Unit,
) : RecyclerView.Adapter<adapterCompras.viewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            ItemHistorialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listaHistorial.size
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = listaHistorial[position]
        holder.render(item, generarQR)
    }

    class viewHolder(private val binding: ItemHistorialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val codigoPedido = binding.codigoPedido
        val nombreTienda = binding.nombreTienda
        val nombreuser = binding.nombreuser
        val numerouser = binding.numerous
        val TipoTienda = binding.TipoTienda
        val horaus = binding.horaus
        val fechaus = binding.fechaus
        val estadouser = binding.estadous
        val entregaMetodo = binding.entregaMetodo
        val pagoMetodo = binding.pagometodo
        val porductos = binding.productos
        val Totaluser = binding.Totalus
        val descripcionus = binding.descripcionus
        val imgPerfilTienda = binding.imgTienda
        val containerListenr = binding.containerListenr
        val cancelarPedido = binding.cancelar
        val linealdescripcion = binding.linealdescripcion

        val localidaduser = binding.localidaduser
        val localidadTienda = binding.localidadTienda


        val relativeTienda = binding.relativeTienda
        val relativedriver = binding.relativedriver
        val relativecamino = binding.relativecamino
        val relativeentrega = binding.relativeentrega

        val linealDriver = binding.containerDriver
        val nombreDriver = binding.nombreDriver
        val nacionalidad_driver = binding.nacionalidadDriver
        val imgDriver = binding.imgDriver
        val whatsappDriver = binding.whatsappDriver
        val LlamadaDriver = binding.LlamadaDriver
        val generoDriver = binding.generoDriver

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("ResourceAsColor")
        fun render(
            dataclassHistorial: dataclassHistorial,
            generarQR: (dataclassHistorial) -> Unit,
        ) {

            TipoTienda.text = dataclassHistorial.tipoTienda
            codigoPedido.text = dataclassHistorial.idPedido
            nombreTienda.text = dataclassHistorial.nombreTienda
            nombreuser.text = dataclassHistorial.nombre
            numerouser.text = dataclassHistorial.numero
            horaus.text = dataclassHistorial.hora
            fechaus.text = dataclassHistorial.fecha
            estadouser.text = dataclassHistorial.estadp
            entregaMetodo.text = dataclassHistorial.metodoentrega
            pagoMetodo.text = dataclassHistorial.metodoPago
            localidaduser.text = dataclassHistorial.localidadUSer
            localidadTienda.text = dataclassHistorial.localidadTienda

            if(dataclassHistorial.estadp.equals("Aceptado")){
                relativeTienda.setBackgroundResource(R.drawable.round_icono_pedidos_estado)
            }
            Log.d("metodoEntrega", dataclassHistorial.metodoentrega.toString())
            if (dataclassHistorial.metodoentrega.equals("Recojo en tienda")) {
                relativedriver.isVisible = false
                relativecamino.isVisible = false
            }else{
                relativedriver.isVisible = true
                relativecamino.isVisible = true
            }


//            constantesCarrito.obtenerEstadoPedidoReservaCompra(
//                estadouser,
//                itemView.context,
//                relativeTienda,
//                relativedriver,
//                relativecamino,
//                relativeentrega,
//                dataclassHistorial,
//                nombreDriver,
//                generoDriver,
//                nacionalidad_driver,
//                imgDriver,
//                whatsappDriver,
//                LlamadaDriver,linealDriver
//            )
            actualizarTextViewConProductos(dataclassHistorial, porductos)
//            constantesCarrito.obtnerFotoPErfilTienda(
//                itemView.context,
//                dataclassHistorial.idTienda.toString(),
//                imgPerfilTienda
//            )
            Totaluser.text = dataclassHistorial.total

            containerListenr.setOnClickListener {
                generarQR(dataclassHistorial)
            }
        }

        private fun obtenerDatosDriver(
            dataclassHistorial: dataclassHistorial,
            nombreDriver: TextView,
            generoDriver: TextView,
            nacionalidadDriver: TextView,
            imgDriver: CircleImageView,
            buttonWhatsapp: ImageView,
            llamar: ImageView,
        ) {
            val firestore = FirebaseFirestore.getInstance().collection("driver")
                .document(dataclassHistorial.idDriver.toString())
            firestore.get().addOnSuccessListener { res ->
                if (res.exists()) {
                    // Obtener los datos del documento
                    val data = res.data
                    val nombre = data?.get("nombre") as? String
                    val imgPerfilDriver = data?.get("imagen_perfil") as? String
                    val numeroDriver = data?.get("telefono") as? String
                    val disponible = data?.get("disponible") as? Boolean
                    val genero = data?.get("genero") as? String
                    val nacionalidad = data?.get("nacionalidad") as? String

                    // Setear los valores en los TextViews y la imagen
                    nombreDriver.text = nombre ?: "N/A"
                    nacionalidadDriver.text = nacionalidad ?: "N/A"
                    generoDriver.text = genero ?: "N/A"

                    try {
                        Glide.with(itemView.context)
                            .load(imgPerfilDriver)
                            .into(imgDriver)
                    } catch (e: Exception) {
                        println("Error al setear la imagen: ${e.message}")
                    }

                    // Configurar el botón de WhatsApp
                    buttonWhatsapp.setOnClickListener {
//                        constantes.contactarWhatsapp(
//                            numeroDriver.toString(),
//                            "Hola, estoy esperando mi pedido ${dataclassHistorial.idPedido}",
//                            itemView.context
//                        )
                    }

                    // Configurar el botón de llamada
                    llamar.setOnClickListener {
//                        constantes.llamarCliente(
//                            itemView.context,
//                            numeroDriver.toString(),
//                            REQUEST_CALL_PHONE = 1
//                        )
                    }

                } else {
                    println("El documento del driver no existe.")
                }
            }.addOnFailureListener { exception ->
                println("Error al obtener los datos del driver: ${exception.message}")
            }
        }


        private fun obtenerItems(
            idTienda: String,
            idArticulo: String,
            cantidad: String,
            onResult: (String) -> Unit,
        ) {
            val db = FirebaseDatabase.getInstance().getReference("Articulos").child(idTienda)
                .child(idArticulo)
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val nombre = snapshot.child("nombreArticulo").getValue(String::class.java)
                        val precio = snapshot.child("precio").getValue(String::class.java)
                        if (nombre != null && precio != null) {
                            val resultado =
                                "Nombre: $nombre\nPrecio: $precio\nCantidad: $cantidad\n\n"
                            println("el id del articulo es $idArticulo")
                            onResult(resultado)
                        } else {
                            onResult("No se pudieron obtener algunos valores para el artículo: $idArticulo")
                        }
                    } else {
                        onResult("El artículo no existe en la base de datos: $idArticulo")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult("Error al obtener los datos para el artículo: $idArticulo - ${error.message}")
                }
            })
        }

        private fun actualizarTextViewConProductos(
            dataclassHistorial: dataclassHistorial,
            textView: TextView,
        ) {
            val productosTexto = StringBuilder()
            val productos = dataclassHistorial.productos ?: return

            var itemsProcesados = 0
            productos.forEach { (idProducto, cantidad) ->
                obtenerItems(
                    dataclassHistorial.idTienda.toString(),
                    idProducto,
                    cantidad.toString()
                ) { resultado ->
                    productosTexto.append(resultado)
                    itemsProcesados++
                    if (itemsProcesados == productos.size) {
                        textView.text = productosTexto.toString()
                    }
                }
            }
        }


    }
}