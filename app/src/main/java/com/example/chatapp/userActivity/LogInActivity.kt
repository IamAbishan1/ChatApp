package com.example.chatapp.userActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val email = findViewById<EditText>(R.id.emailLogIn)
        val password = findViewById<EditText>(R.id.passwordLogIn)
        val logIn = findViewById<Button>(R.id.logInBtn)
        val navToRegister = findViewById<TextView>(R.id.navToRegister)

        logIn.setOnClickListener {
            val emailLogIn = email.text.toString()
            val passwordLogIn = password.text.toString()

            if(emailLogIn.isEmpty() || passwordLogIn.isEmpty()){
                Toast.makeText(this,"These fields cannot be empty!", Toast.LENGTH_LONG).show()

            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailLogIn,passwordLogIn)
                .addOnCompleteListener {
                    Log.d("LogInActivity","Attempt login with email/pw: $email/***")



                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("LogInActivity","Failed to log in: ${it.message}")

                }
        }

        navToRegister.setOnClickListener {
            finish()
        }
    }


}