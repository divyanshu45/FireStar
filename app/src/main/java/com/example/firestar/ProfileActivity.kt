package com.example.firestar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firestar.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        firebaseAuth = FirebaseAuth.getInstance()

        btn_signOut.setOnClickListener {
            firebaseAuth.signOut().also {
                Intent(this@ProfileActivity, LoginActivity::class.java).also {
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(it)
                    finish()
                }
            }
        }

        loadDetails()
    }

    private fun loadDetails() {
        val uuid = firebaseAuth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance("https://fire-star-229d4-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")
        databaseReference.child(uuid!!).get().addOnSuccessListener {
            if(it.exists()){
                tv_fullName.text = it.child("fullName").value.toString()
                tv_coins.text = it.child("coins").value.toString()

                val coins = it.child("coins").value.toString().toInt()
                if(coins < 50){
                    iv_level.setImageResource(R.drawable.levelone)
                    tv_level.text = "Level-1"
                }else if(coins in 50..99){
                    iv_level.setImageResource(R.drawable.leveltwo)
                    tv_level.text = "Level-2"
                }else{
                    iv_level.setImageResource(R.drawable.levelthree)
                    tv_level.text = "Level-3"
                }

                tv_email.text = "Email: ${it.child("email").value.toString()}"
                tv_full.text = "Full Name: ${it.child("fullName").value.toString()}"
                tv_userName.text = "User Name: ${it.child("userName").value.toString()}"
                tv_phone.text = "Phone No.: ${it.child("phone").value.toString()}"

            }else{
                Toast.makeText(this@ProfileActivity, "User doesn't exists", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this@ProfileActivity, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
}
