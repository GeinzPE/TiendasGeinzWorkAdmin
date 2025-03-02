package com.example.tiendasgeinzadmin.DiffUtilClass

import androidx.recyclerview.widget.DiffUtil
import com.example.tiendasgeinzadmin.DataClass.dataclasPedidos

class DiffiutilCompras(
    private val oldList: List<dataclasPedidos>,
    private val newList: List<dataclasPedidos>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].estados == newList[newItemPosition].estados
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}