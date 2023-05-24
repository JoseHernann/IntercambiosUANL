package com.aleor.exchangesapp.activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.aleor.exchangesapp.R
import com.aleor.exchangesapp.activities.ChatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class ItemChatActivity : AppCompatActivity() {
    private lateinit var chatListView: ListView
    private lateinit var chatAdapter: ArrayAdapter<String>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_chat)

        FirebaseApp.initializeApp(this)
        firestore = FirebaseFirestore.getInstance()

        chatListView = findViewById(R.id.chatListView)
        chatAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        chatListView.adapter = chatAdapter

        chatListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val chatId = chatAdapter.getItem(position)
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatId", chatId)
            startActivity(intent)
        }

        fetchChatUsers()
    }

    private fun fetchChatUsers() {
        firestore.collection("Clients").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val userName = document.getString("name")
                    chatAdapter.add(userName)
                }
            }
            .addOnFailureListener { exception ->
               Log.e(TAG, "Error al obtener los datos de los usuarios", exception)
            }
    }
}
