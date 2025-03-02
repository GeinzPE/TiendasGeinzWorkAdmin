package com.example.tiendasgeinzadmin.Fragmentos

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.tiendasgeinzadmin.constantes.obtener_enviar_notification
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.constantes.metodos_pago_dialog
import com.geinzTienda.tiendasgeinzadmin.databinding.FragmentServiciosFrBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class servicios_fr : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentServiciosFrBinding
    private lateinit var mcontex: Context
    private var reserva: Boolean? = null
    private var imgNoticia: Uri? = null
    private var yapeCheked = false
    private var plinChaked = false
    private var efectivoCheked = false
    private var notificar: Boolean = false
    private val pciMEdia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imgNoticia = uri
                binding.imgServicio.setImageURI(uri)

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
        binding = FragmentServiciosFrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()
        super.onViewCreated(view, savedInstanceState)
        binding.imgServicio.setOnClickListener {
            pciMEdia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.crear.setOnClickListener {
            crearServicio(notificar)
        }
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
        binding.yape.setOnCheckedChangeListener { _, _ -> verificarRadioButons() }
        binding.plin.setOnCheckedChangeListener { _, _ -> verificarRadioButons() }
        verificarRadioButons()
        val grupReserva = binding.grupReserva
        grupReserva.setOnCheckedChangeListener { grupo, checkId ->
            reserva = when (checkId) {
                R.id.reserva_si -> true
                R.id.reserva_no -> false
                else -> null
            }
        }

        obtener_enviar_notification.contadorNotificaciones(binding.inclued.notificacionesContador)
        binding.inclued.NotificarSeguidores.setOnCheckedChangeListener { _, isChecked ->
            if (obtener_enviar_notification.isCamposLlenos(
                    binding.nombreServicioED,
                    binding.descripcionServicioED,
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

    }


    private fun crearServicio(notificacion: Boolean) {
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("servicios")
        val idServicio = db.document()
        val idservicioDireto = idServicio.id
        val hashmap = hashMapOf<String, Any>(
            "descripcion" to binding.descripcionServicioED.text.toString(),
            "id" to idservicioDireto,
            "idTienda" to firebaseAuth.uid.toString(),
            "nombre" to binding.nombreServicioED.text.toString(),
            "precio" to binding.precioServicioED.text.toString(),
            "UrlImg" to "",
            "reserva" to reserva!!,
            "yape" to yapeCheked,
            "plin" to plinChaked,
            "efectivo" to efectivoCheked,
            "numero" to binding.numeroED.text.toString()
        )
        idServicio.set(hashmap).addOnSuccessListener {
            if (notificacion) {
                obtener_enviar_notification.obtnerTokenSeguidores(
                    binding.inclued.notificacionesContador,
                    binding.hora,
                    binding.fecha,
                    binding.inclued.tituloNotificacionED,
                    binding.inclued.descripcionNotificacionED,
                    mcontex,
                    firebaseAuth.uid.toString(),
                    idservicioDireto,
                    "SERVICIOS_TIENDAS",
                    "idServicio",
                    "idTienda",
                    "",
                    idservicioDireto,
                    firebaseAuth.uid.toString(),
                    "",
                )

                Log.d("Notificacion", "se envio la notificacione a los seguidores")
            } else {
                Log.d("Notificacion", "no se envio a los seguidores")
            }
            val ruta = "Tiendas/${firebaseAuth.uid.toString()}/Servicios/$idServicio.jpg"
            Toast.makeText(mcontex, "servicio agregado correctamente", Toast.LENGTH_SHORT).show()
            imgNoticia?.let { it1 -> SubirImgFirestore("UrlImg", it1, idservicioDireto, ruta) }
        }.addOnFailureListener {
            Toast.makeText(mcontex, "erro al subir el servicio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun SubirImgFirestore(hasmapDb: String, uri: Uri, id: String, ruta: String) {
        val db = FirebaseFirestore.getInstance().collection("Tiendas")
            .document(firebaseAuth.uid.toString()).collection("servicios").document(id)
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


}