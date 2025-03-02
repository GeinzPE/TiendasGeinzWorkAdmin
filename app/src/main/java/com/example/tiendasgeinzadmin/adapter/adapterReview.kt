package com.geinzTienda.tiendasgeinzadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.geinzTienda.tiendasgeinzadmin.DataClass.dataclassReview
import com.geinzTienda.tiendasgeinzadmin.R
import de.hdodenhof.circleimageview.CircleImageView

class adapterReview(private val lista: MutableList<dataclassReview>) :
    RecyclerView.Adapter<adapterReview.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val layourinflater= LayoutInflater.from(parent.context)
        return viewHolder(layourinflater.inflate(R.layout.item_review,parent,false))
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item =lista[position]
        holder.render(item)
    }

    inner class viewHolder (view: View) : RecyclerView.ViewHolder(view){
        val review = view.findViewById<TextView>(R.id.review)
        val nombrevw = view.findViewById<TextView>(R.id.nombre)
        val imgperfil = view.findViewById<CircleImageView>(R.id.img_PerfilUser)
        val imgpeStart = view.findViewById<ImageView>(R.id.cantidadStart)
        fun render(dataclassReview: dataclassReview) {

            nombrevw.text = dataclassReview.nombre
            review.text = dataclassReview.review
            try {
                Glide.with(itemView.context)
                    .load(dataclassReview.imgperfil)
                    .placeholder(R.drawable.img_perfil)
                    .into(imgperfil)
            } catch (e: java.lang.Exception) {
                println(e)
            }
            try {
                val drawableId = when (dataclassReview.cantidaStarts) {
                    "1" -> R.drawable.start_one
                    "2" -> R.drawable.start_two
                    "3" -> R.drawable.start_tree
                    "4" -> R.drawable.start_four
                    "5" -> R.drawable.start_five
                    else -> R.drawable.start_one // Puedes tener un recurso predeterminado para casos inválidos
                }

                Glide.with(itemView.context)
                    .load(drawableId)
                    .into(imgpeStart)
            } catch (e: Exception) {
                println(e)
            }
        }
    }

}