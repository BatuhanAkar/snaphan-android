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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.batuscode.photoken.SignInActivty.Companion.context
import com.batuscode.photoken.integrity.IntegrityHelper
import com.batuscode.photoken.model.User
import com.batuscode.photoken.ui.theme.PhotokenTheme
import com.batuscode.photoken.utils.Auth
import com.batuscode.photoken.utils.FunctionsUtil
import com.batuscode.photoken.viewmodel.UserUtil
import com.batuscode.photoken.viewmodel.UserUtil.db
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
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
            val intent = Intent(context , AiActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
            finish()
        }

    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            IntegrityHelper.prepareIntegrityTokenProvider(this@SignInActivty)
        }

        FunctionsUtil.app = FirebaseApp.initializeApp(this)!!
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )
        // initialize context .
        context = this

        // initialize firebase auth variable .
        Auth.auth = Firebase.auth
        // init functions .
        FunctionsUtil.functions = Firebase.functions(app = FunctionsUtil.app)

        // init fdb .
        UserUtil.db = Firebase.database

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
        Column(
            modifier = Modifier ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.first) ,
                contentDescription = "" ,
                modifier = Modifier
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.app_name) ,
                style = MaterialTheme.typography.titleLarge ,
                fontSize = 32.sp
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