package com.aleor.exchangesapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MessageListAdapter(context: Context, messages: List<String>) : ArrayAdapter<String>(context, 0, messages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val message = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        }

        val messageTextView = view?.findViewById<TextView>(android.R.id.text1)
        messageTextView?.text = message

        return view!!
    }
}
