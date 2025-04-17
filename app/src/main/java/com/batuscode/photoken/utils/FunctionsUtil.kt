package com.batuscode.photoken.utils

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
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
                .await()

            val imageUrl = (result.data as? Map<*, *>)?.get("url") as? String
            Log.d(GENKIT_TAG, "Image URL: $imageUrl")
            imageUrl // return image url .
        } catch (e: Exception) {
            Log.e(GENKIT_TAG, "Failed: ${e.message}", e)
            null
        }

    }
}