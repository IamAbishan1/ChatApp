package com.example.chatapp.messages

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.NewMessageActivity
import com.example.chatapp.R
import com.example.chatapp.models.ChatMessage
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class ChatLogActivity : AppCompatActivity() {
    private lateinit var userMsgRv:RecyclerView
    private lateinit var sendBtn:Button
    private lateinit var userMsg:EditText

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        sendBtn = findViewById(R.id.sendBtn)
        userMsgRv = findViewById(R.id.userMsg)

       // val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser!!.username

        userMsgRv.adapter = adapter

        //setupDummyData()
        listenForMessages()

        sendBtn.setOnClickListener {
            Log.d(TAG, "Attempt to send message........")
            performSendMessage()
        }






    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null){
                    Log.d(TAG, chatMessage!!.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))

                    }
                    else{
                       // val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }

                }
                userMsgRv.scrollToPosition(adapter.itemCount-1)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    private fun performSendMessage() {
        //Send message to firebase database

        val msg = userMsg.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        val toId = user!!.uid

        if (fromId == null) null
       // val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference  = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()



        val chatMessage = ChatMessage(reference.key!!, msg,fromId!!,toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved our chat message: ${reference.key}")
                userMsg.text.clear()
                userMsgRv.scrollToPosition(adapter.itemCount-1)
            }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }

}

class ChatFromItem(val text: String,val user: User): Item<ViewHolder>(){

    private lateinit var msgFrom:TextView
    private lateinit var imageFrom:CircleImageView


    override fun bind(viewHolder: ViewHolder, position: Int) {
        imageFrom = viewHolder.itemView.findViewById(R.id.imgFrom)
        msgFrom = viewHolder.itemView.findViewById(R.id.msgFrom)

        msgFrom.text = text
        val uri = user.profileImageUrl
        Picasso.get().load(uri).into(imageFrom)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }
}

class ChatToItem(val text: String, val user: User): Item<ViewHolder>(){

    private lateinit var msgTo:TextView
    private lateinit var imageTo:CircleImageView

    override fun bind(viewHolder: ViewHolder, position: Int) {
        msgTo = viewHolder.itemView.findViewById(R.id.msgTo)
        imageTo = viewHolder.itemView.findViewById(R.id.imageTo)

        msgTo.text = text
        val url = user.profileImageUrl
        Picasso.get().load(url).into(imageTo)

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}

