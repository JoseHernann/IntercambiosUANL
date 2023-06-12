package com.aleor.exchangesapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ChatListAdapter(context: Context, chats: List<String>) : ArrayAdapter<String>(context, 0, chats) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val chatId = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        }

        val chatTextView = view?.findViewById<TextView>(android.R.id.text1)
        chatTextView?.text = chatId

        return view!!
    }
}
