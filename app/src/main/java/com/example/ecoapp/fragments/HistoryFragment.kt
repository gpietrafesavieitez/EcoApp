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
import com.example.ecoapp.adapters.HistoryAdapter
import com.example.ecoapp.classes.Product
import com.example.ecoapp.networking.RetrofitBuilder.apiService
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_history.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onActivityCreated(savedInstanceState)
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "I'm working")
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        historyAdapter = HistoryAdapter(ArrayList(), requireActivity().applicationContext)
        recyclerView.adapter = historyAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        @Suppress("DEPRECATION")
        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorAccent))
        swipeRefreshLayout.setOnRefreshListener {
            getAllProductsByUser()
        }
    }

    override fun onResume() {
        super.onResume()
        getAllProductsByUser()
    }

    private fun getAllProductsByUser() {
        if (sharedPreferences.getBoolean("signedIn", false)) {
            swipeRefreshLayout.isRefreshing = true
            val id = sharedPreferences.getInt("id", 0)
            apiService.getProductsByUser(id).enqueue(object : Callback<ArrayList<Product>> {
                override fun onResponse(
                    call: Call<ArrayList<Product>>,
                    response: Response<ArrayList<Product>>
                ) {
                    try {
                        val listProduct = response.body()
                        if (listProduct != null) {
                            if (listProduct.isEmpty()) {
                                if (view != null) {
                                    Snackbar.make(
                                        clayout_history,
                                        R.string.no_history,
                                        Snackbar.LENGTH_INDEFINITE
                                    ).show()
                                }
                            } else {
                                historyAdapter.update(listProduct)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.isRefreshing = false
                    }
                }

                override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable?) {
                    //  context may be null
                    if (view != null) {
                        Snackbar.make(
                            clayout_history,
                            R.string.error_connection,
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                        swipeRefreshLayout.isRefreshing = false
                    }
                    t?.printStackTrace()
                }
            })
        } else {
            Log.d(TAG, "Unauthorized API call")
        }
    }

    companion object {
        const val TAG = "Debug:HistoryFragment"
    }

}
