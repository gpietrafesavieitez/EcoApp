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
import com.example.ecoapp.adapters.ComponentAdapter
import com.example.ecoapp.classes.Component
import com.example.ecoapp.networking.RetrofitBuilder.apiService
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewFragment : Fragment() {

    private var componentList = ArrayList<Component>()
    private lateinit var componentAdapter: ComponentAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "I'm working")
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        componentAdapter = ComponentAdapter(componentList, requireActivity())
        recyclerView2.adapter = componentAdapter
        recyclerView2.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        getAllComponents()
    }

    private fun getAllComponents() {
        if (sharedPreferences.getBoolean("signedIn", false)) {
            progressBar.visibility = View.VISIBLE
            apiService.getAllComponents().enqueue(object : Callback<ArrayList<Component>> {
                override fun onResponse(call: Call<ArrayList<Component>>, response: Response<ArrayList<Component>>) {
                    val availableComponents = response.body()
                    if (availableComponents != null) {
                        Log.d("availableComponents", availableComponents.toString())
                        if (view != null) {
                            progressBar.visibility = View.GONE
                            if (availableComponents.isEmpty()) {
                                Snackbar.make(
                                    clayout_view,
                                    R.string.no_components,
                                    Snackbar.LENGTH_INDEFINITE
                                ).show()
                            }
                            componentAdapter.update(availableComponents)
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<Component>>, t: Throwable?) {
                    t?.printStackTrace()
                    //  context may be null
                    if (view != null) {
                        progressBar.visibility = View.GONE
                        Snackbar.make(
                            clayout_view,
                            R.string.error_connection,
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                }
            })
        } else {
            Log.d(TAG, "Unauthorized API call")
        }
    }

    companion object {
        const val TAG = "Debug:ViewFragment"
    }

}