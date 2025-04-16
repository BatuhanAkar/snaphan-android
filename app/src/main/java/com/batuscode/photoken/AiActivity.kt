package com.batuscode.photoken

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.batuscode.photoken.ui.theme.PhotokenTheme
import com.batuscode.photoken.utils.Auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AiActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PhotokenTheme(darkTheme = true) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize() ,
                    topBar = {
                        TopAppBar(
                            title = {} ,
                            navigationIcon = {
                                IconButton(
                                    onClick = {

                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.shopping_bag_speed_24px) ,
                                        contentDescription = stringResource(R.string.shop_icon)
                                    )
                                }
                            } ,
                            actions = {

                            }
                        )
                    }
                ) { innerPadding ->

                    AiChatContainer(
                        modifier = Modifier
                            .padding(innerPadding)
                    )

                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AiChatContainer(modifier: Modifier = Modifier){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PhotokenTheme(darkTheme = true) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize() ,
            topBar = {
                TopAppBar(
                    title = {} ,
                    navigationIcon = {
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.shopping_bag_speed_24px) ,
                                contentDescription = stringResource(R.string.shop_icon)
                            )
                        }
                    } ,
                    actions = {
                        
                    }
                )
            }
        ) { innerPadding ->
            AiChatContainer(modifier = Modifier.padding(innerPadding))
        }
    }
}