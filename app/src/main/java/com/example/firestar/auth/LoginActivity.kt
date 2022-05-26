package com.example.firestar.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firestar.MainActivity
import com.example.firestar.R
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class LoginActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    val objectMapper = jacksonObjectMapper()

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

        val client = OkHttpClient()

        GlobalScope.launch(Dispatchers.IO) {
            val mediaType = "application/x-www-form-urlencoded".toMediaType()
            val body = "linkFile1=https://admin.thecricketer.com/weblab/Sites/96c8b790-b593-bfda-0ba4-ecd3a9fdefc2/resources/images/site/kohliheadshot-min.jpg&linkFile2=https://i.pinimg.com/originals/f0/5e/58/f05e58ea32f314a7bb39e2306ef90c14.png".toRequestBody(mediaType)
            val request = Request.Builder()
                .url("https://face-verification2.p.rapidapi.com/faceverification")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("X-RapidAPI-Host", "face-verification2.p.rapidapi.com")
                .addHeader("X-RapidAPI-Key", "9d366f6cbdmsh32b7c6b43e6bcdep14c956jsn81c1a47b0532")
                .build()

            client.newCall(request).execute().use {
                val parsedResponse = parseResponse(it)
                println(parsedResponse.toString())
                println(parsedResponse["data"]["resultMessage"])
                val result = parsedResponse["data"]["similarPercent"].toString().toDouble()
                if(result > 0.80) println("YES")
                else println("NO")
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

    fun parseResponse(response : Response): JsonNode {
        val body = response.body?.string() ?: ""
        val jsonBody = objectMapper.readValue<JsonNode>(body)
        return jsonBody
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            Intent(this@LoginActivity, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}