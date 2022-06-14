package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.messages.ChatLogActivity
import com.example.chatapp.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class NewMessageActivity : AppCompatActivity() {

    private lateinit var userRv:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        userRv = findViewById(R.id.newMessageRV)

//        val adapter = GroupAdapter<ViewHolder>()
//
//        userRv.adapter = adapter
        fetchUsers()
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach{
                    Log.d("NewMessageActivity",it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null){
                        adapter.add(UserItem(user))

                    }
                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    //intent.putExtra(USER_KEY,item.user.username)
                    intent.putExtra(USER_KEY,userItem.user)

                    startActivity(intent)

                    finish()
                }
                userRv.adapter = adapter


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

class UserItem(val user: User): Item<ViewHolder>(){
    private lateinit var userName : TextView
    private lateinit var userPic : CircleImageView

    override fun bind(viewHolder: ViewHolder, position: Int) {
        //will be called in our list for each user object later on
        userName = viewHolder.itemView.findViewById(R.id.userName)
        userPic = viewHolder.itemView.findViewById(R.id.UserPicNewMsg)
        userName.text = user.username

        Picasso.get().load(user.profileImageUrl).into(userPic)

    }

    override fun getLayout(): Int {
        return R.layout.user_row
    }
}
