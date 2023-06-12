package com.aleor.exchangesapp.providers

import com.aleor.exchangesapp.models.Client
import com.aleor.exchangesapp.models.Products
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ClientProvider {
    private val clientsCollection = Firebase.firestore.collection("Clients")
    fun create(client: Client): Task<Void> {
        return clientsCollection.document(client.id!!).set(client)
    }
}