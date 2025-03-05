package com.example.tiendasgeinzadmin.vistas_cuenta_tienda

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.databinding.ActivityAgregarProductosPromocionesServiciosBinding

class agregar_productos_promociones_servicios : AppCompatActivity() {
    private lateinit var binding:ActivityAgregarProductosPromocionesServiciosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAgregarProductosPromocionesServiciosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}