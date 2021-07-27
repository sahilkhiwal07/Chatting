package com.example.chatting.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.R
import com.example.chatting.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChatAdapter(
    private val context: Context,
    private val chatList: ArrayList<Chat>
) : RecyclerView.Adapter<ChatAdapter.ChatHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false)
            ChatHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false)
            ChatHolder(view)
        }

    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        val chat = chatList[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class ChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // right side bcz that is sending side
        // left one is receiving side
        private val txtMessage: AppCompatTextView = itemView.findViewById(R.id.tvMessage)

        fun bind(chat: Chat) {
            txtMessage.text = chat.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (chatList[position].senderId == firebaseUser!!.uid) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }
    }

}