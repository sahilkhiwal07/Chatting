package com.example.chatting.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatting.R
import com.example.chatting.adapters.UserAdapter
import com.example.chatting.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {

    private lateinit var firebase: FirebaseUser
    private lateinit var database: DatabaseReference

    private lateinit var userAdapter: UserAdapter
    var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        img_back_user.setOnClickListener {
            onBackPressed()
        }

        img_profile.setOnClickListener {
            startActivity(Intent(this@UserActivity, ProfileActivity::class.java))
            finish()
        }

        getUserList()

    }

    private fun getUserList() {
        firebase = FirebaseAuth.getInstance().currentUser!!
        database = FirebaseDatabase.getInstance().getReference("Users")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                // setting image on the side of text in recycler view
                val currentUser = snapshot.getValue(User::class.java)
                if (currentUser!!.profileImage == "") {
                    img_profile.setImageResource(R.drawable.mia)
                } else {
                    Glide.with(this@UserActivity).load(currentUser.profileImage).into(img_profile)
                }

                // if you put " == " then only login user show in list
                // to show all users except login user use " != "
                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)
                    if (user!!.userId != firebase.uid) {
                        userList.add(user)
                    }
                }

                setRecyclerView()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun setRecyclerView() {
        userAdapter = UserAdapter(this@UserActivity, userList)
        user_recyclerView.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@UserActivity)
        }
    }

}