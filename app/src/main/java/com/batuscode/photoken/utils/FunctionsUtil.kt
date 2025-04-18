package com.batuscode.photoken.utils

import android.util.Log
import com.batuscode.photoken.AiActivity.Companion.aiActivityViewModel
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FunctionsUtil {
    const val GENKIT_TAG = "FunctionsUtil_GENKIT"
    lateinit var functions : FirebaseFunctions
    suspend fun generateImage(prompt : String) : String? = withContext(Dispatchers.IO){
        val data = hashMapOf("prompt" to prompt)

        try {
            val result = FirebaseFunctions.getInstance()
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
                        val result = FirebaseFunctions.getInstance()
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
}