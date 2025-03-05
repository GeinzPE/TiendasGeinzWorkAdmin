package com.example.tiendasgeinzadmin.Fragmentos

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.geinzTienda.tiendasgeinzadmin.R
import com.geinzTienda.tiendasgeinzadmin.databinding.FragmentCuentaFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class cuenta_fragment : Fragment() {
    private lateinit var binding: FragmentCuentaFragmentBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContex: Context

    override fun onAttach(context: Context) {
        mContex = context
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCuentaFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()
        getInfo_Store(firebaseAuth.uid.toString())
    }

    private fun getInfo_Store(idStore: String) {
        val db = FirebaseFirestore.getInstance().collection("Tiendas").document(idStore)
        db.get().addOnSuccessListener { res ->
            if (res.exists()) {
                val data = res.data
                val name_Strore = data?.get("nombre") as? String ?: ""
                val location_of_store = data?.get("ubicacion") as? String ?: ""
                val number_Store = data?.get("numero") as? Number ?: ""
                val id_Store = data?.get("id") as? String ?: ""
                val ruc_Strore = data?.get("ruc") as? Number ?: ""
                val type_Store = data?.get("tipoTienda") as? String ?: ""
                val description_of_store = data?.get("descripcion") as? String ?: ""
                val img_profile = data?.get("imgPerfil") as? String ?: ""
                val img_port = data?.get("imgPortada") as? String ?: ""


                binding.camposLlenados.nameStore.text = name_Strore
                binding.camposLlenados.locationStore.text = location_of_store
                binding.camposLlenados.numberContact.text = number_Store.toString()
                binding.camposLlenados.idStore.text = id_Store
                binding.camposLlenados.ruc.text = ruc_Strore.toString()
                binding.camposLlenados.typeStore.text = type_Store
                binding.camposLlenados.descriptionStore.text = description_of_store
                get_quantily_general(id_Store, "articulos") { art_products ->
                    binding.camposLlenados.productsPubliser.text = "($art_products) ver Productos"
                }
                get_quantily_general(id_Store, "promociones") { prom_products ->
                    binding.camposLlenados.promotionsEnable.text =
                        "($prom_products) ver promociones"
                }
                binding.camposLlenados.optionsDriver.text = "ver opciones"
                binding.camposLlenados.socialNetwork.text = "ver redes"
                binding.camposLlenados.methodOfPayments.text = "ver metodos"
                //  val quantily_of_review = data?.get("") as? String ?: ""
                set_img_store(img_profile,binding.cuentaImagenes.fondo,binding.cuentaImagenes.imgPerfil,"perfil")
                set_img_store(img_port,binding.cuentaImagenes.fondo,binding.cuentaImagenes.imgPerfil,"portada")



            } else {
                Log.e("error_obtenerDatos", "no se encontraron datos ")
            }
        }.addOnFailureListener { e ->
            Log.e("error_obtenerDatos", "no se encontraron datos $e  ")
        }
    }

    private fun get_quantily_general(
        id_Store: String,
        collection: String,
        callback: (Int) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
            .collection("Tiendas").document(id_Store)
            .collection(collection)

        db.get().addOnSuccessListener { res ->
            val cantidad = res.size()
            callback(cantidad)
        }.addOnFailureListener { e ->
            Log.e("Firebase", "Error al obtener la cantidad de documentos", e)
            callback(0)
        }
    }

    private fun set_img_store(
        url_StoreIMG: String,
        imaImageView: ImageView,
        circleImageView: CircleImageView,
        type: String
    ) {
        val target = when (type) {
            "portada" -> imaImageView
            "perfil" -> circleImageView
            else -> imaImageView // Valor por defecto si el tipo no es válido
        }

        Glide.with(mContex)
            .load(url_StoreIMG)
            .error(R.drawable.img_perfil) // Imagen de error en caso de fallo
            .into(target) // Carga la imagen en el target seleccionado
    }


}