package com.batuscode.photoken

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.batuscode.photoken.AiActivity.Companion.aiActivityViewModel
import com.batuscode.photoken.ui.theme.PhotokenTheme
import com.batuscode.photoken.utils.Auth
import com.batuscode.photoken.utils.DrawerSide
import com.batuscode.photoken.utils.FunctionsUtil
import com.batuscode.photoken.utils.FunctionsUtil.GENKIT_TAG
import com.batuscode.photoken.viewmodel.AiActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.ComponentRegistry
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.batuscode.photoken.AiActivity.Companion.snackbarHostState
import com.batuscode.photoken.data.PrefRepository
import com.batuscode.photoken.utils.CrudUtils
import com.batuscode.photoken.utils.CrudUtils.crudTAG
import com.batuscode.photoken.utils.InAppReview
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AiActivity : ComponentActivity() {
    companion object {
        lateinit var aiActivityViewModel: AiActivityViewModel
        val snackbarHostState = SnackbarHostState()
        lateinit var repository : PrefRepository
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted : Boolean ->
        if (isGranted){

        } else {

        }
    }
    @Composable
    private fun AskNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this , Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                //can post notification
            } else if (notificationPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)){
                NotificationPermissionDialog(
                    onDismiss = {}
                )
            }
        }
    }

    @Composable
    fun notificationPermissionRationale(permission : String) : Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            return if (isGranted == PackageManager.PERMISSION_GRANTED) false else true
        } else {
            return true
        }

    }

    @Composable
    fun NotificationPermissionDialog(onDismiss: () -> Unit){
        Dialog(
            onDismissRequest = { onDismiss } ,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true , usePlatformDefaultWidth = true)
        ) {
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight() ,
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth() ,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally ,
                ) {

                    Image(
                        painter = painterResource(R.drawable.token_24dp_ffd700_fill0_wght400_grad0_opsz24) ,
                        contentDescription = "" ,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        text = stringResource(R.string.notificationpermissionquestiontext , stringResource(R.string.app_name)),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center ,
                    )

                    Text(
                        text = stringResource(R.string.in_app_notifications),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth() ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End ,
                    ) {
                        TextButton(
                            onClick = { onDismiss }
                        ) {
                            Text(
                                text = stringResource(R.string.nothanks) ,
                            )
                        }

                        Button(
                            onClick = {

                            }
                        ) {
                            Text(
                                text = stringResource(R.string.ok) ,
                                textAlign = TextAlign.Center ,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun NotificationPermissionDialogPreview(){
        PhotokenTheme(darkTheme = true) {
            Dialog(
                onDismissRequest = {  } ,
                properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true , usePlatformDefaultWidth = true)
            ) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight() ,
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth() ,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally ,
                    ) {

                        Image(
                            painter = painterResource(R.drawable.defult_notification_icon) ,
                            contentDescription = "" ,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )

                        Text(
                            text = stringResource(R.string.notificationpermissionquestiontext , stringResource(R.string.app_name)),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center ,
                        )

                        Text(
                            text = stringResource(R.string.in_app_notifications),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth() ,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End ,
                        ) {
                            TextButton(
                                onClick = {  }
                            ) {
                                Text(
                                    text = stringResource(R.string.nothanks) ,
                                )
                            }

                            Button(
                                onClick = {}
                            ) {
                                Text(
                                    text = stringResource(R.string.ok) ,
                                    textAlign = TextAlign.Center ,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            repository.readOnBoardingState().collect { completed ->
                if (!completed) {
                    val intent = Intent(this@AiActivity , WelcomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch {
            aiActivityViewModel.unregister()
        }
    }
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        repository = PrefRepository(this)
        aiActivityViewModel = ViewModelProvider(this).get(AiActivityViewModel::class.java)

        lifecycleScope.launch {
            aiActivityViewModel.register()
        }
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            var isDrawerOpen = remember { mutableStateOf(false) }
            var isSendedPhoto = remember { mutableStateOf(false) }
            var prompt = remember { mutableStateOf("") }
            val selectedMod by remember { derivedStateOf { aiActivityViewModel.selectedMod } }
            val token by remember { derivedStateOf { aiActivityViewModel.user.value?.token } }
            PhotokenTheme(darkTheme = true) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    topBar = {
                        TopAppBar(
                            title = {} ,
                            modifier = Modifier
                                .nestedScroll(rememberNestedScrollInteropConnection()),
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(context , StoreActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.shopping_bag_speed_24px) ,
                                        contentDescription = stringResource(R.string.shop_icon)
                                    )
                                }
                            } ,
                            actions = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth() ,
                                    verticalAlignment = Alignment.CenterVertically ,
                                    horizontalArrangement = Arrangement.End
                                ) {


                                    Box(
                                        modifier = Modifier
                                            .background(Color.Transparent) // Butonun arkasına hafif şeffaf bir arka plan
                                            .padding(8.dp) ,
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AiChoiceSegmentedButton(
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }

                                    Box(

                                    ) {
                                        Row {
                                            Text(
                                                text = if (token != null) token.toString() else "9"
                                            )
                                            Image(
                                                painter = painterResource(R.drawable.token_24dp_ffd700_fill0_wght400_grad0_opsz24) ,
                                                contentDescription = stringResource(R.string.token_icon) ,

                                                )
                                        }

                                    }

                                    Box(
                                        modifier = Modifier
                                            .clickable(
                                                enabled = true ,
                                                onClick = {
                                                    isDrawerOpen.value = isDrawerOpen.value.not()
                                                }
                                            )
                                    ) {
                                        AsyncImage(
                                            model = R.drawable.ic_launcher_foreground ,
                                            contentDescription = stringResource(R.string.profile_photo) ,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }


                                }
                            } ,
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent.copy(0.0f)
                            )
                        )
                    } ,
                    bottomBar = {
                        BottomAppBar(
                            modifier = Modifier

                                .padding(vertical = 16.dp) ,
                            containerColor = Color.Transparent ,
                            contentColor = Color.Transparent
                        ) {

                            when(selectedMod.value){
                                "Generate" ->
                                    {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 8.dp, vertical = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize() ,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            BasicTextField(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .requiredHeight(68.dp),
                                                value = prompt.value ,
                                                onValueChange = { newPrompt ->
                                                    prompt.value = newPrompt
                                                } ,
                                                textStyle = MaterialTheme.typography.bodyMedium,
                                                decorationBox = { innerTextField ->
                                                    Box(
                                                        modifier = Modifier
                                                            .shadow(
                                                                elevation = 8.dp,
                                                                shape = RoundedCornerShape(32.dp)
                                                            )
                                                            .background(
                                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                                shape = RoundedCornerShape(8.dp)
                                                            )
                                                            .padding(
                                                                horizontal = 32.dp,
                                                                vertical = 16.dp
                                                            )

                                                    ) {
                                                        if (prompt.value.isEmpty()){
                                                            Text(
                                                                text = stringResource(R.string.message_placeholder) ,
                                                                style = MaterialTheme.typography.bodySmall
                                                            )
                                                        }
                                                        innerTextField()
                                                    }
                                                }
                                            )

                                            IconButton(
                                                onClick = {
                                                    CoroutineScope(Dispatchers.IO).launch {
                                                       // FunctionsUtil.checkStat()
                                                        aiActivityViewModel.updateGenerating(true)
                                                        val mprompt = prompt.value
                                                        prompt.value = ""
                                                        val imageUrl = FunctionsUtil.generateImage(mprompt)
                                                        aiActivityViewModel.addImage(imageUrl!!)
                                                    }
                                                } ,
                                                colors = IconButtonDefaults.iconButtonColors(
                                                    containerColor = Color.Transparent
                                                ) ,
                                                modifier = Modifier
                                                    .size(68.dp)
                                                    .clip(CircleShape) ,
                                                enabled = if (prompt.value.isEmpty()) false else true

                                            ) {
                                                Image(
                                                    painter = painterResource(R.drawable.rocket_launch_24px) ,
                                                    contentDescription = "" ,
                                                    contentScale = ContentScale.Crop ,
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                "Mockup" ->
                                    {

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 8.dp, vertical = 8.dp)
                                    ) {

                                        // foto seçilmemişse
                                        if (!isSendedPhoto.value){
                                            IconButton(
                                                onClick = {
                                                    isSendedPhoto.value = isSendedPhoto.value.not()
                                                } ,
                                                colors = IconButtonDefaults.iconButtonColors(
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                                ) ,
                                                modifier = Modifier
                                                    .size(68.dp)
                                            ) {
                                                Image(
                                                    painter = painterResource(R.drawable.image_arrow_up_24px) ,
                                                    contentDescription = "" ,
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                )
                                            }
                                        } else {

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize() ,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                BasicTextField(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .requiredHeight(68.dp),
                                                    value = prompt.value ,
                                                    onValueChange = { newPrompt ->
                                                        prompt.value = newPrompt
                                                    } ,
                                                    textStyle = MaterialTheme.typography.bodyMedium,
                                                    decorationBox = { innerTextField ->
                                                        Box(
                                                            modifier = Modifier
                                                                .shadow(
                                                                    elevation = 8.dp,
                                                                    shape = RoundedCornerShape(32.dp)
                                                                )
                                                                .background(
                                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                                    shape = RoundedCornerShape(8.dp)
                                                                )
                                                                .padding(
                                                                    horizontal = 32.dp,
                                                                    vertical = 16.dp
                                                                )

                                                        ) {
                                                            if (prompt.value.isEmpty()){
                                                                Text(
                                                                    text = stringResource(R.string.message_placeholder) ,
                                                                    style = MaterialTheme.typography.bodySmall
                                                                )
                                                            }
                                                            innerTextField()
                                                        }

                                                    }
                                                )

                                                IconButton(
                                                    onClick = {
                                                    } ,
                                                    colors = IconButtonDefaults.iconButtonColors(
                                                        containerColor = Color.Transparent
                                                    ) ,
                                                    modifier = Modifier
                                                        .size(68.dp)
                                                        .clip(CircleShape)

                                                ) {
                                                    Image(
                                                        painter = painterResource(R.drawable.rocket_launch_24px) ,
                                                        contentDescription = "" ,
                                                        contentScale = ContentScale.Crop ,
                                                        modifier = Modifier
                                                            .size(48.dp)
                                                    )
                                                }
                                            }
                                        }




                                    }
                                }
                                else -> {}
                            }
                        }
                    } ,
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }
                ) { innerPadding ->


                    AiChatContainer(
                        modifier = Modifier
                            .padding(innerPadding) ,
                        innerPadding
                    )

                    if (isDrawerOpen.value){
                        CustomSideDrawerOverlay(
                            isDrawerOpen = isDrawerOpen.value,
                            onDismiss = {
                                isDrawerOpen.value = isDrawerOpen.value.not()
                            },
                            drawerContent = {
                                CustomSideDrawerContent()
                            },
                            // No need to pass content here since it's handled separately
                            drawerWidth = 300.dp,  // Customize the drawer width
                            showMask = true,  // Optional: if you want to show the mask when drawer is open
                            drawerSide = DrawerSide.RIGHT,  // Drawer from left, or RIGHT
                            animationDuration = 300  // Animation duration for opening/closing the drawer
                        )
                    }
                    AskNotificationPermission()

                }

                }
            }
        }
    }


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AiChatContainer(modifier: Modifier = Modifier,innerPadding: PaddingValues){
    val dummylist = listOf(
        "data:image/png;base64,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","data:image/png;base64,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","data:image/png;base64,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")

    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
    val imageUrls by remember { derivedStateOf { aiActivityViewModel.imageUrls } }
    val isGenerating by remember { derivedStateOf { aiActivityViewModel.isGenerating }}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            contentPadding = innerPadding,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(imageUrls) { item ->


                val base64 = item.removePrefix("data:image/png;base64,")
                Log.d(GENKIT_TAG, "base64 : ${base64}")

                val imageBytes = remember(base64) {
                    Base64.decode(base64, Base64.DEFAULT)
                }
                Log.d(GENKIT_TAG, "${imageBytes.size} bytes")

                val request = ImageRequest.Builder(context)
                    .data(imageBytes)
                    .crossfade(true)
                    .build()

                var mbitmap = remember { mutableStateOf<Bitmap?>(null) }

                Box(
                    modifier = Modifier
                        .size(370.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        ) , 
                ) {
                    SubcomposeAsyncImage(
                        model = request ,
                        imageLoader = imageLoader,
                        contentDescription = "" ,
                        contentScale = ContentScale.Fit ,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp)) ,
                        loading = {
                            GeneratingImagePlaceHolder()
                        },
                        success = { state ->

                            val bitmap = state.result.drawable.toBitmap()

                            mbitmap.value=bitmap

                            Image(
                                painter = state.painter ,
                                contentDescription = "" ,
                                modifier = Modifier
                                    .fillMaxSize()
                            )

                        }
                    )


                }


                if (isGenerating.value){
                    GeneratingImagePlaceHolder()
                }
                Spacer(modifier = Modifier.size(8.dp))

                if (mbitmap.value != null){

                    ImageToolBar(
                        modifier = Modifier , mbitmap.value!!
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))

            }
        }

    }
}


@Composable
fun GeneratingImagePlaceHolder(){
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    )

    val infiniteTransition = rememberInfiniteTransition()
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = Modifier
            .size(370.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = brush,
                        blendMode = BlendMode.Screen
                    )
                }
            }
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.dp
        )
    }
}

@Composable
fun ImageToolBar(modifier : Modifier = Modifier , bitmap: Bitmap){
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth() ,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
            } ,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent
            ) ,
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape) ,

            ) {
            Image(
                painter = painterResource(R.drawable.share_24px) ,
                contentDescription = "" ,
                contentScale = ContentScale.Crop ,
                modifier = Modifier
                    .size(48.dp)
            )
        }

        IconButton(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = CrudUtils.saveImageToGallery(context , bitmap , "yellowFlowers")

                    if (result){
                        CoroutineScope(Dispatchers.IO).launch {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.image_saved) ,
                                duration = SnackbarDuration.Short
                            )
                        }
                    } else {

                        Log.e(crudTAG, "save image result false")
                    }
                }
            } ,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent
            ) ,
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape),

            ) {
            Image(
                painter = painterResource(R.drawable.download_24px) ,
                contentDescription = "" ,
                contentScale = ContentScale.Crop ,
                modifier = Modifier
                    .size(48.dp)
            )
        }
    }

}

@Composable
fun AiChoiceSegmentedButton(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf(stringResource(R.string.ai_mode_generate),"Mockup")
    var isActive by remember { mutableStateOf(false) }
    val animatedColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(durationMillis = 500),
        label = "containerColor"
    )
    val animatedLabelColor by animateColorAsState(
        targetValue = if (isActive) Color.Transparent else Color.Gray,
        animationSpec = tween(durationMillis = 500),
        label = "containerColor"
    )

    SingleChoiceSegmentedButtonRow(
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size ,
                    baseShape = SegmentedButtonDefaults.baseShape.copy(CornerSize(0))
                ),
                onClick = {
                    selectedIndex = index
                    isActive = index == selectedIndex

                    when(index){
                        0 -> {
                            aiActivityViewModel.updateSelectedMod("Generate")
                        }

                        1 -> {
                            aiActivityViewModel.updateSelectedMod("Mockup")
                        }
                    }
                },
                selected = index == selectedIndex ,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Color.Transparent ,
                    inactiveContainerColor = Color.Transparent
                ),
                border = SegmentedButtonDefaults.borderStroke(color = Color.Transparent, width = 0.dp) ,
                icon = {},
                label = {
                    Text(
                        text = label ,
                        color = animatedColor
                    )
                } ,
            )
        }
    }
}
@Composable
fun CustomSideDrawerOverlay(
    isDrawerOpen: Boolean,
    onDismiss: () -> Unit,
    drawerContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    drawerWidth: Dp = 300.dp,
    animationDuration: Int = 300,
    maskColor: Color = Color.Black.copy(alpha = 0.5f),
    showMask: Boolean = false,
    drawerSide: DrawerSide = DrawerSide.RIGHT,
    cornerRadius: Dp = 32.dp,
    dragThresholdFraction: Float = 0.5f,
    enableSwipe: Boolean = true
) {
    // Coroutine scope for managing animations
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current

    // Width of the drawer in pixels
    val drawerWidthPx = with(density) { drawerWidth.toPx() }

    // Offset for the drawer animation
    val offsetX = remember { Animatable(if (isDrawerOpen) 0f else drawerWidthPx * (if (drawerSide == DrawerSide.LEFT) -1 else 1)) }

    // Launch animation when the drawer state changes
    LaunchedEffect(isDrawerOpen) {
        val targetOffsetX = if (isDrawerOpen) 0f else drawerWidthPx * (if (drawerSide == DrawerSide.LEFT) -1 else 1)
        offsetX.animateTo(
            targetValue = targetOffsetX,
            animationSpec = tween(durationMillis = animationDuration)
        )
    }

    if (isDrawerOpen) {
        BackHandler {
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {

        // Mask overlay when the drawer is open
        if (isDrawerOpen && showMask) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(maskColor)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onDismiss() })
                    }
            )
        }

        // Drawer content
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(drawerWidth)
                .offset { IntOffset(x = 2 * offsetX.value.roundToInt(), y = 0) }
                .align(if (drawerSide == DrawerSide.LEFT) Alignment.CenterStart else Alignment.CenterEnd)
                .systemBarsPadding()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = if (cornerRadius > 0.dp) {
                        if (drawerSide == DrawerSide.LEFT) {
                            RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius)
                        } else {
                            RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius)
                        }
                    } else {
                        RectangleShape
                    }
                )
                .pointerInput(Unit) {
                    if (enableSwipe) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    val shouldClose = when (drawerSide) {
                                        DrawerSide.LEFT -> offsetX.value < -drawerWidthPx * dragThresholdFraction
                                        DrawerSide.RIGHT -> offsetX.value > drawerWidthPx * dragThresholdFraction
                                    }

                                    val finalTarget = if (shouldClose) {
                                        drawerWidthPx * (if (drawerSide == DrawerSide.LEFT) -1 else 1)
                                    } else {
                                        0f
                                    }

                                    offsetX.animateTo(
                                        targetValue = finalTarget,
                                        animationSpec = tween(durationMillis = animationDuration)
                                    )

                                    if (shouldClose) {
                                        onDismiss()
                                    }
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()

                            scope.launch {
                                val newOffset = offsetX.value + dragAmount.x

                                val clampedOffset = when (drawerSide) {
                                    DrawerSide.LEFT -> newOffset.coerceIn(-drawerWidthPx, 0f)
                                    DrawerSide.RIGHT -> newOffset.coerceIn(0f, drawerWidthPx)
                                }

                                offsetX.snapTo(clampedOffset)
                            }
                        }
                    }
                }
        ) {
            // Content inside the drawer
            drawerContent()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun CustomSideDrawerContent(
    drawerWidth: Dp = 300.dp ,
    cornerRadius: Dp = 32.dp,
){
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(drawerWidth)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius)

            ) ,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(64.dp))
        Box(

        ) {
            AsyncImage(
                model = Auth.auth.currentUser?.photoUrl ,
                contentDescription = stringResource(R.string.profile_photo) ,
                contentScale = ContentScale.Crop ,
                modifier = Modifier
                    .size(172.dp)
                    .clip(CircleShape)
            )


        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = Auth.auth.currentUser?.displayName!! ,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        Column {


            OutlinedButton(
                onClick = {
                    val intent = Intent()
                    intent.setAction(Intent.ACTION_SEND)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.setType("*/*")
                    intent.putExtra(Intent.EXTRA_TEXT,"merhaba")
                    context.startActivity(Intent.createChooser(intent,"share"))
                } ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.share_with_others)
                )
            }
            
            Text(
                text = stringResource(R.string.earn_token) ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) ,
                style = MaterialTheme.typography.labelSmall ,
            )


            OutlinedButton(
                onClick = {
                    InAppReview.requestReview(context)
                } ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.rate_review)
                )
            }
        }


        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    Auth.signOut(context)
                }
            } ,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.sign_out) ,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun GreetingPreview() {
    val context = LocalContext.current

    var isDrawerOpen = remember { mutableStateOf(false) }
    var isSendedPhoto = remember { mutableStateOf(false) }
    var prompt = remember { mutableStateOf("") }
    val selectedMod by remember { mutableStateOf("Generate") }
    PhotokenTheme(darkTheme = true) {
        CustomSideDrawerContent()
        /*Scaffold(
            modifier = Modifier
                .fillMaxSize() ,
            topBar = {
                TopAppBar(
                    title = {} ,
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                val intent = Intent(context , StoreActivity::class.java)
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.shopping_bag_speed_24px) ,
                                contentDescription = stringResource(R.string.shop_icon)
                            )
                        }
                    } ,
                    actions = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth() ,
                            verticalAlignment = Alignment.CenterVertically ,
                            horizontalArrangement = Arrangement.End
                        ) {


                            Box(
                                modifier = Modifier
                                    .background(Color.Transparent) // Butonun arkasına hafif şeffaf bir arka plan
                                    .padding(8.dp) ,
                                contentAlignment = Alignment.Center
                            ) {
                                AiChoiceSegmentedButton(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }

                            Box(

                            ) {
                                Row {
                                    Text(
                                        text = "1B"
                                    )
                                    Image(
                                        painter = painterResource(R.drawable.token_24dp_ffd700_fill0_wght400_grad0_opsz24) ,
                                        contentDescription = stringResource(R.string.token_icon) ,

                                        )
                                }

                            }

                            Box(
                                modifier = Modifier
                                    .clickable(
                                        enabled = true ,
                                        onClick = {
                                            isDrawerOpen.value = isDrawerOpen.value.not()
                                        }
                                    )
                            ) {
                                AsyncImage(
                                    model = R.drawable.ic_launcher_foreground ,
                                    contentDescription = stringResource(R.string.profile_photo) ,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }


                        }
                    } ,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent.copy(0.0f)
                    )
                )
            } ,
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier
                        .padding(vertical = 16.dp) ,
                    containerColor = Color.Transparent
                ) {
                    when(selectedMod){
                        "Generate" -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp , vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize() ,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BasicTextField(
                                        modifier = Modifier
                                            .weight(1f)
                                            .requiredHeight(68.dp),
                                        value = prompt.value ,
                                        onValueChange = { newPrompt ->
                                            prompt.value = newPrompt
                                        } ,
                                        textStyle = MaterialTheme.typography.bodyMedium,
                                        decorationBox = { innerTextField ->
                                            Box(
                                                modifier = Modifier
                                                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(32.dp))
                                                    .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 32.dp, vertical = 16.dp)

                                            ) {
                                                if (prompt.value.isEmpty()){
                                                    Text(
                                                        text = stringResource(R.string.message_placeholder) ,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                                innerTextField()
                                            }

                                        }
                                    )

                                    IconButton(
                                        onClick = {
                                        } ,
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = Color.Transparent
                                        ) ,
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(CircleShape) ,
                                        enabled = if (prompt.value.isEmpty()) false else true

                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.rocket_launch_24px) ,
                                            contentDescription = "" ,
                                            contentScale = ContentScale.Crop ,
                                            modifier = Modifier
                                                .size(48.dp)
                                        )
                                    }
                                }
                            }
                        }
                        "Mockup" -> {

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp , vertical = 8.dp)
                            ) {

                                // foto seçilmemişse
                                if (!isSendedPhoto.value){
                                    IconButton(
                                        onClick = {
                                            isSendedPhoto.value = isSendedPhoto.value.not()
                                        } ,
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        ) ,
                                        modifier = Modifier
                                            .size(68.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.image_arrow_up_24px) ,
                                            contentDescription = "" ,
                                            modifier = Modifier
                                                .size(48.dp)
                                        )
                                    }
                                } else {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize() ,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BasicTextField(
                                            modifier = Modifier
                                                .weight(1f)
                                                .requiredHeight(68.dp),
                                            value = prompt.value ,
                                            onValueChange = { newPrompt ->
                                                prompt.value = newPrompt
                                            } ,
                                            textStyle = MaterialTheme.typography.bodyMedium,
                                            decorationBox = { innerTextField ->
                                                Box(
                                                    modifier = Modifier
                                                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(32.dp))
                                                        .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                                                        .padding(horizontal = 32.dp, vertical = 16.dp)

                                                ) {
                                                    if (prompt.value.isEmpty()){
                                                        Text(
                                                            text = stringResource(R.string.message_placeholder) ,
                                                            style = MaterialTheme.typography.bodySmall
                                                        )
                                                    }
                                                    innerTextField()
                                                }

                                            }
                                        )

                                        IconButton(
                                            onClick = {
                                            } ,
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = Color.Transparent
                                            ) ,
                                            modifier = Modifier
                                                .size(68.dp)
                                                .clip(CircleShape)

                                        ) {
                                            Image(
                                                painter = painterResource(R.drawable.rocket_launch_24px) ,
                                                contentDescription = "" ,
                                                contentScale = ContentScale.Crop ,
                                                modifier = Modifier
                                                    .size(48.dp)
                                            )
                                        }
                                    }
                                }




                            }
                        }
                        else -> {}
                    }
                }
            }
        ) { innerPadding ->

            AiChatContainer(modifier = Modifier.padding(innerPadding) , innerPadding)
            if (isDrawerOpen.value){
                CustomSideDrawerOverlay(
                    isDrawerOpen = isDrawerOpen.value,
                    onDismiss = {
                        isDrawerOpen.value = isDrawerOpen.value.not()
                    },
                    drawerContent = {
                        CustomSideDrawerContent()
                    },
                    // No need to pass content here since it's handled separately
                    drawerWidth = 300.dp,  // Customize the drawer width
                    showMask = true,  // Optional: if you want to show the mask when drawer is open
                    drawerSide = DrawerSide.RIGHT,  // Drawer from left, or RIGHT
                    animationDuration = 300  // Animation duration for opening/closing the drawer
                )
            }
        }*/
    }
}