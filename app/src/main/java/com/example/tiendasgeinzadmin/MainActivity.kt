package com.geinzTienda.tiendasgeinzadmin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.geinzTienda.tiendasgeinzadmin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val SECCION_INICIADA = "seccion_iniciada"
    override fun onCreate(savedInstanceState: Bundle?) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val KEY_Seccion = pref?.getBoolean(SECCION_INICIADA, false) ?: false
        if (KEY_Seccion) {
            startActivity(Intent(this, Principal::class.java))
            finish()
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        binding.BtnIngresar.setOnClickListener {
            login()
        }

    }

    private fun login() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val correo = binding.ingreseSuMail.text.toString()
        val contraseña = binding.txtpassword.text.toString()
        if (correo.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "rellene los campos", Toast.LENGTH_SHORT).show()
        } else {
            firebaseAuth.signInWithEmailAndPassword(correo, contraseña)
                .addOnSuccessListener { resultado ->
                    val user = resultado.user
                    if (user != null) {
                        verificarUsuarioEnTiendas(user.uid)
                    }
                    if (binding.seccionIniciadad.isChecked) {
                        guardarShader(SECCION_INICIADA, pref, true)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error al iniciar sesión: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun guardarShader(KEY: String, pref: SharedPreferences?, valor: Boolean) {
        val editor = pref?.edit()
        editor?.putBoolean(KEY, valor)
        editor?.apply()
    }

    private fun verificarUsuarioEnTiendas(uid: String) {
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
        db.document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    startActivity(Intent(this, Principal::class.java))
                    finish()
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                } else {

                    firebaseAuth.signOut()
                    Toast.makeText(
                        this,
                        "No tiene permiso para acceder a esta aplicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al verificar permisos: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}