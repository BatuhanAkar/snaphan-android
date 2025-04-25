package com.batuscode.photoken

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.batuscode.photoken.data.PrefRepository
import com.batuscode.photoken.ui.theme.PhotokenTheme
import com.batuscode.photoken.utils.OnBoardingPage
import com.batuscode.photoken.viewmodel.WelcomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val welcomeViewModel: WelcomeViewModel = hiltViewModel()
            val pagerState = rememberPagerState(pageCount = {4})

            val pages = welcomeViewModel.pages

            PhotokenTheme(darkTheme = true) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->


                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        HorizontalPager(state = pagerState) { page ->


                            when(page){
                                0 -> {
                                    EntryPagerView(
                                        modifier = Modifier
                                            .padding(innerPadding) ,
                                        pages[page]
                                    )
                                }
                                3 -> {
                                    LastPagerView(
                                        modifier = Modifier
                                            .padding(innerPadding) ,
                                        pages[page] ,
                                        welcomeViewModel
                                    )
                                }
                                else -> {
                                    PagerView(
                                        modifier = Modifier
                                            .padding(innerPadding) ,
                                        pages[page]
                                    )
                                }
                            }
                        }

                        Row(
                            Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(pagerState.pageCount) { iteration ->
                                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun QuickTourButton(text : String){
    Button(
        onClick = {

        } ,
        modifier = Modifier
            .fillMaxWidth(0.5f)
    ) {

        Text(
            text = text
        )
    }
}

@Composable
fun LastPagerView(modifier : Modifier = Modifier , onBoardingPage: OnBoardingPage , welcomeViewModel: WelcomeViewModel){
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize() ,
        horizontalAlignment = Alignment.CenterHorizontally ,
        verticalArrangement = Arrangement.Top
    ) {

        Image(
            painter = painterResource(onBoardingPage.image) ,
            contentDescription = "" ,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = modifier,
            text = onBoardingPage.title ,
            style = MaterialTheme.typography.titleLarge ,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = modifier
                .padding(horizontal = 16.dp),
            text = onBoardingPage.description ,
            style = MaterialTheme.typography.bodyLarge ,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    welcomeViewModel.saveOnBoardingState(true)
                    withContext(Dispatchers.Main) {
                        (context as? Activity)?.finish()
                    }
                }
            } ,
            modifier = Modifier
                .fillMaxWidth(0.5f)
        ) {
            Text(
                text = stringResource(R.string.imdone)
            )
        }


    }
}


@Composable
fun EntryPagerView(modifier : Modifier = Modifier , onBoardingPage: OnBoardingPage){
    val infiniteTransition = rememberInfiniteTransition(label = "Swipe Animation")

    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -100f, // sola doğru 100dp kayacak
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
    )


    Column(
        modifier = modifier
            .fillMaxSize() ,
        horizontalAlignment = Alignment.CenterHorizontally ,
        verticalArrangement = Arrangement.Top
    ) {

        Image(
            painter = painterResource(onBoardingPage.image) ,
            contentDescription = "" ,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = modifier,
            text = onBoardingPage.title ,
            style = MaterialTheme.typography.titleLarge ,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = modifier
                .padding(horizontal = 16.dp),
            text = onBoardingPage.description ,
            style = MaterialTheme.typography.bodyLarge ,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = stringResource(R.string.explore_tour))
        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(R.drawable.swipe_left_alt_24px) ,
            contentDescription = "" ,
            modifier = Modifier
                .size(48.dp)
                .offset(x = offsetX.dp)
        )

    }
}

@Composable
fun PagerView(modifier : Modifier = Modifier , onBoardingPage: OnBoardingPage){
    Column(
        modifier = modifier
            .fillMaxSize() ,
        horizontalAlignment = Alignment.CenterHorizontally ,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            modifier = Modifier,
            text = onBoardingPage.title ,
            style = MaterialTheme.typography.titleLarge ,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(370.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                ) ,
        ) {
            Image(
                painter = painterResource(onBoardingPage.image) ,
                contentDescription = "" ,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
            )


        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            text = onBoardingPage.description ,
            style = MaterialTheme.typography.bodyLarge ,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {

    PhotokenTheme(darkTheme = true) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val pagerState = rememberPagerState(pageCount = {4})

            val p = listOf(OnBoardingPage.First, OnBoardingPage.Second , OnBoardingPage.Third ,
                OnBoardingPage.Fourth)

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                HorizontalPager(state = pagerState) { page ->


                    when(page){
                        0 -> {
                            EntryPagerView(
                                modifier = Modifier
                                    .padding(innerPadding) ,
                                p[page]
                            )
                        }
                        3 -> {
                            /*LastPagerView(
                                modifier = Modifier
                                    .padding(innerPadding) ,
                                p[page]
                            )*/
                        }
                        else -> {
                            PagerView(
                                modifier = Modifier
                                    .padding(innerPadding) ,
                                p[page]
                            )
                        }
                    }


                }

                Row(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(16.dp)
                        )
                    }
                }
            }
        }
    }
}