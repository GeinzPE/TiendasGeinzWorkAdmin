package com.geinzTienda.tiendasgeinzadmin.Fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geinzTienda.tiendasgeinzadmin.DataClass.dataclassHistorial
import com.geinzTienda.tiendasgeinzadmin.Fragmentos.vistas.cambar_estado_designar_delivery
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.adapter.adapterCompras
import com.geinzTienda.tiendasgeinzadmin.databinding.FragmentPedidosFrBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONException
import org.json.JSONObject


class pedidos_fr : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentPedidosFrBinding
    private lateinit var mcontex: Context
    private val listaHistorial = mutableListOf<dataclassHistorial>()
    override fun onAttach(context: Context) {
        mcontex = context
        super.onAttach(context)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPedidosFrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        obtenerPedidos(firebaseAuth.uid.toString())
        val recyclerView = binding.pedidos
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
            ?: run {
                Log.e("LayoutManager Error", "LayoutManager is not LinearLayoutManager or is null")
                return
            }

        val handler = Handler(Looper.getMainLooper())
        val delay = 3000L

        val runnable = object : Runnable {
            override fun run() {
                val currentPosition = layoutManager.findFirstVisibleItemPosition()
                val currentOffset = layoutManager.findViewByPosition(currentPosition)?.top ?: 0

                obtenerPedidos(firebaseAuth.uid.toString())

                // Restaurar la posición
                recyclerView.post {
                    layoutManager.scrollToPositionWithOffset(currentPosition, currentOffset)
                }

                handler.postDelayed(this, delay)
            }
        }

        handler.postDelayed(runnable, delay)
    }

    private fun obtenerPedidos(idTienda: String) {
        val db = FirebaseDatabase.getInstance().getReference("CompraTienda").child(idTienda)
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listaHistorial.clear()
                if (dataSnapshot.exists()) {
                    for (usuarioSnapshot in dataSnapshot.children) {
                        val idUsuario = usuarioSnapshot.key
                        println("ID de usuario: $idUsuario")

                        val comprasRef = usuarioSnapshot.child("compra")
                        for (compraSnapshot in comprasRef.children) {
                            val idCompra = compraSnapshot.key ?: ""
                            val direccion =
                                compraSnapshot.child("direccion").getValue(String::class.java) ?: ""
                            val estado =
                                compraSnapshot.child("estado").getValue(String::class.java) ?: ""
                            val fecha =
                                compraSnapshot.child("fecha").getValue(String::class.java) ?: ""
                            val hora =
                                compraSnapshot.child("hora").getValue(String::class.java) ?: ""
                            val idPedido =
                                compraSnapshot.child("idPedido").getValue(String::class.java) ?: ""
                            val idTiendaCompra =
                                compraSnapshot.child("idTienda").getValue(String::class.java) ?: ""
                            val idUser =
                                compraSnapshot.child("idUSer").getValue(String::class.java) ?: ""
                            val metodoEntrega =
                                compraSnapshot.child("metodoEntrega").getValue(String::class.java)
                                    ?: ""
                            val metodoPago =
                                compraSnapshot.child("metodoPago").getValue(String::class.java)
                                    ?: ""
                            val nombre =
                                compraSnapshot.child("nombre").getValue(String::class.java) ?: ""
                            val numero =
                                compraSnapshot.child("numero").getValue(String::class.java) ?: ""
                            val localidadUsuario =
                                compraSnapshot.child("localidad").getValue(String::class.java) ?: ""
                            val localidadTienda =
                                compraSnapshot.child("localidadTienda").getValue(String::class.java)
                                    ?: ""
                            val productosJSON =
                                compraSnapshot.child("productos").getValue(String::class.java)
                                    ?: "{}"
                            val estadoPedido =
                                compraSnapshot.child("estadoPedido").getValue(String::class.java)
                                    ?: ""
                            val total =
                                compraSnapshot.child("total").getValue(String::class.java) ?: ""
                            val nombreTienda =
                                compraSnapshot.child("nombreTienda").getValue(String::class.java)
                                    ?: ""
                            val descipcion =
                                compraSnapshot.child("descipcion").getValue(String::class.java)
                                    ?: ""
                            val tipoTienda =
                                compraSnapshot.child("tipoTienda").getValue(String::class.java)
                                    ?: ""
                            val idDriver =
                                compraSnapshot.child("idDriver").getValue(String::class.java) ?: ""

                            val tipoRealizado =
                                compraSnapshot.child("tipoRealizado").getValue(String::class.java)
                                    ?: ""
                            val tipoReserva =
                                compraSnapshot.child("tipoReserva").getValue(String::class.java)
                                    ?: ""
                            val tituloProducto =
                                compraSnapshot.child("tituloProducto").getValue(String::class.java)
                                    ?: ""
                            val totalCancelar =
                                compraSnapshot.child("totalCancelar").getValue(String::class.java)
                                    ?: ""
                            val totalDriver =
                                compraSnapshot.child("totalDriver").getValue(String::class.java)
                                    ?: ""
                            val totalProductos =
                                compraSnapshot.child("totalProductos").getValue(String::class.java)
                                    ?: ""


                            val productosMap = mutableMapOf<String, Int>()
                            try {
                                val productosJSONObj = JSONObject(productosJSON)
                                val keysIterator: Iterator<String> = productosJSONObj.keys()
                                while (keysIterator.hasNext()) {
                                    val productId = keysIterator.next()
                                    val quantity = productosJSONObj.getInt(productId)
                                    productosMap[productId] = quantity
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            val datos = dataclassHistorial(
                                direccion,
                                estado,
                                fecha,
                                hora,
                                idPedido,
                                idTienda,
                                idUser,
                                metodoEntrega,
                                metodoPago,
                                nombre,
                                productosMap,
                                numero,
                                total,
                                nombreTienda,
                                localidadTienda,
                                localidadUsuario,
                                descipcion,
                                tipoTienda,
                                estadoPedido,
                                idDriver
                            )
                            listaHistorial.add(datos)
                        }
                        inicalizarLista(listaHistorial, binding.pedidos, mcontex)
                    }
                } else {
                    println("No se encontraron usuarios para la tienda con ID: $idTienda")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error al leer la base de datos: ${databaseError.message}")
            }
        })
    }

    private fun inicalizarLista(
        lista: MutableList<dataclassHistorial>,
        recile: RecyclerView,
        context: Context,
    ) {
        val recicle = recile
        recicle.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recicle.adapter =
            adapterCompras(
                lista,
                { dataclassHistorial -> cambiarEstado_DesignarDelivery(dataclassHistorial) })
    }

    private fun cambiarEstado_DesignarDelivery(dataclassHistorial: dataclassHistorial) {
        val productosJSON = dataclassHistorial.productos
        val productosList = obtenerListaProductos(productosJSON.toString())


        val listaProductos = ArrayList<Pair<String, Int>>(productosList)

        val intent = Intent(mcontex, cambar_estado_designar_delivery::class.java).apply {
            putExtra("listaProductos", listaProductos)
            putExtra("total", dataclassHistorial.total)
            putExtra("idUser", dataclassHistorial.iduser)
            putExtra("idPedido", dataclassHistorial.idPedido)
            putExtra("tipoPedido", "compra")
        }


        mcontex.startActivity(intent)
    }

    private fun obtenerListaProductos(productosJSON: String): List<Pair<String, Int>> {
        val productosMap = mutableListOf<Pair<String, Int>>()
        try {
            val productosJSONObj = JSONObject(productosJSON)
            val keysIterator: Iterator<String> = productosJSONObj.keys()
            while (keysIterator.hasNext()) {
                val productId = keysIterator.next()
                val quantity = productosJSONObj.getInt(productId)
                productosMap.add(productId to quantity)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return productosMap
    }

}