package com.example.tiendasgeinzadmin.constantes

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


object cosntantes_datos_user {

    fun viewDataUSer(
        idUSer: String,
        callback: (String?, String?, String?, String?, String?) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()

        returnUser(idUSer) { isTrabajador ->
            val collectionPath = if (isTrabajador) "trabajadores" else "usuarios"
            val usuariosRef = firestore.collection("Trabajadores_Usuarios_Drivers")
                .document(collectionPath).collection(collectionPath)

            usuariosRef.whereEqualTo("id", idUSer).get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val datos = result.documents.first()
                        Log.d(
                            "FirestoreData",
                            "Datos obtenidos: ${datos.data}"
                        ) // Ver qué devuelve Firestore

                        val nombre = datos.getString("nombre") ?: ""
                        val numeroUSer = datos.getString("numero") ?: ""
                        val apellido = datos.getString("apellido") ?: ""
                        val localidad = datos.getString("localidad") ?: ""
                        val imgPerfil = datos.getString("imagenPerfil") ?: ""

                        Log.d("FirestoreData", "ImagenPerfil obtenida: $imgPerfil")

                        callback(nombre, numeroUSer, apellido, localidad, imgPerfil)
                    } else {
                        Log.e("FirestoreData", "No se encontró usuario con ID: $idUSer")
                        callback(null, null, null, null, null)
                    }
                }

                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Error al obtener datos de $collectionPath", exception)
                    callback(null, null, null, null, null)
                }
        }
    }

    fun get_place_of_delivery(
        idUSer: String,
        idUbi: String,
        callback: (String?, String?, String?, String?, String?) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        returnUser(idUSer) { isTrabajador ->
            val collectionPath = if (isTrabajador) "trabajadores" else "usuarios"
            val usuariosRef = firestore.collection("Trabajadores_Usuarios_Drivers")
                .document(collectionPath).collection(collectionPath).document(idUSer)
                .collection("ubicacion").document(idUbi)
            usuariosRef.get().addOnSuccessListener { result ->
                if (result.exists()) {
                    val datos = result.data
                    val direccion = datos?.get("direccion") as? String ?: ""
                    val nombreref = datos?.get("nombre") as? String ?: ""
                    val lat = datos?.get("lat") as? String ?: ""
                    val log = datos?.get("log") as? String ?: ""
                    val localidad = datos?.get("localidad") as? String ?: ""
                    callback(direccion, nombreref, localidad, lat, log)
                } else {
                    callback(null, null, null, null, null) // No se encontró en la colección
                }
            }.addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error al obtener datos de $collectionPath", exception)
                callback(null, null, null, null, null)
            }
        }
    }

    fun returnUser(idUSer: String, retunsUser: (Boolean) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()

        val trabajadorRef = firestore.collection("Trabajadores_Usuarios_Drivers")
            .document("trabajadores").collection("trabajadores").document(idUSer)

        val usuarioRef = firestore.collection("Trabajadores_Usuarios_Drivers")
            .document("usuarios").collection("usuarios").document(idUSer)

        trabajadorRef.get().addOnSuccessListener { trabajadorDoc ->
            if (trabajadorDoc.exists()) {
                retunsUser(true) // Es trabajador
            } else {
                usuarioRef.get().addOnSuccessListener { usuarioDoc ->
                    if (usuarioDoc.exists()) {
                        retunsUser(false) // Es usuario normal
                    } else {
                        retunsUser(false) // No se encontró en ninguna colección
                    }
                }.addOnFailureListener {
                    retunsUser(false) // Error al consultar usuarios
                }
            }
        }.addOnFailureListener {
            retunsUser(false) // Error al consultar trabajadores
        }
    }

    fun getdataofItems(
        idArt: String,
        tipo: String,
        caracteristica: (String?, String?, String?, String?) -> Unit
    ) {
        // Determinar la colección correcta según el tipo
        val collectionPath = when (tipo) {
            "compra_carrito" -> "articulos"
            "compra_promociones" -> "promociones"
            else -> null
        }

        if (collectionPath == null) {
            caracteristica("", "", "", "")
            return
        }

        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection(collectionPath)
            .document(idArt)

        db.get().addOnSuccessListener { res ->
            if (res.exists()) {
                val data = res.data
                val nameART = data?.get("nombreArticulo") as? String ?: ""
                val quantily = data?.get("cantidad") as? String ?: ""
                val Quantity_Discount = data?.get("cantidadDescuento") as? Number ?: 0
                val price = data?.get("precio") as? String ?: ""

                caracteristica(nameART, quantily, Quantity_Discount.toString(), price)
            } else {
                caracteristica("", "", "", "")
            }
        }.addOnFailureListener {
            caracteristica("", "", "", "")
        }
    }


}