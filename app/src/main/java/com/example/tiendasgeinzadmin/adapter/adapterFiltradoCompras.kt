package com.example.tiendasgeinzadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendasgeinzadmin.DataClass.dataClassFiltradoCompras
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.databinding.ItemFiltradoBinding

class adapterFiltradoCompras(
    private val lista: List<dataClassFiltradoCompras>,
    private val listener: (dataClassFiltradoCompras) -> Unit
) : RecyclerView.Adapter<adapterFiltradoCompras.viewHolderFiltrado>() {

    private var selectedPosition: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolderFiltrado {
        val binding =
            ItemFiltradoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolderFiltrado(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: viewHolderFiltrado, position: Int) {
        val item = lista[position]
        holder.render(item, position)
    }

    inner class viewHolderFiltrado(private val binding: ItemFiltradoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun render(item: dataClassFiltradoCompras, position: Int) {
            binding.nombreCategoria.text = item.nombreCategoria

            if (position == selectedPosition) {
                binding.nombreCategoria.setBackgroundResource(R.drawable.item_roun_carpetas_oscuro) // Fondo seleccionado
            } else {
                binding.nombreCategoria.setBackgroundResource(R.drawable.roun_item_carpetas) // Fondo normal
            }

            binding.nombreCategoria.setOnClickListener {

                if (selectedPosition != position) {

                    val previousPosition = selectedPosition

                    selectedPosition = position

                    if (previousPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(previousPosition)
                    }
                    notifyItemChanged(selectedPosition)
                }


                listener(item)
            }

        }

    }

}