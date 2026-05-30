package com.batuscode.photoken.utils

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.ui.res.stringResource
import com.batuscode.photoken.R

sealed class OnBoardingPage(
    @DrawableRes
    val image : Int ,
    val title : String ,
    val description : String ,
) {
    object First : OnBoardingPage(
        image = R.drawable.first ,
        title = "Welcome to Photoken" ,
        description = "Let’s get started with a quick tour. We’ll show you how to get the best experience from Photoken and share what we’re all about."
    )

    object Second : OnBoardingPage(
        image = R.drawable.second ,
        title = "Generate" ,
        description = "prompt: generate yellow flowers in the forest.\n" +
                "Although it can overcome complex prompts, the prompts that indicate its purpose most clearly are the ones that are closest to the goal."
    )

    object Third : OnBoardingPage(
        image = R.drawable.third ,
        title = "Mockup" ,
        description = "prompt: generate yellow flowers in the forest.\n" +
                "Although it can overcome complex prompts, the prompts that indicate its purpose most clearly are the ones that are closest to the goal."
    )

    object Fourth : OnBoardingPage(
        image = R.drawable.third ,
        title = "what you should not forget" ,
        description = "According to our principles, you cannot make requests for adult content."
    )
}