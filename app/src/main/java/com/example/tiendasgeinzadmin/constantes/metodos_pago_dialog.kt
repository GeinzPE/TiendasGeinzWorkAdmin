package com.geinzTienda.tiendasgeinzadmin.constantes

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.geinzTienda.tiendasgeinzadmin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object metodos_pago_dialog {
    private lateinit var firebaseAuth: FirebaseAuth
     fun mostrarDialogoGuardarMetodoPago(
        context: Context,
        yapeche: CheckBox,
        plinche: CheckBox,
        efectivoche: CheckBox,
        yapeCheked: Boolean,
        plinChaked: Boolean,
        efectivoCheked: Boolean,
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Guardar Método de Pago")
        builder.setMessage("¿Deseas Crear o usar un nuevo método de pago?")
        builder.setPositiveButton("Crear") { dialog, _ ->
            mostrarDialogoAgregarMetodoPago(context, yapeCheked, plinChaked, efectivoCheked)
        }
        builder.setNegativeButton("Usar") { dialog, _ ->
            mostrarDialogoSeleccionMetodoPago(context, yapeche, plinche, efectivoche)
        }
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.roun_dialog)
        dialog.show()
    }

    private fun mostrarDialogoAgregarMetodoPago(
        context: Context,
        yapeCheked: Boolean,
        plinChaked: Boolean,
        efectivoCheked: Boolean,
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.agregar_metodo_pago, null)
        val agregarMetodo = dialogView.findViewById<EditText>(R.id.input_nombre_metodo)

        // Crear un nuevo AlertDialog.Builder para el diálogo de agregar método de pago
        val agregarMetodoBuilder = AlertDialog.Builder(context)
        agregarMetodoBuilder.setTitle("Agregar Método de Pago")
        agregarMetodoBuilder.setView(dialogView)
        agregarMetodoBuilder.setPositiveButton("Guardar") { dialog, _ ->
            val nombreMetodo = agregarMetodo.text.toString().trim()
            if (nombreMetodo.isNotEmpty()) {
                guardarMetodoPagoEnFirestore(
                    nombreMetodo,
                    context,
                    yapeCheked,
                    plinChaked,
                    efectivoCheked
                )
            }
            dialog.dismiss()
        }
        agregarMetodoBuilder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = agregarMetodoBuilder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.roun_dialog)
        dialog.show()
    }

    private fun mostrarDialogoSeleccionMetodoPago(
        context: Context,
        yapeche: CheckBox,
        plinche: CheckBox,
        efectivoche: CheckBox,
    ) {
        firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("Metodos_pago")

        db.get().addOnSuccessListener { result ->
            val metodos = result.documents.map { it.id }
            if (metodos.isEmpty()) {
                Toast.makeText(context, "No hay métodos de pago disponibles", Toast.LENGTH_SHORT)
                    .show()
                return@addOnSuccessListener
            }

            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_prezonalisado_pago, null)
            val listaMetodosPago = dialogView.findViewById<ListView>(R.id.lista_metodos_pago)

            val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, metodos)
            listaMetodosPago.adapter = adapter

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Selecciona un Método de Pago")
            builder.setView(dialogView)


            val dialog = builder.create()

            listaMetodosPago.setOnItemClickListener { _, _, position, _ ->
                val metodoSeleccionado = metodos[position]
                println("Obtenemos el método de pago: $metodoSeleccionado")
                cargarDatosMetodoPago(metodoSeleccionado, yapeche, plinche, efectivoche, context)
                dialog.dismiss()
            }

            builder.setPositiveButton("Cancelar") { _, _ ->
                dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawableResource(R.drawable.roun_dialog)
            dialog.show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Error al obtener métodos de pago: $e", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun guardarMetodoPagoEnFirestore(
        nombreMetodo: String,
        context: Context,
        yapeCheked: Boolean,
        plinChaked: Boolean,
        efectivoCheked: Boolean,
    ) {
        firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("Metodos_pago").document(nombreMetodo)
        val hashMap = hashMapOf<String, Any>(
            "yape" to yapeCheked,
            "plin" to plinChaked,
            "efectivo" to efectivoCheked
        )
        db.set(hashMap, SetOptions.merge()).addOnSuccessListener {
            Toast.makeText(
                context,
                "Metodo de pago guardado en la coleccion $nombreMetodo",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Error al guardar el metodo de pago", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDatosMetodoPago(
        nombreMetodo: String,
        yapeche: CheckBox,
        plinche: CheckBox,
        efectivoche: CheckBox,
        context: Context,
    ) {
        firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("Metodos_pago")
            .document(nombreMetodo)

        db.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val datos = document.data
                val yape = datos?.get("yape") as? Boolean ?: false
                val plin = datos?.get("plin") as? Boolean ?: false
                val efectivo = datos?.get("efectivo") as? Boolean ?: false


                yapeche.isChecked = yape

                plinche.isChecked = plin

                efectivoche.isChecked = efectivo

            } else {
                Toast.makeText(context, "Método de pago no encontrado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                context,
                "Error al cargar datos del método de pago: $e",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}