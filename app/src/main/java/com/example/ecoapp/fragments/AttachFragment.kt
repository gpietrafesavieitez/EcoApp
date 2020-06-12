package com.example.ecoapp.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ecoapp.R
import com.example.ecoapp.activities.MainActivity
import com.example.ecoapp.classes.Component
import com.example.ecoapp.classes.Product
import com.example.ecoapp.networking.RetrofitBuilder.apiService
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_attach.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AttachFragment : Fragment() {

    private var filePath: String? = null
    private var hasImage: Boolean = false
    private var listOfUrls = ArrayList<String>()
    private var isListOfUrlsEmpty = true
    private var listView: ListView? = null
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attach, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ScanFragment.CODE = ""
        txtProductCode.setText("")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "I'm working")
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        listView = requireActivity().findViewById(R.id.multiple_list_view)
        getAllComponents()
        fabCamera.setOnClickListener {
            openCamera()
        }
        fabAttach.setOnClickListener {
            if (isProductReady()) {
                uploadProduct()
            }
        }
        if (!ScanFragment.CODE.isBlank() && ScanFragment.CODE.isNotEmpty()) {
            txtProductCode.setText(ScanFragment.CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainActivity.CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(context, R.string.explanation, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getAllComponents() {
        if (sharedPreferences.getBoolean("signedIn", false)) {
            apiService.getAllComponents().enqueue(object : Callback<ArrayList<Component>> {
                override fun onResponse(call: Call<ArrayList<Component>>, response: Response<ArrayList<Component>>) {
                    Log.i(TAG, response.toString())
                    if (response.body() != null) {
                        val listOfComponents: ArrayList<Component> = response.body() as ArrayList<Component>
                        if (context != null) {
                            val namesList = ArrayList<String>()
                            for (i in listOfComponents.indices) {
                                namesList.add(listOfComponents[i].name)
                            }
                            arrayAdapter = ArrayAdapter(requireActivity(), R.layout.row_component, namesList)
                            listView?.adapter = arrayAdapter
                            listView?.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                            listView?.itemsCanFocus = false
                            listView?.setOnItemClickListener { _, _, position, _ ->
                                val urlOfComponent =
                                    "/components/" + listOfComponents[position].code + "/"
                                if (listOfUrls.contains(urlOfComponent) && listOfUrls.isNotEmpty()) {
                                    listOfUrls.remove(urlOfComponent)
                                } else {
                                    listOfUrls.add(urlOfComponent)
                                }
                                isListOfUrlsEmpty = listOfUrls.isEmpty()
                            }
                        }
                        if (view != null) {
                            progressBar3.visibility = View.GONE
                            if (listOfComponents.isEmpty()) {
                                Snackbar.make(clayout_attach, R.string.no_components, Snackbar.LENGTH_INDEFINITE).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<Component>>, t: Throwable) {
                    t.printStackTrace()
                    if (view != null) {
                        progressBar3.visibility = View.GONE
                        Snackbar.make(clayout_attach, R.string.error_connection, Snackbar.LENGTH_INDEFINITE).show()
                    }
                }
            })
        } else {
            Log.d(TAG, "Unauthorized API call")
        }
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MainActivity.CAMERA_REQUEST)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImage()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (photoFile != null) {
                    val photoUri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.example.ecoapp.fileprovider",
                        photoFile
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(intent, MainActivity.CAMERA_REQUEST)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImage(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageName = "JPEG_" + timeStamp + "_"
        val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageName, ".jpg", storageDir)
        filePath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val file = File(filePath!!)
            val uri = Uri.fromFile(file)
            val imageStream: InputStream? = context?.contentResolver?.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(imageStream)
            fabCamera.setImageBitmap(bitmap)
            hasImage = true
        }
    }

    private fun uploadProduct() {
        progressBar3.visibility = View.VISIBLE
        if (sharedPreferences.getBoolean("signedIn", false)) {
            //progressBar.visibility = View.VISIBLE
            val mCode = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                txtProductCode.text.toString()
            )
            val mFormat =
                RequestBody.create(MediaType.parse("multipart/form-data"), ScanFragment.FORMAT)
            val mName = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                txtProductName.text.toString()
            )
            val image = File(filePath as String)
            val bodyImage = RequestBody.create(MediaType.parse("multipart/form-data"), image)
            val mImage = MultipartBody.Part.createFormData("image", image.name, bodyImage)
            val mComponents: ArrayList<MultipartBody.Part> = ArrayList()
            for (i in listOfUrls.indices) {
                mComponents.add(MultipartBody.Part.createFormData("components", listOfUrls[i]))
            }
            val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
            val objectUrl = sharedPreferences?.getString("objectUrl", "") as String
            val mUser = RequestBody.create(MediaType.parse("multipart/form-data"), objectUrl)
            apiService.addProduct(mCode, mFormat, mName, mImage, mComponents, mUser)
                .enqueue(object : Callback<Product> {
                    override fun onResponse(call: Call<Product>, response: Response<Product>) {
                        if (view != null) {
                            progressBar3.visibility = View.GONE
                            if (response.code() == 201) {
                                image.delete()
                                Toast.makeText(context, R.string.add_success, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.navigation_scan)
                            } else {
                                Log.d(TAG, response.message())
                                /** Guessing error code is because of duplicate product */
                                Snackbar.make(clayout_attach, R.string.error_duplicate, Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<Product>, t: Throwable) {
                        t.printStackTrace()
                        if (view != null) {
                            progressBar3.visibility = View.GONE
                            Snackbar.make(clayout_attach, R.string.error_connection, Snackbar.LENGTH_INDEFINITE).show()
                        }
                    }
                })
        } else {
            Log.d(TAG, "Unauthorized API call")
        }
    }

    private fun isProductReady(): Boolean {
        var valid = true
        if (!hasImage) {
            valid = false
            Snackbar.make(clayout_attach, R.string.alert_photo, Snackbar.LENGTH_SHORT)
                .setAction(R.string.upload_picture) {
                    openCamera()
                }
                .show()
        }
        if (txtProductCode.text.isNullOrEmpty() || txtProductCode.text.isNullOrBlank()) {
            valid = false
            txtProductCode.error = resources.getString(R.string.empty_field)
        }
        if (txtProductCode.length() > 200) {
            valid = false
            txtProductCode.error = resources.getString(R.string.long_code)
        }
        if (txtProductName.text.isNullOrEmpty() || txtProductName.text.isNullOrBlank()) {
            valid = false
            txtProductName.error = resources.getString(R.string.empty_field)
        }
        if (txtProductName.length() > 50) {
            valid = false
            txtProductName.error = resources.getString(R.string.long_name)
        }
        if (isListOfUrlsEmpty) {
            valid = false
            Snackbar.make(clayout_attach, R.string.at_least_one_component, Snackbar.LENGTH_SHORT)
                .show()
        }
        Log.d(TAG, listOfUrls.toString())
        return valid
    }

    companion object {
        const val TAG = "Debug:AttachFragment"
    }

}