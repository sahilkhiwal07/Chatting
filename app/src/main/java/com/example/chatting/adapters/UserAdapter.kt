package com.example.chatting.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatting.R
import com.example.chatting.activity.ChatActivity
import com.example.chatting.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private val context: Context,
    private val userList: ArrayList<User>
) : RecyclerView.Adapter<UserAdapter.UserHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return UserHolder(view)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userImage: CircleImageView = itemView.findViewById(R.id.userImage)
        private val userName: AppCompatTextView = itemView.findViewById(R.id.name)
        private val layoutUser: LinearLayoutCompat = itemView.findViewById(R.id.layoutUser)

        fun bind(user: User) {
            userName.text = user.userName
            Glide.with(itemView.context)
                .load(user.profileImage)
                .placeholder(R.drawable.mia)
                .into(userImage)

            layoutUser.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java).apply {
                    putExtra("userId", user.userId)
                }
                itemView.context.startActivity(intent)
            }

        }
    }

}