package com.batuscode.photoken.store

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object StoreHelper {
    const val TAG = "StoreHelper"
    lateinit var billingClient: BillingClient
    val purchase_listener = PurchasesUpdatedListener { billingResult, purchases ->
        when(billingResult.responseCode){
            -1 -> {
                // service disconnected .
                Log.d(TAG, "service disconnected")
            }
            0 -> {
                // ok .
                Log.d(TAG, "ok")


            }
            1-> {
                // user canceled .
                Log.d(TAG, "user canceled")

            }
            2 -> {
                // service unavailable .
                Log.d(TAG, "service unavailable")

            }
            3 -> {
                // billing unavailable .
                Log.d(TAG, "billing unavailable")

            }
            4 -> {
                // item unavailable .

                Log.d(TAG, "item unavailable")
            }
            5 -> {
                // developer error .

                Log.d(TAG, "developer error")
            }
            6 -> {
                // error .

                Log.d(TAG, "error")
            }
            7 -> {
                // item already owned .

            }
            8 -> {
                // item not owned .

            }
            12 -> {
                // network error .
            }



        }


    }

}