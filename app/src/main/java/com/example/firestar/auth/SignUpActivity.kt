package com.example.firestar.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firestar.MainActivity
import com.example.firestar.R
import com.example.firestar.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.*

class SignUpActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btn_signUp.setOnClickListener {
            registerUser()
        }

        tv_goToSignIn.setOnClickListener {
            Intent(this@SignUpActivity, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun registerUser(){
        val username= et_userName.text.toString()
        val fullName = et_fullName.text.toString()
        val phone = et_mobile.text.toString()
        val email = et_email.text.toString()
        val password = et_password.text.toString()
        val user = User(username, fullName, phone, email, password, 0)

        firebaseDatabase = FirebaseDatabase.getInstance("https://fire-star-229d4-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")

        if(username.isNotEmpty() && fullName.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful){
                            val uuid = firebaseAuth.currentUser!!.uid
                            firebaseDatabase.child(uuid).setValue(user).addOnSuccessListener {
                                Intent(this@SignUpActivity, MainActivity::class.java).also {
                                    Toast.makeText(this@SignUpActivity, "Sign Up successfully", Toast.LENGTH_SHORT).show()
                                    startActivity(it)
                                    finish()
                                }
                            }
                        }else{
                            Toast.makeText(this@SignUpActivity, "Sign Up failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@SignUpActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            Toast.makeText(this@SignUpActivity, "Please fill the credentials properly", Toast.LENGTH_SHORT).show()
        }
    }
}