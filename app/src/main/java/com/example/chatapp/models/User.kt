package com.example.chatapp.models

import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.parcelize.Parcelize

@Parcelize
class User(val uid:String, val username:String, val profileImageUrl:String) : Parcelable{
    constructor() : this("","","")
}