package com.example.tiendasgeinzadmin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.databinding.ActivityHorariosTiendaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Horarios_tienda : AppCompatActivity() {
    private lateinit var binding: ActivityHorariosTiendaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var dia_semana: String = ""
    private var notificar: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHorariosTiendaBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        val grupo_semana = binding.grupoRb
        grupo_semana.setOnCheckedChangeListener { grupo, checkId ->
            dia_semana = when (checkId) {
                R.id.lunes -> "lunes"
                R.id.martes -> "martes"
                R.id.miercoles -> "miercoles"
                R.id.jueves -> "jueves"
                R.id.viernes -> "viernes"
                R.id.sabado -> "sabado"
                R.id.domingo -> "domingo"
                else -> ""// Manejo de caso por defecto
            }
        }

        binding.agregar.setOnClickListener {
            agregarHorario(firebaseAuth.uid.toString(), dia_semana)
        }

        binding.Cerrado.setOnCheckedChangeListener { _, isChecked ->
            notificar = isChecked
            Log.d("swtch","verificando el estado $notificar")
        }

    }

    private fun agregarHorario(idTienda: String, horario: String) {
        val db = FirebaseFirestore.getInstance().collection("Tiendas").document(idTienda)
            .collection("horario")
        val hashMap = hashMapOf<String, Any>()
        when (horario) {
            "lunes" -> {
                hashMap["h_apertura"] = binding.horaAM.text.toString()
                hashMap["h_cierre"] = binding.horaPM.text.toString()
                hashMap["cerrado"]=notificar
            }

            "martes" -> {
                hashMap["h_apertura"] = binding.horaAM.text.toString()
                hashMap["h_cierre"] = binding.horaPM.text.toString()
                hashMap["cerrado"]=notificar
            }

            "miercoles" -> {
                hashMap["h_apertura"] = binding.horaAM.text.toString()
                hashMap["h_cierre"] = binding.horaPM.text.toString()
                hashMap["cerrado"]=notificar
            }

            "jueves" -> {
                hashMap["h_apertura"] = binding.horaAM.text.toString()
                hashMap["h_cierre"] = binding.horaPM.text.toString()
                hashMap["cerrado"]=notificar
            }

            "viernes" -> {
                hashMap["h_apertura"] = binding.horaAM.text.toString()
                hashMap["h_cierre"] = binding.horaPM.text.toString()
                hashMap["cerrado"]=notificar
            }

            "sabado" -> {
                hashMap["h_apertura"] = binding.horaAM.text.toString()
                hashMap["h_cierre"] = binding.horaPM.text.toString()
                hashMap["cerrado"]=notificar
            }

            "domingo" -> {
                hashMap["h_apertura"] = binding.horaAM.text.toString()
                hashMap["h_cierre"] = binding.horaPM.text.toString()
                hashMap["cerrado"]=notificar
            }

            else -> {
                println("Día inválido")
            }
        }

        db.document(horario).set(hashMap, SetOptions.merge()).addOnSuccessListener {
            Toast.makeText(this, "horario agregado correctamente", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { e ->
                Toast.makeText(this, "error al agregar el horario $e", Toast.LENGTH_SHORT).show()
            }

    }
}