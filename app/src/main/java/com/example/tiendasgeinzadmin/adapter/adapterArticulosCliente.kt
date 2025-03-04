package com.example.tiendasgeinzadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendasgeinzadmin.DataClass.dataClassArticulosObtenidos
import com.example.tiendasgeinzadmin.constantes.cosntantes_datos_user
import com.geinzTienda.tiendasgeinzadmin.R

class adapterArticulosCliente(
    private val listaArticulos: List<dataClassArticulosObtenidos>,
    private val tipo: String
) :
    RecyclerView.Adapter<adapterArticulosCliente.viewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val layout = LayoutInflater.from(parent.context)
        return viewHolder(layout.inflate(R.layout.item_articulos_cliente_obtenidos, parent, false))
    }

    override fun getItemCount(): Int {
        return listaArticulos.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = listaArticulos[position]
        holder.render(item)

    }

    inner class viewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val precioART = view.findViewById<TextView>(R.id.precioART)
        val nombreART = view.findViewById<TextView>(R.id.nombreART)
        val cantidadARt = view.findViewById<TextView>(R.id.cantidadART)
        fun render(item: dataClassArticulosObtenidos) {
            cosntantes_datos_user.getdataofItems(
                item.nombreART.toString(),
                tipo
            ) { name, cuantily, Quantity_Discount, price ->
                precioART.text = item.precioARt.toString()
                nombreART.text = name
                cantidadARt.text = item.cantidadARt.toString()
            }


        }


    }
}


