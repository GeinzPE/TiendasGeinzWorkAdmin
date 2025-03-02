package com.example.tiendasgeinzadmin.DataClass

data class dataclassProductosCompras(
    val imgProducto: String?,
    val tituloProducto: String?,
    val productoid: String?,
    val cantidad: Int = 0,
    val precio: Int = 0,
    val descuentoBoolean: Boolean?,
    val cantidadDescuento: Number? = 0
)
