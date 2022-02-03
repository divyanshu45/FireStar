package com.example.firestar.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firestar.MainActivity
import com.example.firestar.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()


        btn_signIn.setOnClickListener {
            loginUser()
        }

        tv_goToSignUp.setOnClickListener {
            Intent(this@LoginActivity, SignUpActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    private fun loginUser(){
        val email = et_email.text.toString()
        val password = et_password.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@LoginActivity) { task ->
                        if(task.isSuccessful) {
                            Intent(this@LoginActivity, MainActivity::class.java).also {
                                startActivity(it)
                                finish()
                            }
                        }else{
                            Toast.makeText(baseContext, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@LoginActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            Intent(this@LoginActivity, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}