package com.example.chatting.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.chatting.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this@LoginActivity, UserActivity::class.java))
            finish()
        }

        btn_login.setOnClickListener {
            val email: String = et_email.text.toString().trim()
            val password: String = et_password.text.toString().trim()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Enter Email and Password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                loginUser(email, password)
            }
        }

        btn_signup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }

    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                et_email.setText("")
                et_password.setText("")
                startActivity(Intent(this@LoginActivity, UserActivity::class.java))
                finish()
            } else {
                Toast.makeText(applicationContext, "email or password invalid", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}