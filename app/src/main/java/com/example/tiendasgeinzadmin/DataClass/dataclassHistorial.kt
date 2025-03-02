package com.geinzTienda.tiendasgeinzadmin.DataClass

data class dataclassHistorial(

    var direccion: String?,
    var estadp: String?,
    var fecha: String?,
    var hora: String?,
    var idPedido: String?,
    var idTienda: String?,
    var iduser: String?,
    var metodoentrega: String?,
    var metodoPago: String?,
    var nombre: String?,
    var productos: Map<String, Int>?,
    var numero: String?,
    var total: String?,
    var nombreTienda:String?,
    val localidadTienda:String?,
    val localidadUSer:String?,
    val descipcion:String?,
    val tipoTienda:String?,
    val estadoPedido:String?,
    val idDriver:String?

)
