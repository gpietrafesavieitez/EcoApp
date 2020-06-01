package com.example.ecoapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ecoapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

/** This class extends from Activity and disposes main elements of the UI
 * @author Gabriel Pietrafesa Viéitez
 * @since v0.0.1-alpha
 * */
class MainActivity : AppCompatActivity() {

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
        Log.d(TAG, "I'm working")
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_view,
                R.id.navigation_scan,
                R.id.navigation_attach,
                R.id.navigation_history
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()
    }

    /** Handles menu item click event
     * @param item MenuItem
     * @return Boolean
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        // if language option is selected
        if (id == R.id.action_language) {
            setLanguage()
            // if logout option is selected
        } else if (id == R.id.action_logout) {
            signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    /** Displays an AlertDialog in order to allow the user selecting a language */
    private fun setLanguage() {
        // default locale
        var locale = Locale("en", "US")
        AlertDialog.Builder(this)
            .setTitle(R.string.language)
            .setSingleChoiceItems(arrayOf(getString(R.string.english), getString(R.string.spanish)), 0) { _, item ->
                // set english locale
                if (item == 0) {
                    locale = Locale("en", "US")
                }
                // set spanish locale
                if (item == 1) {
                    locale = Locale("es", "ES")
                }
            }
            .setPositiveButton(R.string.ok) { _, _ ->
                setLocale(locale)
            }
            .show()
    }

    /** Sets default locale and refresh this MainActivity
     * @param locale Locale
     * */
    @Suppress("DEPRECATION")
    private fun setLocale(locale: Locale) {
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        this.baseContext.resources.updateConfiguration(config, this.baseContext.resources.displayMetrics)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        application.resources.updateConfiguration(config, application.resources.displayMetrics)
        this.resources.updateConfiguration(config, this.resources.displayMetrics)
        val refresh = Intent(this, MainActivity::class.java)
        startActivity(refresh)
        finish()
    }

    /** Sets user icon image when menu is created
     * @param menu Menu
     * @return Boolean
     * */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.top_menu, menu)
        if (menu != null) {
            val profileIcon = menu.findItem(R.id.action_login)
            Glide.with(this)
                .asBitmap()
                .transition(BitmapTransitionOptions.withCrossFade())
                .load(sharedPreferences.getString("photoUrl", ""))
                .apply(RequestOptions().circleCrop())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        profileIcon.icon = BitmapDrawable(resources, resource)
                    }
                })
        }
        return true
    }

    /** Signs out user, deletes local data and returns to LoginActivity */
    private fun signOut() {
        Log.d(TAG, "User logged out")
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
        //  Refresh UI
        val refresh = Intent(this, LoginActivity::class.java)
        startActivity(refresh)
        finish()
    }

    /** Defines static constant variables for this class */
    companion object {
        const val TAG = "Debug:MainActivity"
        const val CAMERA_REQUEST = 123
    }

}