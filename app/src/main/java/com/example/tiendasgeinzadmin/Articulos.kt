package com.geinzTienda.tiendasgeinzadmin

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geinzTienda.tiendasgeinzadmin.DataClass.dataclassArticulos
import com.geinzTienda.tiendasgeinzadmin.adapter.adapterArticulos
import com.geinzTienda.tiendasgeinzadmin.databinding.ActivityArticulosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Articulos : AppCompatActivity() {
    private lateinit var binding: ActivityArticulosBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticulosBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        obtenerArticulos()
    }

    private fun obtenerArticulos() {
        val listaArticulos = mutableListOf<dataclassArticulos>()
        val Realtime = FirebaseDatabase.getInstance().getReference("Articulos")
            .child(firebaseAuth.uid.toString())
        Realtime.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaArticulos.clear()
                for(articulos in snapshot.children){
                    val descripcion=articulos.child("descripcion").getValue(String::class.java).toString()
                    val imgArticulo =articulos.child("imgArticulo").getValue(String::class.java).toString()
                    val nombreArticulo=articulos.child("nombreArticulo").getValue(String::class.java).toString()
                    val precio=articulos.child("precio").getValue(String::class.java).toString()
                    val fecha=articulos.child("fecha").getValue(String::class.java).toString()
                    val articulos=dataclassArticulos(
                        nombreArticulo,imgArticulo,precio,descripcion,fecha
                    )
                    listaArticulos.add(articulos)
                }
                inicalizarRecicle(listaArticulos,binding.recicleArticulos,this@Articulos)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun inicalizarRecicle(
        listaArticulos: MutableList<dataclassArticulos>,
        recicleContainer: RecyclerView, context: Context
    ) {
        val recicle = recicleContainer
        recicle.layoutManager = LinearLayoutManager(context)
        recicle.adapter = adapterArticulos(listaArticulos,{dataclassArticulos -> vermas(dataclassArticulos)})
    }


   private fun vermas(dataclassArticulos: dataclassArticulos) {

    }

}
