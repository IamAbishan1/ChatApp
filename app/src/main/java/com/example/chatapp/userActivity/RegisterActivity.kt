package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.chatapp.messages.LatestMessagesActivity
import com.example.chatapp.models.User
import com.example.chatapp.userActivity.LogInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class RegisterActivity : AppCompatActivity() {
    private lateinit var register:Button
    private lateinit var email:EditText
    private lateinit var username:EditText
    private lateinit var password:EditText
    private lateinit var navLogin:TextView
    private lateinit var photoBtn:CircleImageView

    var selectedPhotoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username=findViewById(R.id.userName)
        register = findViewById(R.id.registerBtn)
        email = findViewById(R.id.emailET);
        password = findViewById(R.id.passwordET);
        navLogin = findViewById(R.id.navToLogin)
        photoBtn = findViewById(R.id.userNamePic)


        register.setOnClickListener {
            makeRegistration()

        }

        navLogin.setOnClickListener {

            val intent = Intent(this, LogInActivity::class.java);
            startActivity(intent);
        }

        photoBtn.setOnClickListener {
            Log.d("RegisterActivity","Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //proceed and check what the selected image was...
            Log.d("RegisterActivity","Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            photoBtn.setImageBitmap(bitmap)

//            val bitmapDrawable = BitmapDrawable(bitmap)
//
//            photoBtn.setBackgroundDrawable(bitmapDrawable)


        }
    }

    private fun makeRegistration() {
        val emailAddress = email.text.toString();
        val passwordUser = password.text.toString();

        if(emailAddress.isEmpty() || passwordUser.isEmpty()){
            Toast.makeText(this,"These fields cannot be empty!",Toast.LENGTH_LONG).show()
            return
        }

        Log.d("RegisterActivity","Email is: "+emailAddress)
        Toast.makeText(this,email.text.toString(),Toast.LENGTH_LONG).show();

        //Firebase Authenticationn to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailAddress,passwordUser)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d("RegisterActivity","Successfully created user with uid: ${it.result.user?.uid}")

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("RegisterActivity","Failed to create user: ${it.message}")
                Toast.makeText(this,"Failed to create user: ${it.message}",Toast.LENGTH_LONG).show()


            }

    }

    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Successfully uploaded photo: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {

                    Log.d("RegisterActivity","File location: $it")

                    saveUsersToFirebaseDatabase(it.toString())

                }
                    .addOnFailureListener {
                        //do some loggin here
                    }
            }
    }

    private fun saveUsersToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")

        val user = User(uid,username.text.toString(),profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Finally we saved the user to firebase database!")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("RegisterActivity","Failed to set value to database: ${it.message}")

            }
    }

}

