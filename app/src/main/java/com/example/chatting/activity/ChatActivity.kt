package com.example.chatting.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatting.R
import com.example.chatting.adapters.ChatAdapter
import com.example.chatting.model.Chat
import com.example.chatting.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_user.*

class ChatActivity : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = null
    private var reference: DatabaseReference? = null

    var chatList = ArrayList<Chat>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // data coming from user adapter
        val userId = intent.getStringExtra("userId")!!

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)    // here child provide opening chat name at the top of tab

        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                tvUserName.text = user!!.userName
                if (user.profileImage == "") {
                    img_profile_chat.setImageResource(R.drawable.mia)
                } else {
                    Glide.with(this@ChatActivity)
                        .load(user.profileImage)
                        .into(img_profile_chat)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message,Toast.LENGTH_SHORT).show()
            }

        })

        img_back_chat.setOnClickListener {
            onBackPressed()
        }

        btn_send_message.setOnClickListener {
            val message = et_chat.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(applicationContext,"Message is Empty",Toast.LENGTH_SHORT).show()
                et_chat.setText("")
            } else {
                sendMessage(firebaseUser!!.uid, userId, message)
                et_chat.setText("")
            }
        }

        readMessages(firebaseUser!!.uid, userId)

    }

    // sending message to database
    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference

        val hashMap: HashMap<String,String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message

        reference.child("Chat").push().setValue(hashMap)
    }

    private fun readMessages(senderId: String, receiverId: String) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (data in snapshot.children) {
                    val chat = data.getValue(Chat::class.java)

                    if (chat!!.senderId == senderId && chat.receiverId == receiverId ||
                        chat.senderId == receiverId && chat.receiverId == senderId) {

                        chatList.add(chat)
                    }
                }

                setChatRecyclerView()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setChatRecyclerView() {
        chatAdapter = ChatAdapter(this@ChatActivity, chatList)
        chat_recyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity)
        }
    }

}