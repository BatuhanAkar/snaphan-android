package com.batuscode.photoken.utils

import android.util.Log
import com.batuscode.photoken.AiActivity.Companion.aiActivityViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.HttpsCallableOptions
import com.google.firebase.functions.getHttpsCallable
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FunctionsUtil {
    const val GENKIT_TAG = "FunctionsUtil_GENKIT"
    lateinit var app : FirebaseApp
    lateinit var functions : FirebaseFunctions
    suspend fun generateImage(prompt : String) : String? = withContext(Dispatchers.IO){
        val data = hashMapOf("prompt" to prompt)

        try {
            val result = functions
                .getHttpsCallable("generate")
                .call(data)
                .addOnSuccessListener {
                    aiActivityViewModel.updateGenerating(false)
                }
                .await()

            val imageUrl = (result.data as? Map<*, *>)?.get("url") as? String
            Log.d(GENKIT_TAG, "Image URL: $imageUrl")
            imageUrl // return image url .
        } catch (e: FirebaseFunctionsException) {
            Log.e(GENKIT_TAG, "Failed: ${e.message}", e)
            when(e.code.name){
                "INTERNAL" -> {

                    Log.e(GENKIT_TAG, "Failed handling in internal :: ${e.code.name}")

                    try {
                        val result = functions
                            .getHttpsCallable("generate")
                            .call(data)
                            .addOnSuccessListener {
                                aiActivityViewModel.updateGenerating(false)
                            }
                            .await()

                        val imageUrl = (result.data as? Map<*, *>)?.get("url") as? String
                        Log.d(GENKIT_TAG, "Image URL: $imageUrl")
                        imageUrl // return image url .
                    }catch (e : FirebaseFunctionsException){

                        when(e.code.name){
                            "INTERNAL" -> {
                                null
                            }
                            else -> null
                        }


                    }
                }
                else -> null
            }
        }

    }

    suspend fun checkStat() : Boolean = withContext(Dispatchers.IO){
        val data = hashMapOf("uid" to Auth.auth.uid)

        if (Auth.auth.currentUser != null){
            Log.d(GENKIT_TAG , "user not null")

            val stat = Firebase.functions.getHttpsCallable("stat") {
                limitedUseAppCheckTokens = true
            }

            val result = stat
                .call(data)
                .addOnSuccessListener { result ->
                    Log.d(GENKIT_TAG , "stat result :: ${result.data}")
                }
                .addOnFailureListener { error ->
                    if (error is FirebaseFunctionsException){
                        val code = error.code
                        val message = error.message
                        Log.d(GENKIT_TAG , "error stackTrace :: ${error.stackTrace}")

                        Log.d(GENKIT_TAG , "stat error :: ${code} + ${message}")
                    }
                }.await()

            return@withContext true
        } else {
            Log.d(GENKIT_TAG , "user null")
            return@withContext false
        }

    }

    suspend fun sendMSGtoken(token : String) = withContext(Dispatchers.IO) {
        val uid = Auth.auth.currentUser?.uid
        val data = hashMapOf("token" to token)

        functions
            .getHttpsCallable("saveMSGtoken")
            .call(data)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful){
                    return@addOnCompleteListener
                }
                Log.d(GENKIT_TAG , "saved msg token")
            }

    }
}