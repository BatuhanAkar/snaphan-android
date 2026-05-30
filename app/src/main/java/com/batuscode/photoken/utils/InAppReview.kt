package com.batuscode.photoken.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.review.testing.FakeReviewManager

object InAppReview {
    private const val TAG = "InAppReview"
    fun requestReview(context : Context){
        val manager = FakeReviewManager(context)

        val request = manager.requestReviewFlow()

        request
            .addOnCompleteListener { task ->
            if (task.isSuccessful){
                val activity = (context as? Activity)
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity!!, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            } else {
                @ReviewErrorCode val reviewErrorCode = (task.getException() as ReviewException).errorCode
                Log.d(TAG , "review_error_code :: ${reviewErrorCode}")
            }
        }
    }


}