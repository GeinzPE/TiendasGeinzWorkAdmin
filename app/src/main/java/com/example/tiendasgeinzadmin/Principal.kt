package com.geinzTienda.tiendasgeinzadmin

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.tiendasgeinzadmin.Fragmentos.compras_fragment
import com.example.tiendasgeinzadmin.Fragmentos.cuenta_fragment
import com.example.tiendasgeinzadmin.Fragmentos.historial_ventas_fragment
import com.example.tiendasgeinzadmin.Fragmentos.promociones_fr
import com.example.tiendasgeinzadmin.Fragmentos.reservas_fragment
import com.example.tiendasgeinzadmin.Fragmentos.servicios_fr
import com.example.tiendasgeinzadmin.Fragmentos.sin_internet
import com.geinzTienda.tiendasgeinzadmin.Fragmentos.articulos_fr
import com.geinzTienda.tiendasgeinzadmin.Fragmentos.noticias_fr
import com.geinzTienda.tiendasgeinzadmin.Fragmentos.pedidos_fr
import com.geinzTienda.tiendasgeinzadmin.Fragmentos.perfil_fr
import com.geinzTienda.tiendasgeinzadmin.adapter.adapterViewPager
import com.geinzTienda.tiendasgeinzadmin.databinding.ActivityPrincipalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Principal : AppCompatActivity() {
    private lateinit var binding: ActivityPrincipalBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var currentFragmentTag: String? = null
    private var isUpdatingBottomNavigation = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        hideNavigationBar()
//        val viewPage = binding.viewPager
//        val tableLayour = binding.tabLayout
//        val db = FirebaseFirestore.getInstance().collection("Tiendas")
//            .document(firebaseAuth.uid.toString())
//        db.get().addOnSuccessListener { res ->
//            if (res.exists()) {
//                val data = res.data
//                val plan = data?.get("plan") as? String
//                val adapter = adapterViewPager(supportFragmentManager)
//                adapter.addFragmet(perfil_fr(), "Perfil")
//                adapter.addFragmet(pedidos_fr(), "pedidos")
//                if (plan == "intermedio" || plan == "premiun") {
//                    adapter.addFragmet(noticias_fr(), "Noticias")
//                    adapter.addFragmet(servicios_fr(), "servicios")
//                    adapter.addFragmet(promociones_fr(), "Promociones")
//                    adapter.addFragmet(articulos_fr(), "Articulos")
//                }
//
//                viewPage.adapter = adapter
//                tableLayour.setupWithViewPager(viewPage)
//            }
//        }

        changeSystemBarsColor(Color.parseColor("#744ACB"))
        if (!isInternetAvailable(this)) {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            sinInternetfun()
        } else {
            loadMainContent()
        }

    }
    var nombreUsuario = ""
    var id = ""

    val bundle =
        Bundle().apply {
        putString("clave", "$nombreUsuario")
        putString("idUSer", "$id")
    }
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }



    private fun hideNavigationBar() {
        window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    )
        }
    }

    private fun changeSystemBarsColor(color: Int) {
        window.navigationBarColor = color
        window.statusBarColor = color
    }

    fun onInternetRestored() {
        val fragment = supportFragmentManager.findFragmentByTag("sin_internet")
        fragment?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
        loadMainContent()
    }

    private fun loadMainContent() {
        compras()

        binding.buttonNavigation.setOnItemSelectedListener { item ->
            if (isUpdatingBottomNavigation) return@setOnItemSelectedListener true // Evita cambios mientras se actualiza
            when (item.itemId) {
                R.id.compras -> {
                    if (currentFragmentTag != "FragmentInicio") {
                        compras()
                    }
                    true
                }

                R.id.reservas -> {
                    if (currentFragmentTag != "FragmentContacto") {
                        reservas()
                    }
                    true
                }

                R.id.historial_venta -> {
                    if (currentFragmentTag != "FragmentCategorias") {
                        historial_compras()
                    }
                    true
                }

                R.id.cuenta -> {
                    if (currentFragmentTag != "FragmentCuenta") {
                        cuenta()
                    }
                    true
                }



                else -> false
            }
        }
    }


    private fun sinInternetfun() {
        val fragmentBinding = sin_internet()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fracmentoLayaout.id, fragmentBinding, "sin_internet")
        fragmentTransaction.commit()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        currentFragmentTag = tag
        supportFragmentManager.beginTransaction()
            .replace(R.id.fracmentoLayaout, fragment, tag)
            .commit()
    }
    private fun updateBottomNavigation(selectedItemId: Int) {
        isUpdatingBottomNavigation = true
        binding.buttonNavigation.selectedItemId = selectedItemId
        isUpdatingBottomNavigation = false
    }

    private fun compras() {
        val fragmentBinding = compras_fragment()
        fragmentBinding.arguments = bundle
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fracmentoLayaout.id, fragmentBinding, "FragmentCompras")
        replaceFragment(fragmentBinding, "FragmentCompras")
        fragmentTransaction.commit()
        updateBottomNavigation(R.id.compras)
    }

    private fun reservas() {
        val fragmentBinding = reservas_fragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fracmentoLayaout.id, fragmentBinding, "FragmentReservas")
        replaceFragment(fragmentBinding, "FragmentReservas")
        fragmentTransaction.commit()
        updateBottomNavigation(R.id.reservas)
    }

    private fun historial_compras() {
        val fragmentBinding = historial_ventas_fragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fracmentoLayaout.id, fragmentBinding, "FragmentHistorialCompras")
        replaceFragment(fragmentBinding, "FragmentHistorialCompras")
        fragmentTransaction.commit()
        updateBottomNavigation(R.id.historial_venta)
    }

    private fun cuenta() {
        val fragmentBinding = cuenta_fragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fracmentoLayaout.id, fragmentBinding, "FragmentCuenta")
        replaceFragment(fragmentBinding, "FragmentCuenta")
        fragmentTransaction.commit()
        updateBottomNavigation(R.id.cuenta)
    }



}