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
import com.geinzTienda.tiendasgeinzadmin.DataClass.dataclassReview
import com.geinzTienda.tiendasgeinzadmin.adapter.adapterArticulos
import com.geinzTienda.tiendasgeinzadmin.adapter.adapterReview
import com.geinzTienda.tiendasgeinzadmin.databinding.ActivityReviewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class review : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        obtenerReview()
    }

    private fun obtenerReview() {
        val listaReview = mutableListOf<dataclassReview>()
        val Realtime = FirebaseDatabase.getInstance().getReference("reseñasTiendas")
            .child(firebaseAuth.uid.toString())
        Realtime.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaReview.clear()
                for (datos in snapshot.children) {
                    val id = datos.child("iduserReview").getValue(String::class.java)
                    val cantidad = datos.child("cantidad").getValue(String::class.java)
                    val imgPerfil = datos.child("imgPerfil").getValue(String::class.java)
                    val nombre = datos.child("nombre").getValue(String::class.java)
                    val review = datos.child("reseña").getValue(String::class.java)
                    val reviewDataClass = dataclassReview(imgPerfil, id, nombre, cantidad, review)
                    listaReview.add(reviewDataClass)
                }
                inicalizarRecicle(listaReview,binding.recicleReview,this@review)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun inicalizarRecicle(
        listaReview: MutableList<dataclassReview>,
        recicleContainer: RecyclerView, context: Context
    ) {
        val recicle = recicleContainer
        recicle.layoutManager = LinearLayoutManager(context)
        recicle.adapter = adapterReview(listaReview)
    }
}