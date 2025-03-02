package com.example.tiendasgeinzadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendasgeinzadmin.DataClass.dataclasPedidos
import com.example.tiendasgeinzadmin.DiffUtilClass.DiffiutilCompras
import com.geinzTienda.tiendasgeinzadmin.databinding.ItemComprasRealizadasBinding

class adapter_obtener_pedidos(
    private var lista: List<dataclasPedidos>,
    private val listener: (dataclasPedidos) -> Unit
) :
    RecyclerView.Adapter<adapter_obtener_pedidos.ViewHolderCompras>() {

    fun updateList(newList: List<dataclasPedidos>) {
        val compra = DiffiutilCompras(lista, newList)
        val resukt = DiffUtil.calculateDiff(compra)
        lista = newList
        resukt.dispatchUpdatesTo(this)
    }

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
        fun render(item: dataclasPedidos) {
            binding.listenerPedido.setOnClickListener { listener(item) }
            binding.numeroPedido.text = item.numeroPedidos.toString()
            binding.nombreCliente.text = item.nombreUSer.toString()
            binding.totalPedido.text = item.total.toString()
            binding.cantidadArticulos.text = item.cantidadArticulos.toString()
            binding.metodoPago.text = item.metodoPago.toString()
            binding.estadoPedido.text = item.estados.toString()
            binding.numeroUser.text = item.numeroUser.toString()
            binding.tipoEntrega.text = item.tipoEntrega.toString()
            binding.ubicacionCliente.text =
                if (item.ubicacionUser.isNullOrEmpty()) "VACÍO" else item.ubicacionUser.toString()
            binding.localidadUser.text = item.localidadUSer.toString()
            binding.TotalDriver.text = item.tototalDriver.toString()
            binding.TotalProductos.text = item.totalProductos.toString()
            binding.tipoPedido.text = item.tipoPedido.toString()
            binding.fechaPedido.text = item.fecha.toString()
            binding.horaPedido.text = item.hora.toString()

        }

    }
}