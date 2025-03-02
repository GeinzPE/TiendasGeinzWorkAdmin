package com.geinzTienda.tiendasgeinzadmin.Fragmentos

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.tiendasgeinzadmin.constantes.obtener_enviar_notification
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.constantes.fecha_hora
import com.geinzTienda.tiendasgeinzadmin.constantes.metodos_pago_dialog
import com.geinzTienda.tiendasgeinzadmin.databinding.FragmentNoticiasFrBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class noticias_fr : Fragment() {

    private lateinit var binding: FragmentNoticiasFrBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mcontex: Context
    private var imgNoticia: Uri? = null
    private var precio: Boolean? = null
    private var compra: Boolean? = null
    private var reserva: Boolean? = null
    private var yapeCheked = false
    private var plinChaked = false
    private var efectivoCheked = false
    private var notificar: Boolean = false
    private var tipo: String = ""
    private val pciMEdia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imgNoticia = uri
                binding.imgNoticia.setImageURI(uri)

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
        binding = FragmentNoticiasFrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        fecha_hora.obtnerfechaHora(binding.hora, binding.fecha)
        binding.inclued.NotificarSeguidores.isChecked = notificar
        binding.inclued.tituloNotificacion.isVisible = notificar

        obtener_enviar_notification.contadorNotificaciones(binding.inclued.notificacionesContador)
        binding.inclued.NotificarSeguidores.setOnCheckedChangeListener { _, isChecked ->
            if (obtener_enviar_notification.isCamposLlenos(
                    binding.tituliED,
                    binding.contenidoED,
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
        binding.enviar.setOnClickListener {
            crearNoticia(notificar, firebaseAuth.uid.toString())
        }
        binding.imgNoticia.setOnClickListener {
            pciMEdia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.yape.setOnCheckedChangeListener { _, _ -> verificarRadioButons() }
        binding.plin.setOnCheckedChangeListener { _, _ -> verificarRadioButons() }
        binding.usarMetodoPago.setOnClickListener {
            metodos_pago_dialog.mostrarDialogoGuardarMetodoPago(
                mcontex,
                binding.yape,
                binding.plin,
                binding.efectivo,
                yapeCheked,
                plinChaked,
                efectivoCheked
            )

        }

        verificarRadioButons()
        val grupoPrecio = binding.RadioPrecio
        grupoPrecio.setOnCheckedChangeListener { grupo, checkId ->
            precio = when (checkId) {
                R.id.precio_si -> {
                    binding.linealPrecio.isVisible = true
                    binding.linealMetodosPago.isVisible = true
                    true
                }

                R.id.precio_no -> {
                    binding.linealPrecio.isVisible = false
                    binding.linealMetodosPago.isVisible = false
                    false
                }

                else -> {
                    binding.linealPrecio.isVisible = false
                    null
                }
            }
        }
        val grupoReservaCompra = binding.radioReservaCompra
        grupoReservaCompra.setOnCheckedChangeListener { grupo, checkId ->
            when (checkId) {
                R.id.reserva -> {
                    compra = false   // Si "reserva_si" está seleccionado, compra es true
                    reserva = true // y reserva es false
                }

                R.id.compra -> {
                    compra = true  // Si "reserva_no" está seleccionado, compra es false
                    reserva = false  // y reserva es true
                }

                else -> {
                    compra = null   // Si no hay selección válida, ambas variables son null
                    reserva = null
                }
            }
        }

        val grupReserva = binding.radioTipos
        grupReserva.setOnCheckedChangeListener { grupo, checkId ->
            tipo = when (checkId) {
                R.id.oferta -> {
                    binding.tiposNoticias.isVisible = true
                    "Oferta"
                }

                R.id.noticia -> {
                    binding.tiposNoticias.isVisible = false
                    binding.precioNo.isChecked = true
                    "Noticia"
                }

                R.id.anuncio -> {
                    binding.tiposNoticias.isVisible = false
                    binding.precioNo.isChecked = true
                    "Anuncio"
                }

                R.id.recomendaciones -> {
                    binding.tiposNoticias.isVisible = true
                    "recomendados"
                }

                else -> ""
            }
        }
    }

    private fun isCamposLlenos(): Boolean {
        val text1 = binding.tituliED.text.toString().trim()
        val text2 = binding.contenidoED.text.toString().trim()

        if (text1.isNotEmpty() && text2.isNotEmpty()) {
            binding.inclued.tituloNotificacionED.setText(text1)
            binding.inclued.descripcionNotificacionED.setText(text2)

        }
        return text1.isNotEmpty() && text2.isNotEmpty()
    }

    private fun crearNoticia(notificacion: Boolean, idTienda: String) {
        val tiendasRef = FirebaseFirestore.getInstance().collection("Tiendas").document(idTienda)
        tiendasRef.get()
            .addOnSuccessListener { res ->
                if (res.exists()) {
                    val data = res.data
                    val numero = data?.get("numero") as? String ?: ""
                    val plan = data?.get("plan") as? String ?: ""
                    val propietario = data?.get("nombre") as? String ?: ""
                    val tipoTienda = data?.get("tipoTienda") as? String ?: ""
                    val categoria = data?.get("categoria") as? String ?: ""
                    val ubicacion = data?.get("ubicacion") as? String ?: ""
                    val titulo = binding.tituliED.text.toString()
                    val fechaFin = binding.fechaFinalizacionED.text.toString()
                    val msjBienvenida = binding.mensajeWhatsappED.text.toString()
                    val contenido = binding.contenidoED.text.toString()
                    val lugaresDisponibles = binding.LugaresDisponiblesED.text.toString()
                    val tipoTiendaBoolena: Boolean? = when (tipoTienda) {
                        "fisica" -> true
                        "virtual" -> false
                        else -> null
                    }

                    val db = FirebaseFirestore.getInstance().collection("Tiendas")
                        .document(firebaseAuth.uid.toString()).collection("noticias")
                    val newArticuloRef = db.document()
                    val idNoticia = newArticuloRef.id
                    val hashMap = hashMapOf<String, Any>(
                        "categoria" to categoria,
                        "contenido" to contenido,
                        "estado" to "activo",
                        "fecha" to "",
                        "idTiendaProp" to firebaseAuth.uid.toString(),
                        "lugaresDisponibles" to lugaresDisponibles,
                        "numero" to numero,
                        "plan" to plan,
                        "propietario" to propietario,
                        "titulo" to titulo,
                        "tipoPromo" to tipo,
                        "ubicacion" to ubicacion,
                        "tipoTienda" to (tipoTiendaBoolena ?: false),
                        "vencimiento" to fechaFin,
                        "whatsappmsj" to msjBienvenida,
                        "price" to binding.precioED.text.toString(),
                        "precioBoolen" to (precio ?: false),
                        "reserva" to (reserva ?: false),
                        "compra" to (compra ?: false),
                        "yape" to yapeCheked,
                        "plin" to plinChaked,
                        "efectivo" to efectivoCheked,
                        "id" to idNoticia
                    )

                    newArticuloRef.set(hashMap)
                        .addOnSuccessListener {
                            if (notificacion) {
                                obtener_enviar_notification.obtnerTokenSeguidores(
                                    binding.inclued.notificacionesContador,
                                    binding.hora,
                                    binding.fecha,
                                    binding.inclued.tituloNotificacionED,
                                    binding.inclued.descripcionNotificacionED,
                                    mcontex,
                                    idTienda,
                                    idNoticia,
                                    "SUBIDA_NOTICIAS",
                                    "idAnuncio",
                                    "idTienda",
                                    "entrada",
                                    idNoticia,
                                    idTienda,
                                    "tiendas",
                                )
                                Log.d("Notificacion", "se envio la notificacione a los seguidores")
                            } else {
                                Log.d("Notificacion", "no se envio a los seguidores")
                            }
                            Toast.makeText(mcontex, "Se publicó tu noticia", Toast.LENGTH_SHORT)
                                .show()
                            Toast.makeText(
                                mcontex,
                                "Noticia subida exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            val ruta =
                                "Tiendas/${firebaseAuth.uid.toString()}/Noticias/$idNoticia.jpg"
                            imgNoticia?.let { SubirImgFirestore("imagenUrl", it, idNoticia, ruta) }
                        }
                        .addOnFailureListener {
                            Toast.makeText(mcontex, "Error al publicar", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun verificarRadioButons() {
        when {
            binding.yape.isChecked && binding.plin.isChecked && binding.efectivo.isChecked -> {
                yapeCheked = true
                plinChaked = true
                efectivoCheked = true
                binding.linealQr.isVisible = true
                binding.qrYape.isVisible = true
                binding.qrPlin.isVisible = true
            }

            binding.yape.isChecked && binding.plin.isChecked -> {
                yapeCheked = true
                plinChaked = true
                efectivoCheked = false
                binding.linealQr.isVisible = true
                binding.qrYape.isVisible = true
                binding.qrPlin.isVisible = true
            }

            binding.yape.isChecked && binding.efectivo.isChecked -> {
                yapeCheked = true
                efectivoCheked = true
                plinChaked = false
                binding.linealQr.isVisible = true
                binding.qrYape.isVisible = true
                binding.qrPlin.isVisible = false
            }

            binding.plin.isChecked && binding.efectivo.isChecked -> {
                plinChaked = true
                efectivoCheked = true
                yapeCheked = false
                binding.linealQr.isVisible = true
                binding.qrYape.isVisible = false
                binding.qrPlin.isVisible = true
            }

            binding.yape.isChecked -> {
                yapeCheked = true
                plinChaked = false
                efectivoCheked = false
                binding.linealQr.isVisible = true
                binding.qrYape.isVisible = true
                binding.qrPlin.isVisible = false
            }

            binding.plin.isChecked -> {
                plinChaked = true
                yapeCheked = false
                efectivoCheked = false
                binding.linealQr.isVisible = true
                binding.qrYape.isVisible = false
                binding.qrPlin.isVisible = true
            }

            binding.efectivo.isChecked -> {
                efectivoCheked = true
                yapeCheked = false
                plinChaked = false
                binding.linealQr.isVisible = false
                binding.qrYape.isVisible = false
                binding.qrPlin.isVisible = false
            }

            else -> {
                yapeCheked = false
                plinChaked = false
                efectivoCheked = false
                binding.linealQr.isVisible = false
                binding.qrYape.isVisible = false
                binding.qrPlin.isVisible = false
            }
        }
    }

    private fun SubirImgFirestore(hasmapDb: String, uri: Uri, id: String, ruta: String) {
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("noticias").document(id)
        val storage = FirebaseStorage.getInstance().getReference(ruta)
        storage.putFile(uri).addOnCompleteListener { res ->
            if (res.isSuccessful) {
                storage.downloadUrl.addOnSuccessListener { uri ->
                    val urlimg = uri.toString()
                    val hasmap = hashMapOf<String, Any>(
                        hasmapDb to urlimg
                    )
                    db.set(hasmap, SetOptions.merge()).addOnSuccessListener {
                        Toast.makeText(
                            mcontex,
                            "Imagen subida correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
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
                Toast.makeText(
                    mcontex,
                    "Error al subir la imagen",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}