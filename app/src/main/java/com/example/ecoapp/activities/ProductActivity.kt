package com.example.ecoapp.activities

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ecoapp.R
import com.example.ecoapp.adapters.ComponentAdapter
import com.example.ecoapp.classes.Component
import com.example.ecoapp.classes.Product
import com.example.ecoapp.networking.RetrofitBuilder
import com.example.ecoapp.networking.RetrofitBuilder.apiService
import kotlinx.android.synthetic.main.activity_product.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/** This class extends from Activity and draws a popup showing Product details
 * @author Gabriel Pietrafesa Viéitez
 * @since v0.0.1-alpha
 * */
class ProductActivity : Activity() {

    private lateinit var mProduct: Product
    private var componentList = ArrayList<Component>()
    private lateinit var componentAdapter: ComponentAdapter

    /** Custom window for the popup and components instantiation. Components are fetched too.
     * @param savedInstanceState Bundle object
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        // window is generated depending on current display resolution
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val w = displayMetrics.widthPixels
        val h = displayMetrics.heightPixels
        window.setLayout((w * .85).toInt(), (h * .8).toInt())
        mProduct = intent.getSerializableExtra("Product") as Product
        componentAdapter = ComponentAdapter(componentList, this)
        recyclerView2.adapter = componentAdapter
        recyclerView2.layoutManager = LinearLayoutManager(this)
        // set the text
        textView.text = mProduct.name
        // set the image
        Glide.with(this)
            .load(mProduct.image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(viewViewpImg)
        // fetch components
        getAllComponents()
    }

    /** Request to fetch all components of the products and put them in the adapter */
    private fun getAllComponents() {
        progressBar2.visibility = View.VISIBLE
        apiService.getAllComponents().enqueue(object : Callback<ArrayList<Component>> {
            override fun onResponse(call: Call<ArrayList<Component>>, response: Response<ArrayList<Component>>) {
                progressBar2.visibility = View.GONE
                val availableComponents = response.body()
                val componentsFetched = ArrayList<Component>()
                if (availableComponents != null) {
                    Log.d("availableComponents", availableComponents.toString())
                    Log.d("productComponents", mProduct.components.toString())
                    // cursor to fetch all components
                    for (i in availableComponents.indices) {
                        // cursor to fetch components of the product
                        for (j in mProduct.components.indices) {
                            val string = RetrofitBuilder.BASE_URL + "/components/" + availableComponents[i].code + "/"
                            // if a component matches, the icon of that component will lighted up
                            if (mProduct.components[j] == string) {
                                Log.d("debug", availableComponents[i].name + " -> " + availableComponents[i].recycleType)
                                componentsFetched.add(availableComponents[i])
                                when (availableComponents[i].recycleType) {
                                    1 -> {
                                        imgYellow2.setImageResource(R.drawable.ic_light_yellow)
                                    }
                                    2 -> {
                                        imgGreen2.setImageResource(R.drawable.ic_light_green)
                                    }
                                    3 -> {
                                        imgBlue2.setImageResource(R.drawable.ic_light_blue)
                                    }
                                    4 -> {
                                        imgBrown2.setImageResource(R.drawable.ic_light_brown)
                                    }
                                    5 -> {
                                        imgRed2.setImageResource(R.drawable.ic_light_red)
                                    }
                                    6 -> {
                                        imgGrey2.setImageResource(R.drawable.ic_light_grey)
                                    }
                                    7 -> {
                                        imgWhite2.setImageResource(R.drawable.ic_light_white)
                                    }
                                    8 -> {
                                        imgWhite2.setImageResource(R.drawable.ic_light_white)
                                    }
                                    9 -> {
                                        imgWhite2.setImageResource(R.drawable.ic_light_white)
                                    }
                                    10 -> {
                                        imgWhite2.setImageResource(R.drawable.ic_light_white)
                                    }
                                    else -> {
                                        imgWhite2.setImageResource(R.drawable.ic_light_null)
                                    }
                                }
                            }
                        }
                        // notify changes to the adapter
                        componentAdapter.update(componentsFetched)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Component>>, t: Throwable) {
                progressBar2.visibility = View.GONE
                t.printStackTrace()
            }
        })
    }
}