package com.aleor.exchangesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aleor.exchangesapp.R
import com.aleor.exchangesapp.databinding.ActivityChatBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Boton de regreso a Home
        binding.backButton.setOnClickListener{
            backToHome()
        }

        //Boton de regreso a Home
        binding.rightButton.setOnClickListener{
            goToMap()
        }
    }

    //Regresar a Home
    private fun backToHome(){
        val intent =  Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun goToMap(){
        val intent =  Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

}