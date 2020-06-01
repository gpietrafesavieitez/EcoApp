package com.example.ecoapp.classes

/** This data class defines main model of User
 * @author Gabriel Pietrafesa Viéitez
 * @since v0.0.1-alpha
 * */
data class User(
    val url: String,
    val username: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val groups: List<Any>
)

/** This data class defines main model of FbUser
 * @author Gabriel Pietrafesa Viéitez
 * @since v0.0.1-alpha
 * */
data class FbUser(
    val id: Int,
    val uid: String,
    val user: User
)