package com.example.ecoapp.classes

/** This data class extends from Serializable and defines main model of Product
 * @author Gabriel Pietrafesa Viéitez
 * @since v0.0.1-alpha
 * */
data class Product(
    val code: String,
    val format: String,
    val name: String,
    val image: String,
    val fbuser: String,
    val components: List<String>,
    val date: String,
    var username: String
) : java.io.Serializable

/** This data class extends from Serializable and defines main model of Component
 * @author Gabriel Pietrafesa Viéitez
 * @since v0.0.1-alpha
 * */
data class Component(
    val code: Int,
    val name: String,
    val recycleType: Int,
    val image: String
) : java.io.Serializable