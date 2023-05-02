package com.aleor.exchangesapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aleor.exchangesapp.databinding.ActivityDetailBinding


class DetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var etxt = binding.textView
    }
}