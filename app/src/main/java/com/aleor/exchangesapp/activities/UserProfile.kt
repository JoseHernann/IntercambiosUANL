package com.aleor.exchangesapp.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aleor.exchangesapp.R
import com.aleor.exchangesapp.databinding.ActivityUserProfileBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var bottomNavigationView: BottomNavigationView

    companion object {
        private const val REQUEST_IMAGE_CHOOSER = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //FUNCIONALIDAD DEL BOTTOM MENU BAR
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.selectedItemId = R.id.menu_profile

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.menu_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }R.id.menu_map -> {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
                true
            }
                R.id.menu_add -> {
                    val intent = Intent(this, FormsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_profile -> {

                    val intent = Intent(this, UserProfile::class.java)
                    startActivity(intent)
                    true
                }R.id.menu_chat -> {
                val intent = Intent(this, ItemChatActivity::class.java)
                startActivity(intent)
                true
            }
                else -> false
            }
        }

        //Boton de regreso
        binding.toolbar.setOnClickListener{
            backToHome()
        }
        val userNameInterface = binding.profileName
        val userNameFaculty = binding.profileFaculty
        val userEmail = binding.profileEmail

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser

        if (user != null) {
            db.collection("Clients")
                .whereEqualTo("id", user.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    println(querySnapshot.documents)
                    for (document in querySnapshot.documents) {
                        val name = document.getString("name").toString()
                        val email = document.getString("email").toString()
                        val phone = document.getString("faculty").toString()
                        val photoUrl = document.getString("photoUrl")

                        userNameInterface.text = name
                        userNameFaculty.text = phone
                        userEmail.text = email

                        // Cargar una imagen de perfil desde la galeria
                        if (photoUrl != null) {
                            Glide.with(this)
                                .load(photoUrl)
                                .placeholder(R.drawable.basic_input)
                                .into(binding.profileimage)
                        }
                    }
            }
        }

            //Accesos necesarios para cambiar la foto de perfil
        binding.profileimage.setOnClickListener {
            openImageChooser()
        }

        binding.changeProfileImageButton.setOnClickListener {
            openImageChooser()
        }
    }



    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_CHOOSER)
    }

    private fun backToHome(){
        val intent =  Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CHOOSER && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                // Envia la imagen a la storage de la Firestore
                val user = auth.currentUser
                if (user != null) {
                    val storageRef =
                        FirebaseStorage.getInstance().reference.child("profile_photos/${user.uid}")
                    storageRef.putFile(imageUri)
                        .addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { uri ->
                                val photoUrl = uri.toString()

                                db.collection("users").document(user.uid)
                                    .update("photoUrl", photoUrl)
                                    .addOnSuccessListener {
                                        Glide.with(this@UserProfile)
                                            .load(photoUrl)
                                            .placeholder(R.drawable.basic_input)
                                            .into(binding.profileimage)
                                    }
                            }
                        }
                }
            }
        }
    }
}
