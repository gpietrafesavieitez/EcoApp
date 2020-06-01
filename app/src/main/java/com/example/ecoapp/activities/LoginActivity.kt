package com.example.ecoapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ecoapp.R
import com.example.ecoapp.classes.FbUser
import com.example.ecoapp.networking.RetrofitBuilder
import com.example.ecoapp.networking.RetrofitBuilder.apiService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/** This class extends from Activity and handles the sign in process with Firebase and the API
 * @author Gabriel Pietrafesa Viéitez
 * @since v0.0.1-alpha
 * */
class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var editor: SharedPreferences.Editor

    /** Shared preferences and Firebase are instantiated when activity is created
     * @param savedInstanceState Bundle object
     * */
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "I'm working")
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()
        // Button listener for login
        button2.setOnClickListener {
            login()
        }
    }

    /** Starts the activity to authenticate with Google */
    private fun login() {
        Log.d(TAG, "Logging in...")
        //  Send intent for Google request
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /** Receives the result of the Google Auth activity and starts Firebase Auth if task is successful
     * @param requestCode
     * @param resultCode
     * @param data
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(MainActivity.TAG, "Google sign in failed", e)
            }
        }
    }

    /**
     * Starts Firebase Auth process and if the task is successful receives FirebaseUser object and
     * sends it to an API call to get user data. If user doesn't exist on the server will be created.
     * @param acct GoogleSignInAccount object with the necessary credentials for Firebase Auth
     * */
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(MainActivity.TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        progressBar4.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Successful login with Google
                    Log.d(MainActivity.TAG, "signInWithCredential:success")
                    val googleUser = mAuth.currentUser
                    // Is this the best way to check auth status?
                    editor.putBoolean("signedIn", true)
                    editor.putString("photoUrl", googleUser?.photoUrl.toString())
                    editor.apply()
                    getFbUserById(googleUser)
                } else {
                    //  If login fails
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    /** Method to make the API call with Firebase credentials and receive necessary user data to do other requests
     * @param googleUser firebaseUser object sent to the API call
     * @exception JSONException occurs when response body format is unexpected
     * */
    private fun getFbUserById(googleUser: FirebaseUser?) {
        if (googleUser != null) {
            apiService.getFbUserByUid(googleUser.uid).enqueue(object : Callback<List<FbUser>> {
                override fun onResponse(call: Call<List<FbUser>>, response: Response<List<FbUser>>) {
                    try {
                        val fbUser = response.body()
                        if (fbUser != null) {
                            for (i in fbUser.indices) {
                                if (googleUser.uid == fbUser[i].uid) {
                                    Log.d(TAG, fbUser[i].toString())
                                    //  get full url of the user
                                    val rawUrl = fbUser[i].user.url
                                    //  split url using the server string as a pattern and gets the index that contains the user object
                                    val objectUrl = rawUrl.split(RetrofitBuilder.BASE_URL)[1]
                                    //  number filtering to get the id
                                    val id = objectUrl.filter { it.isDigit() }.toInt()
                                    editor.putString("rawUrl", rawUrl)
                                    editor.putString("objectUrl", objectUrl)
                                    editor.putInt("id", id)
                                    //  save the data
                                    editor.apply()
                                    // start main activity
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    progressBar4.visibility = View.INVISIBLE
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        } else {
                            Log.d(TAG, "fbUser is null")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<List<FbUser>>, t: Throwable) {
                    // if a problem occurs FirebaseUser will be signed out
                    logout()
                    progressBar4.visibility = View.INVISIBLE
                    Snackbar.make(clayout_login, R.string.error_connection, Snackbar.LENGTH_LONG).show()
                    t.printStackTrace()
                }
            })
        } else {
            Log.d(TAG, "currentUser is null")
        }
    }

    /** Signs out user and deletes local data */
    private fun logout() {
        Log.d(MainActivity.TAG, "User logged out")
        //  Firebase sign out
        mAuth.signOut()
        //  Google sign out
        googleSignInClient.signOut()
        //  Remove shared preferences
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            deleteSharedPreferences("user")
        } else {
            sharedPreferences.edit().clear().apply()
        }
    }

    /** Defines static constant variables for this class */
    companion object {
        const val TAG = "Debug:LoginActivity"
        private const val RC_SIGN_IN = 9001
    }

}