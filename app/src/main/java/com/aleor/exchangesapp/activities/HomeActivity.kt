package com.aleor.exchangesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aleor.exchangesapp.R
import com.aleor.exchangesapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
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
                }
                else -> false
            }
        }
    }


    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityHomeBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.goToForms.setOnClickListener {
//            val intent =  Intent(this, FormsActivity::class.java)
//            startActivity(intent)
//        }
//    }
}