package com.geinzTienda.tiendasgeinzadmin.Fragmentos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.tiendasgeinzadmin.constantes.obtener_enviar_notification
import com.geinzTienda.tiendasgeinzadmin.Articulos
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.databinding.FragmentArticulosFrBinding
import com.geinzTienda.tiendasgeinzadmin.review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.FirebaseStorage

class articulos_fr : Fragment() {
    private lateinit var binding: FragmentArticulosFrBinding
    private lateinit var mcontex: Context
    private lateinit var firebaseAuth: FirebaseAuth
    private var imgProducto: Uri? = null
    private var reserva:Boolean?=false
    private var notificar: Boolean = false
    private val pciMEdia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imgProducto = uri
                binding.imgProducto.setImageURI(uri)

            } else {
                println("Imagen no seleccionada")
            }
        }

    override fun onAttach(context: Context) {
        mcontex = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentArticulosFrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        obtener_enviar_notification.contadorNotificaciones(binding.inclued.notificacionesContador)
        binding.inclued.NotificarSeguidores.setOnCheckedChangeListener { _, isChecked ->
            if (obtener_enviar_notification.isCamposLlenos(
                    binding.nombreArticuloEditext,
                    binding.descripcionArticuloEditext,
                    binding.inclued.tituloNotificacionED,
                    binding.inclued.descripcionNotificacionED
                )
            ) {
                notificar = isChecked
                binding.inclued.tituloNotificacion.isVisible = isChecked
                binding.inclued.descripcionNotificacion.isVisible = isChecked
                Log.d("Notificar", "Estado del Switch: $notificar")
            } else {
                binding.inclued.NotificarSeguidores.isChecked = false
                binding.inclued.tituloNotificacion.isVisible = false
                binding.inclued.descripcionNotificacion.isVisible = false
                Log.d("Notificar", "Campos vacíos. El Switch no se activará.")
            }
        }

        binding.crear.setOnClickListener {
            agregarArticulo(notificar)
        }
        binding.imgProducto.setOnClickListener {
            pciMEdia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.menuButton.setOnClickListener {
            popup()
        }
        setearTipoTienda(binding.tipoItem)

        obtenerCategoriasRealtime(firebaseAuth.uid.toString(), binding.categoria)

        val grupReserva = binding.grupoReserva
        grupReserva.setOnCheckedChangeListener { grupo, checkId ->
            reserva = when (checkId) {
                R.id.reserva_si -> {
                  true
                }

                R.id.reserva_no -> {
                  false
                }

                else -> false
            }
        }
    }

    private fun popup() {
        val popup = PopupMenu(mcontex, binding.menuButton)
        popup.menu.add(Menu.NONE, 1, 1, "Reseñas")
        popup.menu.add(Menu.NONE, 2, 2, "Ver Articulos")
        popup.show()
        popup.setOnMenuItemClickListener { item ->
            val itemID = item.itemId
            if (itemID == 1) {
                startActivity(Intent(mcontex, review::class.java))
            } else if ((itemID == 2)) {
                startActivity(Intent(mcontex, Articulos::class.java))
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun setearTipoTienda(autoCompleteTextView: AutoCompleteTextView) {
        val TipoTienda = TipoItem()
        val adapter = ArrayAdapter(
            autoCompleteTextView.context,
            android.R.layout.simple_dropdown_item_1line,
            TipoTienda
        )
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val tipoSeleccionado = TipoTienda[position]
            if (tipoSeleccionado == "Promociones Sugeridas") {
                binding.categoriaid.isVisible = false
                binding.nuevaCat.isVisible = false
            } else if (tipoSeleccionado == "Productos Principales") {
                binding.categoriaid.isVisible = true
            }
        }
    }

    private fun TipoItem(): List<String> {
        return listOf("Promociones Sugeridas", "Productos Principales")
    }

    private fun obtenerCategoriasRealtime(
        idTienda: String,
        autoCompleteTextView: AutoCompleteTextView,
    ) {
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("articulos")
        db.get().addOnSuccessListener { res ->
            val categorias = mutableSetOf<String>()
            for (data in res) {
                val datas = data.data
                val categoria = datas?.get("categoriaProducto") as? String

                categoria?.let { categorias.add(it) }
                categorias.add("Agregar nueva categoría")

                val adapter = ArrayAdapter(
                    autoCompleteTextView.context,
                    android.R.layout.simple_dropdown_item_1line,
                    categorias.toList()
                )
                autoCompleteTextView.setAdapter(adapter)

                autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                    val selectedCategory = parent.getItemAtPosition(position).toString()
                    if (selectedCategory == "Agregar nueva categoría") {

                        Toast.makeText(
                            autoCompleteTextView.context,
                            "Agregar nueva categoría",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.nuevaCat.isVisible = true
                    } else if (selectedCategory != "Agregar nueva categoría") {
                        binding.nuevaCat.isVisible = false
                    }
                }
            }
        }.addOnFailureListener {
            println("Error al crear o obtener las cateogiras")
        }

    }

    private fun agregarArticulo(notificacion:Boolean) {
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("articulos")

        // Generar una nueva referencia de documento con un ID único
        val newArticuloRef = db.document()
        val articuloId = newArticuloRef.id

        // Crear el hashMap con los datos del artículo
        val hashMap = if (binding.categoria.text.toString() == "Agregar nueva categoría") {
            hashMapOf(
                "id" to articuloId,
                "tipoItem" to binding.tipoItem.text.toString(),
                "categoriaProducto" to binding.nuevaCatED.text.toString(),
                "imgArticulo" to "",
                "nombreArticulo" to binding.nombreArticuloEditext.text.toString(),
                "precio" to binding.precioArticuloEditext.text.toString(),
                "fecha" to "", // Considera agregar la fecha actual aquí si es necesario
                "descripcion" to binding.descripcionArticuloEditext.text.toString(),
                "descripcion_corta" to binding.descripcionCortaED.text.toString(),
                "cantidad" to binding.stokArticulosED.text.toString(),
                "reserva" to (reserva?:false)
            )
        } else {
            hashMapOf(
                "id" to articuloId,
                "tipoItem" to binding.tipoItem.text.toString(),
                "categoriaProducto" to binding.categoria.text.toString(),
                "imgArticulo" to "",
                "nombreArticulo" to binding.nombreArticuloEditext.text.toString(),
                "precio" to binding.precioArticuloEditext.text.toString(),
                "fecha" to "", // Considera agregar la fecha actual aquí si es necesario
                "descripcion" to binding.descripcionArticuloEditext.text.toString(),
                "descripcion_corta" to binding.descripcionCortaED.text.toString(),
                "cantidad" to binding.stokArticulosED.text.toString(),
                "reserva" to (reserva?:false)
            )
        }

        // Agregar el artículo a la base de datos
        newArticuloRef.set(hashMap).addOnSuccessListener {
            Toast.makeText(mcontex, "Se agregó correctamente el artículo", Toast.LENGTH_SHORT)
                .show()

            // Subir la imagen si existe
            imgProducto?.let { imgUri ->
                val ruta = "Tiendas/${firebaseAuth.uid.toString()}/productos/$articuloId"
                SubirImgFirestore(imgUri, articuloId, ruta)
            } ?: run {
                // Si no hay imagen, limpiar los datos
                limpiarDatos()
            }

            if (notificacion) {
                obtener_enviar_notification.obtnerTokenSeguidores(
                    binding.inclued.notificacionesContador,
                    binding.hora,
                    binding.fecha,
                    binding.inclued.tituloNotificacionED,
                    binding.inclued.descripcionNotificacionED,
                    mcontex,
                    firebaseAuth.uid.toString(),
                    articuloId,
                    "PRODUCTOS_PRINCIPALES",
                    "idProductoClikado",
                    "idTienda",
                    "",
                    articuloId,
                    firebaseAuth.uid.toString(),
                    "",
                )

                Log.d("Notificacion", "se envio la notificacione a los seguidores")
            } else {
                Log.d("Notificacion", "no se envio a los seguidores")
            }
        }.addOnFailureListener {
            Toast.makeText(mcontex, "Hubo un error al agregar el artículo", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun SubirImgFirestore(uri: Uri, idArticulos: String, ruta: String) {
        val firebaseAuth = FirebaseAuth.getInstance()  // Asegúrate de inicializar firebaseAuth
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("articulos").document(idArticulos)

        val storage = FirebaseStorage.getInstance().getReference(ruta)
        storage.putFile(uri).addOnCompleteListener { res ->
            if (res.isSuccessful) {
                storage.downloadUrl.addOnSuccessListener { uri ->
                    val urlimg = uri.toString()
                    val hasmap = hashMapOf(
                        "imgArticulo" to urlimg
                    )
                    db.set(hasmap, SetOptions.merge()).addOnSuccessListener {
                        Toast.makeText(mcontex, "Imagen subida correctamente", Toast.LENGTH_SHORT)
                            .show()
                        limpiarDatos()
                    }.addOnFailureListener {
                        Toast.makeText(
                            mcontex,
                            "Error al actualizar la base de datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        mcontex,
                        "Error al obtener la URL de la imagen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(mcontex, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun limpiarDatos() {
        imgProducto = null
        binding.tipoItem.setText("")
        binding.categoria.setText("")
        binding.nombreArticuloEditext.setText("")
        binding.precioArticuloEditext.setText("")
        binding.descripcionArticuloEditext.setText("")
    }


}