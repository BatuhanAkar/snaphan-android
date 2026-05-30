package com.batuscode.photoken.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batuscode.photoken.AiActivity
import com.batuscode.photoken.AiActivity.Companion.aiActivityViewModel
import com.batuscode.photoken.WelcomeActivity
import com.batuscode.photoken.data.PrefRepository
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
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

object UserUtil {
    const val TAG = "UserUtil"
    lateinit var user: User
    lateinit var db : FirebaseDatabase
    val uid = Auth.auth.currentUser?.uid
    lateinit var ref : DatabaseReference

}
@HiltViewModel
class AiActivityViewModel@Inject constructor(
    private val repository: PrefRepository ,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val TAG = "AiActivtyViewModel"
    val _isGrantedNotificationPermission = mutableStateOf<Boolean>(false)
    suspend fun saveNotificationState(isGranted : Boolean) = withContext(Dispatchers.IO){
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveNotificationPermissionState(isGranted)
        }
    }
    init {
        viewModelScope.launch {
            repository.readOnBoardingState().collect { completed ->
                if (!completed) {
                    val intent = Intent(context , WelcomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                }
            }
        }
        viewModelScope.launch {
            repository.readNotificationState().collect { isGranted ->
                Log.d("isNeedAskNotificationPermission" , isGranted.toString())
                _isGrantedNotificationPermission.value = isGranted
            }
        }
        viewModelScope.launch {
            // read msg token status .
            repository.readTakedMSGToken().collect { isTaked ->
                Log.d(TAG , "isTaked " + isTaked)
                if (!isTaked){
                    Log.d(TAG , "msg token not taked")
                    requestMSGtoken()
                }
            }
        }

    }

    private fun requestMSGtoken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful){
                Log.w(TAG , "messaging token failed " , task.exception)
                return@addOnCompleteListener
            }
            Log.d(TAG , "msg token is taked")
            CoroutineScope(Dispatchers.IO).launch {
                repository.saveTakedMessageState(true)

            }
        }
    }


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






}