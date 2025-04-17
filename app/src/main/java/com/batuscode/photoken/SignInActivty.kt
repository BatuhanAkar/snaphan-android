package com.batuscode.photoken

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.batuscode.photoken.SignInActivty.Companion.context
import com.batuscode.photoken.ui.theme.PhotokenTheme
import com.batuscode.photoken.utils.Auth
import com.batuscode.photoken.utils.FunctionsUtil
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.initialize
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInActivty : ComponentActivity() {

    companion object {
        lateinit var context: Context
    }

    override fun onStart() {
        super.onStart()

        val currentUser = Auth.auth
        if (currentUser.currentUser != null){
            val intent = Intent(context , AiActivity::class.java)
            context.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.initialize(this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )
        // initialize context .
        context = this

        // initialize firebase auth variable .
        Auth.auth = Firebase.auth
        FunctionsUtil.functions = Firebase.functions

        enableEdgeToEdge()
        setContent {
            PhotokenTheme(darkTheme = true) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                )
                { innerPadding ->
                    SignInScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SignInScreen(modifier: Modifier = Modifier){
    Column(
        modifier
            .fillMaxSize() ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,

    ) {

        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
        ) {
            Text(
                text = stringResource(R.string.app_name) ,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = {
                CoroutineScope(Dispatchers.Default).launch {
                    Auth.firebaseAuthWithGoogle(context = context)
                }
            } ,
            modifier = Modifier
                .absolutePadding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically ,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Image(
                    painter = painterResource(R.drawable.android_dark_rd_na) ,
                    contentDescription = stringResource(R.string.gicon)
                )
                Text(
                    text = stringResource(R.string.continuewithgoogle) ,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    PhotokenTheme(darkTheme = true) {
        SignInScreen()
    }
}