package com.example.ecoapp.networking

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/** This class extends from Interceptor interface and handles OAuth between Firebase and the API
 * @author Gabriel Pietrafesa Viéitez
 * @since v0.0.1-alpha
 * */
class TokenHandler : Interceptor {

    /**
     * Intercepts current request and injects token auth on HTTP headers
     * @param chain Interceptor.Chain object
     * @return response with modified request
     * */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val task = user.getIdToken(false)
            val tokenResult = Tasks.await(task)
            val idToken = tokenResult.token
            val modifiedRequest: Request = request.newBuilder().addHeader("Authorization", "JWT $idToken").build()
            Log.d(TAG, idToken.toString())
            return chain.proceed(modifiedRequest)
        } else { //  TODO: handle token expiration event
            Log.w(TAG, "API call from unauthorized FirebaseUser")
            return chain.proceed(request)
        }
    }

    /** Defines static constant variables for this class */
    companion object {
        const val TAG = "Debug:TokenHandler"
    }

}