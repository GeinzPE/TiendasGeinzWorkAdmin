package com.geinzTienda.tiendasgeinzadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.geinzTienda.tiendasgeinzadmin.DataClass.dataclassitem_pedidos
import com.geinzTienda.tiendasgeinzadmin.databinding.ItemPedidosBinding

class adapterPedidos_item(private var listaItems: MutableList<dataclassitem_pedidos>) :
    RecyclerView.Adapter<adapterPedidos_item.viewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = ItemPedidosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listaItems.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = listaItems[position]
        holder.render(item)
    }

    class viewHolder(private val binding: ItemPedidosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var img_item = binding.imgItem
        var titulo_item = binding.tituloItem
        var precio_item = binding.precioItem
        val cantidad_item = binding.cantidad
//        val total = binding.total
        fun render(dataclassitem_pedidos: dataclassitem_pedidos) {
//            try {
//                Glide.with(itemView.context)
//                    .load(dataclassitem_pedidos.imgArticulo)
//                    .into(img_item)
//            }catch (e:Exception){
//                println("no se pudo setear la img")
//            }
//            titulo_item.text=dataclassitem_pedidos.tituloItem
//            precio_item.text=dataclassitem_pedidos.precio
//            cantidad_item.text=dataclassitem_pedidos.cantidad
//            total.text=dataclassitem_pedidos.total
        }
    }
}