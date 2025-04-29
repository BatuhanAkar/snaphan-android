package com.batuscode.photoken.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.batuscode.photoken.model.User
import com.batuscode.photoken.utils.Auth
import com.batuscode.photoken.viewmodel.UserUtil.db
import com.batuscode.photoken.viewmodel.UserUtil.ref
import com.batuscode.photoken.viewmodel.UserUtil.uid
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInActivityViewModel @Inject constructor() : ViewModel(){
    private val TAG = "SignInActivityViewModel"

    val user = mutableStateOf<User?>(null)

    val childEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.d(TAG , "onDataChange")
            val muser = snapshot.getValue(User::class.java)
            if (muser != null){
                user.value = muser
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }
    suspend fun register() = withContext(Dispatchers.IO){
        Log.d(TAG , "user uid ... ${uid}")

        ref = db.getReference("uti").child(uid!!)
        ref.addValueEventListener(childEventListener)
    }

    suspend fun unregister(){
        ref.removeEventListener(childEventListener)
    }

    suspend fun checkClaims() = withContext(Dispatchers.IO) {
        Auth.auth.currentUser?.getIdToken(false)?.addOnSuccessListener { task ->
            val isBlacklabel = task.claims.get("blacklabel")
            Log.d(TAG , "user is black_label subs :: ${isBlacklabel}")
        }
    }
}