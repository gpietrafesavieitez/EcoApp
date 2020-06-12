package com.example.ecoapp.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.example.ecoapp.R
import com.example.ecoapp.activities.MainActivity
import com.example.ecoapp.activities.ProductActivity
import com.example.ecoapp.classes.Product
import com.example.ecoapp.networking.RetrofitBuilder.apiService
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_scan.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanFragment : Fragment() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "I'm working")
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        if (ContextCompat.checkSelfPermission(
                context as Context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                MainActivity.CAMERA_REQUEST
            )
        } else {
            startScanner()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainActivity.CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner()
            } else {
                Toast.makeText(context, R.string.explanation, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startScanner() {
        // Parameters (default values)
        codeScanner = CodeScanner(context as Context, scanner_view)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type Barformat,
        // ex. listOf(Barformat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                c = it.text as String
                f = it.barcodeFormat.toString()
                Toast.makeText(context, c, Toast.LENGTH_LONG).show()
                getProductByCode(c)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            activity?.runOnUiThread {
                Toast.makeText(
                    context,
                    "Camera initialization error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun getProductByCode(code: String) {
        if (sharedPreferences.getBoolean("signedIn", false)) {
            apiService.getProductsByCode(code).enqueue(object : Callback<List<Product>> {
                override fun onResponse(
                    call: Call<List<Product>>,
                    response: Response<List<Product>>?
                ) {
                    val products = response?.body()
                    Log.i("Producto", products.toString())
                    try {
                        if (products != null) {
                            if (products.isNotEmpty()) {
                                for (i in products.indices) {
                                    /**  should index be 0? is a list but always one product inside */
                                    val p = products[i]
                                    if (context != null) {
                                        val intent = Intent(context, ProductActivity::class.java)
                                        intent.putExtra("Product", p)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    }
                                }
                            } else {
                                if (view != null) {
                                    Snackbar.make(
                                        clayout_scan,
                                        R.string.no_products,
                                        Snackbar.LENGTH_LONG
                                    )
                                        .setAction(R.string.activity_attach_title) {
                                            CODE = c
                                            FORMAT = f
                                            findNavController().navigate(R.id.navigation_attach)
                                        }
                                        .show()
                                }
                            }
                        }
                    } catch (e: JSONException) {
                        if (view != null) {
                            Toast.makeText(context, R.string.error_json, Toast.LENGTH_SHORT).show()
                        }
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable?) {
                    //  context may be null
                    if (context != null) {
                        Toast.makeText(context, R.string.error_connection, Toast.LENGTH_SHORT)
                            .show()
                    }
                    t?.printStackTrace()
                }
            })
        } else {
            Log.d(ViewFragment.TAG, "Unauthorized API call")
        }
    }

    companion object {
        const val TAG = "Debug:ScanFragment"
        private var c = ""
        private var f = ""
        var CODE = ""
        var FORMAT = "EAN_13"
    }

}