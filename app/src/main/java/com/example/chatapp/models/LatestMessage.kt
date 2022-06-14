package com.example.chatapp.models

import android.widget.TextView
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class LatestMessage(val chatMessage: ChatMessage): Item<ViewHolder>(){
    private lateinit var userImg: CircleImageView
    private lateinit var userName: TextView
    private lateinit var msg: TextView

    var chatPartnerUser: User? =null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        userImg = viewHolder.itemView.findViewById(R.id.imageUser)
        userName = viewHolder.itemView.findViewById(R.id.user)
        msg = viewHolder.itemView.findViewById(R.id.msg)

        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
        }
        else{
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                userName.text = chatPartnerUser?.username

                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(userImg)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.latestmsg_row
    }
}