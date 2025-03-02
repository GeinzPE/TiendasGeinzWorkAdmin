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
import com.bumptech.glide.Glide
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.databinding.ActivityEditarPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class editarPerfil : AppCompatActivity() {
    private lateinit var binding:ActivityEditarPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var imgportada: Uri? = null
    private var imgperfil: Uri? = null
    private val pciMEdia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imgportada = uri
                binding.portada.setImageURI(uri)

            } else {
                println("Imagen no seleccionada")
            }
        }
    private val pciMEdia2 = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imgperfil = uri
                binding.imgPerfilTienda.setImageURI(uri)

            } else {
                println("Imagen no seleccionada")
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditarPerfilBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        obtnerCampos()
        binding.Editar.setOnClickListener {
            actualizaCampos()
        }
        binding.imgPerfilTienda.setOnClickListener {
            pciMEdia2.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.portada.setOnClickListener {
            pciMEdia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun actualizaCampos() {
        val rutaPefil = "Tiendas/${firebaseAuth.uid.toString()}/perfil.pnj"
        val rutaProtada = "Tiendas/${firebaseAuth.uid.toString()}/portada.pnj"
        val userId = firebaseAuth.uid
        if (userId == null) {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance().collection("Tiendas").document(userId)

        val nombre = binding.EditarNombreEditext.text.toString().trim()
        val descripcion = binding.EditarDescripcionTiendaEditext.text.toString().trim()
        val horario = binding.EditarHorarioTiendaEditext.text.toString().trim()
        val numero = binding.EditarnumeroTiendaEditext.text.toString().trim()
        val zona = binding.EditarZonaTiendaEditext.text.toString().trim()
        val ubicacion = binding.EditarUbicacioniendaEditext.text.toString().trim()

        if (nombre.isEmpty() || descripcion.isEmpty() || horario.isEmpty() || numero.isEmpty() || zona.isEmpty()) {
            Toast.makeText(this, "Todos los campos deben ser llenados", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val hashMap = hashMapOf<String, Any>(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "horario" to horario,
            "numero" to numero,
            "zona" to zona,
            "ubicacion" to ubicacion
        )

        db.update(hashMap)
            .addOnSuccessListener {
                if (imgperfil != null) {
                    SubirImgFirestore(
                        "imgPerfil",
                        imgperfil!!,
                        firebaseAuth.uid.toString(),
                        rutaPefil
                    )
                } else {
                    println("se queda con la misma foto de perfil")
                }
                if (imgportada != null) {
                    SubirImgFirestore(
                        "imgPortada",
                        imgportada!!,
                        firebaseAuth.uid.toString(),
                        rutaProtada
                    )
                }
                Toast.makeText(this, "Campos actualizados correctamente", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar los campos", Toast.LENGTH_SHORT).show()
            }
    }


    private fun obtnerCampos() {
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString())

        db.get()
            .addOnSuccessListener { res ->
                if (res.exists()) {
                    val data = res.data
                    val nombre = data?.get("nombre") as? String
                    val descripcion = data?.get("descripcion") as? String
                    val horario = data?.get("horario") as? String
                    val numero = data?.get("numero") as? String
                    val zona = data?.get("zona") as? String
                    val imgPerfil = data?.get("imgPerfil") as? String
                    val imgPortada = data?.get("imgPortada") as? String
                    val ubicacion = data?.get("ubicacion") as? String
                    binding.EditarNombreEditext.setText(nombre)
                    binding.EditarDescripcionTiendaEditext.setText(descripcion)
                    binding.EditarHorarioTiendaEditext.setText(horario)
                    binding.EditarnumeroTiendaEditext.setText(numero)
                    binding.EditarZonaTiendaEditext.setText(zona)
                    binding.EditarUbicacioniendaEditext.setText(ubicacion)

                    try {
                        Glide.with(this)
                            .load(imgPerfil)
                            .into(binding.imgPerfilTienda)
                        Glide.with(this)
                            .load(imgPortada)
                            .into(binding.portada)
                    } catch (e: Exception) {
                        println("no se pudo setear la img")
                    }
                }
            }
    }

    private fun SubirImgFirestore(hasmapDb: String, uri: Uri, id: String, ruta: String) {
        val firestore = FirebaseFirestore.getInstance().collection("Tiendas").document(id)
        val storage = FirebaseStorage.getInstance().getReference(ruta)
        storage.putFile(uri).addOnCompleteListener { res ->
            if (res.isSuccessful) {
                storage.downloadUrl.addOnSuccessListener { uri ->
                    val urlimg = uri.toString()
                    val hasmap = hashMapOf<String, Any>(
                        hasmapDb to urlimg
                    )
                    firestore.set(hasmap, SetOptions.merge()).addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Imagen subida correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Error al actualizar la base de datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Error al obtener la URL de la imagen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Error al subir la imagen",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}