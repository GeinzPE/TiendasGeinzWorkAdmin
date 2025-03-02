package com.geinzTienda.tiendasgeinzadmin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.geinzTienda.tiendasgeinzadmin.DataClass.dataclassArticulos
import com.geinzTienda.tiendasgeinzadmin.R
import com.google.android.material.imageview.ShapeableImageView

class adapterArticulos(
    private val listaArticulos: MutableList<dataclassArticulos>,
    private val vermas: (dataclassArticulos) -> Unit
) :RecyclerView.Adapter<adapterArticulos.viewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val layout=LayoutInflater.from(parent.context)
        return viewHolder(layout.inflate(R.layout.item_articulos,parent,false))
    }

    override fun getItemCount(): Int {
     return listaArticulos.size
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
     val item=listaArticulos[position]
        holder.render(item,vermas)
    }

    inner class viewHolder (view: View) : RecyclerView.ViewHolder(view){
        val imgProducto=view.findViewById<ShapeableImageView>(R.id.imgProducto)
        val tituloProducto=view.findViewById<TextView>(R.id.tituloProducto)
        val descripcionProducto=view.findViewById<TextView>(R.id.descripcionProducto)
        val precioProducto=view.findViewById<TextView>(R.id.precioProducto)
        val fechaoProducto=view.findViewById<TextView>(R.id.fechaoProducto)
        val Editar=view.findViewById<LinearLayout>(R.id.Editar)
        fun render(dataclassArticulos: dataclassArticulos,vermas: (dataclassArticulos) -> Unit){
            try {
                Glide.with(itemView.context)
                    .load(dataclassArticulos.imgArt)
                    .into(imgProducto)
            }catch (e:Exception){
                println("no se pudo setear la img")
            }
            Editar.setOnClickListener {
                vermas(dataclassArticulos)
            }
            tituloProducto.text=dataclassArticulos.nombreART
            descripcionProducto.text=dataclassArticulos.descripcion
            precioProducto.text=dataclassArticulos.precio
            fechaoProducto.text=dataclassArticulos.fecha
        }
    }

}