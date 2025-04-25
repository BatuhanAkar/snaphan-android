package com.batuscode.photoken.integrity

import android.content.Context
import android.util.Log
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityException
import com.google.android.play.core.integrity.StandardIntegrityManager
import com.google.android.play.core.integrity.model.StandardIntegrityErrorCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object IntegrityHelper {
    const val TAG = "igyHelper"
    lateinit var integrityTokenProvider: StandardIntegrityManager.StandardIntegrityTokenProvider

    suspend fun prepareIntegrityTokenProvider(applicationContext: Context) = withContext(Dispatchers.IO){
        val standardIntegrityManager = IntegrityManagerFactory.createStandard(applicationContext)
        val pn = 510851154449

        standardIntegrityManager.prepareIntegrityToken(
            StandardIntegrityManager.PrepareIntegrityTokenRequest.builder()
                .setCloudProjectNumber(pn)
                .build()
        ).addOnSuccessListener { tokenProvider ->
            integrityTokenProvider = tokenProvider
        }
            .addOnFailureListener { exception -> handlePrepareError(exception , applicationContext) }
    }

    suspend fun makeStandardIntegrityTokenRequest(){
        val requestHash = ""

        val integrityTokenResponse = integrityTokenProvider.request(
            StandardIntegrityManager.StandardIntegrityTokenRequest.builder()
                .setRequestHash(requestHash)
                .build()
        )

        integrityTokenResponse
            .addOnSuccessListener { response -> boomToken(response.token()) }
            .addOnFailureListener { exception -> handleTokenRequestError(exception) }
    }

    fun boomToken(token : String){

    }

    fun handlePrepareError(exception: Exception , applicationContext: Context){
        if (exception is StandardIntegrityException){
            Log.d(TAG , "integrity exception :: ${exception.message}")

            when(exception.errorCode){
                StandardIntegrityErrorCode.API_NOT_AVAILABLE -> {

                }
                StandardIntegrityErrorCode.APP_NOT_INSTALLED -> {
                    // exit ..
                }
                StandardIntegrityErrorCode.APP_UID_MISMATCH -> {
                    // exit ..
                }
                StandardIntegrityErrorCode.CANNOT_BIND_TO_SERVICE -> {

                }
                StandardIntegrityErrorCode.CLIENT_TRANSIENT_ERROR -> {

                }
                StandardIntegrityErrorCode.CLOUD_PROJECT_NUMBER_IS_INVALID -> {

                }
                StandardIntegrityErrorCode.GOOGLE_SERVER_UNAVAILABLE -> {

                }
                StandardIntegrityErrorCode.INTEGRITY_TOKEN_PROVIDER_INVALID -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        prepareIntegrityTokenProvider(applicationContext)
                    }
                }
                StandardIntegrityErrorCode.INTERNAL_ERROR -> {

                }
                StandardIntegrityErrorCode.NETWORK_ERROR -> {
                    // show check connection dialog ...
                }
                StandardIntegrityErrorCode.NO_ERROR -> {

                }
                StandardIntegrityErrorCode.PLAY_STORE_NOT_FOUND -> {

                }
                StandardIntegrityErrorCode.PLAY_SERVICES_NOT_FOUND -> {
                  //  Play Services is not available or version is too old.
                  //  Ask the user to Install or Update Play Services.

                }
                StandardIntegrityErrorCode.PLAY_SERVICES_VERSION_OUTDATED -> {
                   // Play Services needs to be updated.

                  //  Ask the user to update Google Play Services.
                }
                StandardIntegrityErrorCode.PLAY_STORE_VERSION_OUTDATED -> {
                   // The Play Store needs to be updated.

                   // Ask the user to update the Google Play Store.
                }
                StandardIntegrityErrorCode.REQUEST_HASH_TOO_LONG -> {
                   // The provided request hash is too long. The request hash length must be less than 500 bytes.

                  //  Retry with a shorter request hash.
                }
                StandardIntegrityErrorCode.TOO_MANY_REQUESTS -> {
                  //  The calling app is making too many requests to the API and hence is throttled.

                  //  Retry with an exponential backoff.
                }
            }
        }
    }

    fun handleTokenRequestError(exception: Exception){

    }

}