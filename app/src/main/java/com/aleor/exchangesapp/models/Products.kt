package com.aleor.exchangesapp.models
import com.beust.klaxon.*

private val klaxon = Klaxon()
data class Products(
    val name: String? = null,
    val description: String? = null,
    val category: String? = null,
    val sate: String? = null
){

    fun toJson() = klaxon.toJsonString(this)

    companion object {
        fun fromJson(json: String) = klaxon.parse<Products>(json)
    }


}
