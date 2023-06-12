package com.aleor.exchangesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.aleor.exchangesapp.R
import com.aleor.exchangesapp.databinding.ActivityChatBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class Message(
    val senderUid: String,
    val receiverUid: String,
    val content: String
)

class ChatActivity : AppCompatActivity() {

    private lateinit var messageListView: ListView
    private lateinit var messageAdapter: ArrayAdapter<String>
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var messagesReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var currentUserUid: String
    private lateinit var receiverUid: String
    private lateinit var messageValueEventListener: ValueEventListener
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

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

        FirebaseApp.initializeApp(this)

        val auth = FirebaseAuth.getInstance()
        currentUserUid = auth.currentUser?.uid ?: ""
        val destinatarioUid = intent.getStringExtra("destinatarioUid")
        receiverUid = destinatarioUid ?: ""

        val chatId = intent.getStringExtra("chatId")
        if (chatId.isNullOrEmpty()) {
            createNewChat()
        } else {
            initializeChat(chatId)
        }

        messageListView = findViewById(R.id.messageListView)
        messageAdapter = ArrayAdapter(this, R.layout.item_message)
        messageListView.adapter = messageAdapter

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                val newMessageRef = messagesReference.push()
                val newMessage = Message(currentUserUid, receiverUid, message)
                newMessageRef.setValue(newMessage)
                messageEditText.text.clear()
            }
        }

        messageValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messageAdapter.clear()
                for (messageSnapshot in dataSnapshot.children) {
                    if (messageSnapshot.exists()) {
                        val messageContent = messageSnapshot.child("content").getValue(String::class.java)
                        if (messageContent != null) {
                            messageAdapter.add(messageContent)
                        }
                    }
                }
                messageListView.setSelection(messageAdapter.count - 1)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@ChatActivity,
                    "Error al acceder a Firebase: " + databaseError.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        updateMessageAdapter()
    }



    private fun updateMessageAdapter() {
        messagesReference.addValueEventListener(messageValueEventListener)
    }

    private fun createNewChat() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        val chatsReference = firebaseDatabase.reference.child("chats")
        val newChatReference = chatsReference.push()
        val newChatId = newChatReference.key
        if (newChatId != null) {
            messagesReference = newChatReference.child("messages")
            newChatReference.child("title").setValue("Inicia chat")
            newChatReference.child("participants").child(currentUserUid).setValue(true)
        } else {
            Toast.makeText(this, "Error al crear el nuevo chat", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeChat(chatId: String) {
        firebaseDatabase = FirebaseDatabase.getInstance()
        messagesReference = firebaseDatabase.reference.child("chats").child(chatId).child("messages")
    }

    override fun onDestroy() {
        super.onDestroy()
        messagesReference.removeEventListener(messageValueEventListener)
    }





    //Regresar a Home
    private fun backToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun goToMap() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }
}
