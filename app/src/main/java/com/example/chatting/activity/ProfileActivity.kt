package com.example.chatting.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.chatting.R
import com.example.chatting.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_user.*
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var database: DatabaseReference

    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 20

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        database = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                et_userName.setText(user!!.userName)

                if (user.profileImage == "") {
                    userImage.setImageResource(R.drawable.mia)
                } else {
                    Glide.with(this@ProfileActivity)
                        .load(user.profileImage)
                        .into(userImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        img_back_profile.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, UserActivity::class.java))
            finish()
        }

        userImage.setOnClickListener { chooseImage() }

        btn_save.setOnClickListener {
            uploadImages()
            progressBar.visibility = View.VISIBLE
        }

        btn_signOut.setOnClickListener {
            onAlertDialog(it)
        }

    }

    private fun onAlertDialog(view: View) {
        AlertDialog.Builder(view.context).apply {
            setTitle(R.string.alert_title)
            setMessage(R.string.alert_message)
            setNegativeButton("Cancel") { _, _ -> }
            setPositiveButton("Log Out") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                finish()
            }
        }.create().show()
    }

    private fun chooseImage() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST) {
            filePath = data!!.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                userImage.setImageBitmap(bitmap)
                btn_save.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    // updating profile images and user name
    private fun uploadImages() {
        if (filePath != null) {
            progressBar.visibility = View.VISIBLE
            val ref = storageReference.child("image/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["userName"] = et_userName.text.toString()
                    hashMap["profileImage"] = filePath.toString()

                    database.updateChildren(hashMap as Map<String, Any>)

                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Uploaded", Toast.LENGTH_SHORT).show()
                    btn_save.visibility = View.GONE

                }
                .addOnFailureListener {

                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Failed" + it.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

}