package com.example.tiendasgeinzadmin.DataClass

data class dataclasPedidos(
    val estado: String?,
    val estadoPedido: String?,
    val fecha: String?,
    val fechaEntregaLlegada: String?,
    val hora: String?,
    val horaEntregaLlegada: String?,
    val idDriver: String?,
    val idPedido: String?,
    val idRefUser: String?,
    val idTienda: String?,
    val idUser: String?,
    val metodoEntrega: String?,
    val metodoPago: String?,
    val precioDelivery: Number?,
    val productos: String?, // Si deseas manejarlo como JSON, usa un tipo de datos adecuado
    val tipoRealizado: String?,
    val totalCancelar: Number?,
    val totalDriver: Number?,
    val totalProductos: Number?
)

