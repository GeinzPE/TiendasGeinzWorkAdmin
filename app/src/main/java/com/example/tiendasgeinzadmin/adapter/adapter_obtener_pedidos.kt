package com.example.tiendasgeinzadmin.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendasgeinzadmin.DataClass.dataClassArticulosObtenidos
import com.example.tiendasgeinzadmin.DataClass.dataclasPedidos
import com.example.tiendasgeinzadmin.constantes.cosntantes_datos_user
import com.geinzTienda.tiendasgeinzadmin.R
//import com.example.tiendasgeinzadmin.DiffUtilClass.DiffiutilCompras
import com.geinzTienda.tiendasgeinzadmin.databinding.ItemComprasRealizadasBinding
import org.json.JSONObject

class adapter_obtener_pedidos(
    private var lista: List<dataclasPedidos>,
    private val listener: (dataclasPedidos) -> Unit
) :
    RecyclerView.Adapter<adapter_obtener_pedidos.ViewHolderCompras>() {

//    fun updateList(newList: List<dataclasPedidos>) {
//        val compra = DiffiutilCompras(lista, newList)
//        val resukt = DiffUtil.calculateDiff(compra)
//        lista = newList
//        resukt.dispatchUpdatesTo(this)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCompras {
        val binding =
            ItemComprasRealizadasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderCompras(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolderCompras, position: Int) {
        val item = lista[position]
        holder.render(item)
    }

    inner class ViewHolderCompras(private val binding: ItemComprasRealizadasBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Definir el adaptador


        fun render(item: dataclasPedidos) {
            binding.listenerPedido.setOnClickListener { listener(item) }
            binding.numeroPedido.text = item.idPedido.toString()
            binding.totalPedido.text = item.totalProductos.toString()
            binding.metodoPago.text = item.metodoPago.toString()
            binding.estadoPedido.text = item.estadoPedido.toString()
            binding.tipoEntrega.text = item.metodoEntrega.toString()
            binding.TotalDriver.text = item.totalDriver.toString()
            binding.TotalProductos.text = item.totalProductos.toString()
            binding.tipoPedido.text = item.tipoRealizado.toString()
            binding.fechaPedido.text = item.fecha.toString()
            binding.horaPedido.text = item.hora.toString()

            cosntantes_datos_user.viewDataUSer(item.idUser.toString()) { name, number, last_name, location,imgPerfil ->
                binding.nombreCliente.text = "$name $last_name"
                binding.numeroUser.text = number
                binding.localidadUser.text = location
                try {
                    Glide.with(itemView.context)
                        .load(imgPerfil)
                        .error(R.drawable.img_perfil)
                        .into(binding.imgUser)
                }catch (e:Exception){
                    println("error al setar la img")
                }

            }

            if (item.idRefUser.isNullOrEmpty()) {
                binding.ubicacionCliente.text = "VACÍO"
            } else {
                cosntantes_datos_user.get_place_of_delivery(
                    item.idUser.toString(),
                    item.idRefUser.toString()
                ) { direction, _, _, _, _ ->
                    binding.ubicacionCliente.text = direction ?: "VACÍO"
                }
            }
            when(item.estado.toString()){
                "pendiente"->{
                    binding.estadoPedidoColor.setBackgroundResource(R.drawable.circle_status_order_violet)

                }
                "Aceptado"->{
                    binding.estadoPedidoColor.setBackgroundResource(R.drawable.circle_status_order_green)

                }
                "Rechazado"->{
                    binding.estadoPedidoColor.setBackgroundResource(R.drawable.circle_status_order_red)

                }
                "Completado"->{
                    binding.estadoPedidoColor.setBackgroundResource(R.drawable.circle_status_order_orange)

                }
            }

            // Obtener lista de artículos y actualizar el RecyclerView interno
            val listaArticulos = getItemUser(item.tipoRealizado.toString(),item.productos.toString())

        }

        private fun getItemUser(tipo:String,item: String): List<dataClassArticulosObtenidos> {
            val jsonObject = JSONObject(item)
            val listaArticulos = mutableListOf<dataClassArticulosObtenidos>()

            for (key in jsonObject.keys()) {
                val values = jsonObject.getJSONObject(key)
                val cantidad = values.optInt("cantidad", 0).toString()
                val precio = values.optInt("precio", 0).toString()

                val articulo = dataClassArticulosObtenidos(
                    nombreART = key,
                    precioARt = precio,
                    cantidadARt = cantidad
                )

                listaArticulos.add(articulo)
                initRecicleItems(tipo,listaArticulos)
                Log.d("FirebaseDatagetuser", "ID: $key, Cantidad: $cantidad, Precio: $precio")
            }

            return listaArticulos
        }

        private fun initRecicleItems(tipo:String,lista: List<dataClassArticulosObtenidos>) {
            val recyclerView = binding.cantidadArticulos

            // Configurar el RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapterArticulosCliente(lista, tipo)

        }
    }


}