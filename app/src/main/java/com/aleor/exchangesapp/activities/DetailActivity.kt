package com.aleor.exchangesapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aleor.exchangesapp.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class DetailActivity: AppCompatActivity() {


    private lateinit var binding: ActivityDetailBinding
    private val myAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val users = db.collection("Clients")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Elementos graficos del detalle
        val mainPic = binding.mainPic
        val productName = binding.productName
        val prodState = binding.prodState
        val prodCat = binding.prodCat
        val prodDescript = binding.prodDescrip
        val prodDisop = binding.prodDisp
        val userPic  = binding.userPic
        val userName = binding.userName
        val userFac = binding.userFac
        var userEmail:String = ""
        var faculty = ""
        var name = ""
        binding.back.setOnClickListener{
            backToHome()
        }




        val productId = intent.getStringExtra("productId")
        println(productId)

        if (productId != null) {
            db.collection("Products").document(productId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null){
                        val productData = document.toObject(HomeActivity.Product::class.java)
                        if (productData != null) {
                            productName.text = productData.name
                            prodState.text = productData.state
                            prodDescript.text = productData.description
                            prodCat.text = productData.category
                            userEmail = productData.userEmail.toString()
                            users
                                .whereEqualTo("email", userEmail)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (documentUsers in documents) {
                                        faculty = documentUsers.getString("faculty").toString()
                                        userFac.text = faculty
                                        name = documentUsers.getString("name").toString()
                                        userName.text = name
                                    }
                                }
                        }
                    }
                }
        }


        val storageRef = Firebase.storage.reference.child("/${productId}")
        storageRef.listAll().addOnSuccessListener { listResult ->
            if (listResult.items.isNotEmpty()) {
                val firstImageRef = listResult.items.first()
                firstImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this)
                        .load(uri)
                        .into(mainPic)
                }
            }
        }









    }
    private fun backToHome(){
        val intent =  Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}