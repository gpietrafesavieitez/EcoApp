package com.example.ecoapp.networking

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Custom Builder to simplify Retrofit instantiation
 * @author Gabriel Pietrafesa Viéitez
 * @since v0.0.1-alpha
 * */
object RetrofitBuilder {

    const val BASE_URL = "http://192.168.0.14:8000"

    /**
     * Builds and returns a Retrofit object that will be used on the requests
     * @return Retrofit object built
     */
    private fun getRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(TokenHandler())
            .build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)

}