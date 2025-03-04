package com.example.tiendasgeinzadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendasgeinzadmin.DataClass.dataclassProductosCompras
import com.geinzTienda.tiendasgeinzadmin.databinding.ItemPedidosBinding

class adapterProductosComprados(private val lista: MutableList<dataclassProductosCompras>) :
    RecyclerView.Adapter<adapterProductosComprados.ViewHolderComprasRealizadas>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderComprasRealizadas {
        val binding = ItemPedidosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderComprasRealizadas(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolderComprasRealizadas, position: Int) {
        val item = lista[position]
        holder.render(item)
    }

    inner class ViewHolderComprasRealizadas(private val binding: ItemPedidosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun render(item: dataclassProductosCompras) {
            try {
                Glide.with(itemView.context)
                    .load(item.imgProducto)
                    .into(binding.imgItem)
            } catch (e: Exception) {
                println(e)
            }

            binding.cantidad.text = item.cantidad.toString()
            binding.precioItem.text = "S/.${item.precio}.00"
            binding.tituloItem.text = item.tituloProducto

            var totalPagar =  item.precio
            val descuento = item.cantidadDescuento?.toInt() ?: 0
            if (item.descuentoBoolean == true && descuento > 0) {
                totalPagar = calcularPrecioConDescuento(totalPagar, descuento)
                binding.descuentoProducto.text = "-$descuento%"
            } else {
                binding.descuentoProducto.text = "NO"
            }
            binding.totalcancelar.text = "S/.${totalPagar}.00"


        }

    }

    private fun calcularPrecioConDescuento(precio: Int, descuento: Int): Int {
        return precio - (precio * (descuento / 100))
    }
}