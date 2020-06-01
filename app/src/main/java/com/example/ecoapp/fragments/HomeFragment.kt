package com.example.ecoapp.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecoapp.R
import com.example.ecoapp.adapters.ProductAdapter
import com.example.ecoapp.classes.Product
import com.example.ecoapp.classes.User
import com.example.ecoapp.networking.RetrofitBuilder.apiService
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onActivityCreated(savedInstanceState)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        productAdapter = ProductAdapter(ArrayList(), requireActivity().applicationContext)
        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        @Suppress("DEPRECATION")
        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorAccent))
        swipeRefreshLayout.setOnRefreshListener {
            getAllProducts()
        }
    }

    override fun onResume() {
        super.onResume()
        getAllProducts()
    }

    private fun getAllProducts() {
        if (sharedPreferences.getBoolean("signedIn", false)) {
            swipeRefreshLayout.isRefreshing = true
            apiService.getAllProducts().enqueue(object : Callback<ArrayList<Product>> {
                override fun onResponse(
                    call: Call<ArrayList<Product>>,
                    response: Response<ArrayList<Product>>
                ) {
                    try {
                        val listProduct = response.body()
                        if (listProduct != null) {
                            apiService.getAllUsers().enqueue(object : Callback<List<User>> {
                                override fun onResponse(
                                    call: Call<List<User>>,
                                    response: Response<List<User>>
                                ) {
                                    try {
                                        val listUsers = response.body()
                                        if (listUsers != null && listUsers.isNotEmpty()) {
                                            for (i in listProduct.indices) {
                                                for (j in listUsers.indices) {
                                                    if (listProduct[i].fbuser == listUsers[j].url) {
                                                        listProduct[i].username =
                                                            listUsers[j].username
                                                        break // pasa al siguiente producto xq ya ha encontrado el producto
                                                    }
                                                }
                                            }
                                            // el adaptador no es actualizado hasta q no acaba la consulta de usuarios
                                            if (listProduct.isEmpty()) {
                                                //  view may be null
                                                if (view != null) {
                                                    Snackbar.make(
                                                        clayout_home,
                                                        R.string.no_products,
                                                        Snackbar.LENGTH_INDEFINITE
                                                    ).show()
                                                }
                                            }
                                            productAdapter.update(listProduct)
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                    if (swipeRefreshLayout != null) {
                                        swipeRefreshLayout.isRefreshing = false
                                    }
                                }

                                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                                    t.printStackTrace()
                                    if (view != null) {
                                        Snackbar.make(
                                            clayout_home,
                                            R.string.no_products,
                                            Snackbar.LENGTH_INDEFINITE
                                        ).show()
                                    }
                                    if (swipeRefreshLayout != null) {
                                        swipeRefreshLayout.isRefreshing = false
                                    }
                                }
                            })
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
                    t.printStackTrace()
                    //  In asynchronous methods some components may be null when switching between fragments
                    if (view != null) {
                        Snackbar.make(
                            clayout_home,
                            R.string.error_connection,
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            })
        } else {
            Log.d(TAG, "Unauthorized API call")
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    companion object {
        const val TAG = "Debug:HomeFragment"
    }
}