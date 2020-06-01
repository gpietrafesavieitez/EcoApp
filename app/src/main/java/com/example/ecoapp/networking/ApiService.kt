package com.example.ecoapp.networking

import com.example.ecoapp.classes.Component
import com.example.ecoapp.classes.FbUser
import com.example.ecoapp.classes.Product
import com.example.ecoapp.classes.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/** Custom interface for Retrofit requests
 * @author Gabriel Pietrafesa Viéitez
 * @since v0.0.1-alpha
 * */
interface ApiService {

    /**
     * Receives all products with GET request
     * @return call of ArrayList of Products
     */
    @GET("products/")
    fun getAllProducts(): Call<ArrayList<Product>>

    /**
     * Receives FbUser filtered by its Uid with GET request
     * @param fbuser FbUser Uid
     * @return call of List of FbUsers
     */
    @GET("firebaseusers/")
    fun getFbUserByUid(@Query("fbuser") fbuser: String): Call<List<FbUser>>

    /**
     * Receives all users with GET request
     * @return call of List of Users
     */
    @GET("users/")
    fun getAllUsers(): Call<List<User>>

    /**
     * Receives products filtered by the user with GET request
     * @param fbuser id of the user
     * @return call of ArrayList of Products
     */
    @GET("products/")
    fun getProductsByUser(@Query("fbuser") fbuser: Int): Call<ArrayList<Product>>

    /**
     * Receives products filtered by its code with GET request
     * @param code code of the product
     * @return call of List of Products
     */
    @GET("products/")
    fun getProductsByCode(@Query("code") code: String): Call<List<Product>>

    /**
     * Receives all components with GET request
     * @return call of ArrayList of Components
     */
    @GET("components/")
    fun getAllComponents(): Call<ArrayList<Component>>

    /**
     * Send product with POST request
     * @param code RequestBody type
     * @param format RequestBody type
     * @param name RequestBody type
     * @param image MultipartBody.Part type
     * @param components ArrayList of MultipartBody.Part objects
     * @param fbuser RequestBody type
     * @return call of Product
     */
    @Multipart
    @POST("products/")
    fun addProduct(
        @Part("code") code: RequestBody,
        @Part("format") format: RequestBody,
        @Part("name") name: RequestBody,
        @Part image: MultipartBody.Part,
        @Part components: ArrayList<MultipartBody.Part>,
        @Part("fbuser") fbuser: RequestBody
    ): Call<Product>

}