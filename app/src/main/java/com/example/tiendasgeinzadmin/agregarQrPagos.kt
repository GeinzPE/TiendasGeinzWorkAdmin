package com.example.tiendasgeinzadmin

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.databinding.ActivityAgregarQrPagosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class agregarQrPagos : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarQrPagosBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var yape_img: Uri? = null
    private var plin_img: Uri? = null

    private val pciMEdia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                yape_img = uri
                binding.yape.setImageURI(uri)

            } else {
                println("Imagen no seleccionada")
            }
        }
    private val pciMEdia2 =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                plin_img = uri
                binding.plin.setImageURI(uri)

            } else {
                println("Imagen no seleccionada")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAgregarQrPagosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth=FirebaseAuth.getInstance()


        binding.yape.setOnClickListener {
            pciMEdia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.plin.setOnClickListener {
            pciMEdia2.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.agregar.setOnClickListener {
            val yapeRuta = "Tiendas/${firebaseAuth.uid.toString()}/QR_pagos/Yape.jpg"
            val plin = "Tiendas/${firebaseAuth.uid.toString()}/QR_pagos/Plin.jpg"
            yape_img?.let { agregarQR(it,yapeRuta,"Qr de yape agregada correctamente") }
            plin_img?.let { agregarQR(it,plin,"Qr de plin agregada correctamente") }
        }
    }

    private fun agregarQR(yape: Uri,ruta:String,texto:String) {
        val storage = FirebaseStorage.getInstance().getReference(ruta)
        storage.putFile(yape).addOnCompleteListener { res ->
            if (res.isSuccessful) {
                Toast.makeText(
                    this,
                    texto,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Error al subir el qr de yape",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


}