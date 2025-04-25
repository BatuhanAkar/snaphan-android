package com.batuscode.photoken.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuscode.photoken.model.User
import com.batuscode.photoken.utils.Auth
import com.batuscode.photoken.viewmodel.UserUtil.TAG
import com.batuscode.photoken.viewmodel.UserUtil.db
import com.batuscode.photoken.viewmodel.UserUtil.ref
import com.batuscode.photoken.viewmodel.UserUtil.uid
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object UserUtil {
    const val TAG = "UserUtil"
    lateinit var user: User
    lateinit var db : FirebaseDatabase
    val uid = Auth.auth.currentUser?.uid
    lateinit var ref : DatabaseReference

}

class AiActivityViewModel : ViewModel() {

    private val _isGenerating =  mutableStateOf<Boolean>(false)
    val isGenerating : MutableState<Boolean> = _isGenerating

    fun updateGenerating(state : Boolean){
        _isGenerating.value = state
    }

    private val _selectedMod = mutableStateOf<String>("Generate")
    val selectedMod : MutableState<String> = _selectedMod

    fun updateSelectedMod(mod : String){
        _selectedMod.value = mod
    }
    private val _imageUrls = mutableStateListOf<String>()
    val imageUrls: List<String> = _imageUrls

    fun addImage(url: String) {
        _imageUrls.add(url)
    }

    fun removeImage(url: String) {
        _imageUrls.remove(url)
    }

    fun clearImages() {
        _imageUrls.clear()
    }


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

}