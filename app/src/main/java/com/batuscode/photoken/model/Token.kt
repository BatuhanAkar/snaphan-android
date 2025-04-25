package com.batuscode.photoken.model

import androidx.annotation.DrawableRes
import com.android.billingclient.api.ProductDetails

data class Token(
    @DrawableRes
    val image : Int ,
    val name : String ,
    val title : String ,
    val description : String ,
    val price : String ,
    val productDetails: ProductDetails ,
    val type : String ,
)
