package com.aleor.exchangesapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aleor.exchangesapp.databinding.ActivityHomeBinding
import com.aleor.exchangesapp.databinding.ActivityUserProfileBinding

class UserProfile : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}