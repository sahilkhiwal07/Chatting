package com.example.chatting.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.chatting.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        btn_signup.setOnClickListener {
            val userName = et_Name.text.toString().trim()
            val userEmail = et_email.text.toString().trim()
            val password = et_password.text.toString().trim()
            val confirmPassword = et_confirm_password.text.toString().trim()

            if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userEmail)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)
            ) {
                Toast.makeText(applicationContext, "Empty credentials!", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(applicationContext, "Password is too short! Must be greater than 6 digit", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(applicationContext, "Password doesn't match", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(userName, userEmail, password)
            }
        }

        btn_login.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }

    }

    private fun registerUser(userName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                val userId: String = user!!.uid

                database = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                val hashMap: HashMap<String, String> = HashMap()
                hashMap["userId"] = userId
                hashMap["userName"] = userName
                hashMap["profileImage"] = ""

                database.setValue(hashMap).addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        et_Name.setText("")
                        et_email.setText("")
                        et_password.setText("")
                        et_confirm_password.setText("")

                        startActivity(Intent(this@SignUpActivity, UserActivity::class.java))
                        finish()
                    }
                }

            } else {
                Toast.makeText(applicationContext, "Registration Failed", Toast.LENGTH_SHORT).show()
            }

        }
    }


}