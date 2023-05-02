package com.aleor.exchangesapp.models
import com.beust.klaxon.Klaxon

private val klaxon = Klaxon()
data class Products(
    val id: String,
    val name: String? = null,
    val description: String? = null,
    val category: String? = null,
    val sate: String? = null,
    val userEmail:String? = null,
    val faculty:String? = null
){
    fun Product() {}
    fun toJson() = klaxon.toJsonString(this)

    companion object {
        fun fromJson(json: String) = klaxon.parse<Products>(json)
    }


}
