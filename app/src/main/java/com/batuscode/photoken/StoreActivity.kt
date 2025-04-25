package com.batuscode.photoken

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.batuscode.photoken.model.Token
import com.batuscode.photoken.ui.theme.PhotokenTheme
import com.batuscode.photoken.utils.Auth
import com.batuscode.photoken.viewmodel.StoreActivityViewModel
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoreActivity : ComponentActivity() , PurchasesUpdatedListener {
    private val TAG = "StorePurchases"

    companion object {
        lateinit var purchasesUpdatedListener: PurchasesUpdatedListener
        lateinit var billingClient: BillingClient
        lateinit var storeActivityViewModel: StoreActivityViewModel
    }
    suspend fun processQuery(){
        val subsList = listOf(
            Product.newBuilder()
                .setProductId("snaphan_studio_plan")
                .setProductType(ProductType.SUBS)
                .build() ,
            Product.newBuilder()
                .setProductId("snaphan_black_label")
                .setProductType(ProductType.SUBS)
                .build()
        )
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("snaphan_1")
                .setProductType(BillingClient.ProductType.INAPP)
                .build() ,
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("snaphan_2")
                .setProductType(BillingClient.ProductType.INAPP)
                .build() ,
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("snaphan_3")
                .setProductType(BillingClient.ProductType.INAPP)
                .build() ,
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("snaphan_4")
                .setProductType(BillingClient.ProductType.INAPP)
                .build() ,
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("snaphan_5")
                .setProductType(BillingClient.ProductType.INAPP)
                .build() ,


        )
        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        // leverage queryProductDetails Kotlin extension function
        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }

        val subsParams = QueryProductDetailsParams.newBuilder()
        subsParams.setProductList(subsList)

        val subsDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(subsParams.build())
        }

        val details = productDetailsResult.productDetailsList?.takeIf { it.isNotEmpty() }

        if (details != null) {
            for ( i in details){
                val token = Token(
                    image = R.drawable.token_24dp_ffd700_fill0_wght400_grad0_opsz24,
                    name = i.name ,
                    title = i.title ,
                    description = i.description ,
                    price = i.oneTimePurchaseOfferDetails!!.formattedPrice ,
                    productDetails = i ,
                    type = "one-time"
                )
                storeActivityViewModel.addToken(token)
            }
        }

        val subsDetails = subsDetailsResult.productDetailsList?.takeIf { it.isNotEmpty() }

        if (subsDetails != null) {
            for ( i in subsDetails){
                val token = Token(
                    image = R.drawable.token_24dp_ffd700_fill0_wght400_grad0_opsz24,
                    name = i.name ,
                    title = i.title ,
                    description = i.description ,
                    price = i.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice!! ,
                    productDetails = i ,
                    type = "subs"
                )
                storeActivityViewModel.addToken(token)
            }
        }


    }
    override fun onStart() {
        super.onStart()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    CoroutineScope(Dispatchers.IO).launch {
                        processQuery()
                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storeActivityViewModel = ViewModelProvider(this).get(StoreActivityViewModel::class.java)
        purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
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

        billingClient = BillingClient.newBuilder(this@StoreActivity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        enableEdgeToEdge()
        setContent {
            val products by remember { derivedStateOf { storeActivityViewModel.tokens } }
            PhotokenTheme(darkTheme = true) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2) ,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding) ,
                        contentPadding = PaddingValues(8.dp) ,
                        verticalArrangement = Arrangement.spacedBy(8.dp) ,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f), // kare kutular için
                                elevation = CardDefaults.cardElevation(4.dp) ,
                                onClick = {
                                    if (Auth.auth.currentUser != null){
                                        val uid = Auth.auth.currentUser?.uid
                                        var productDetailsParamsList = emptyList<BillingFlowParams.ProductDetailsParams>()

                                        when(item.type){
                                            "one-time" -> {
                                                productDetailsParamsList = listOf(
                                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                                        .setProductDetails(item.productDetails)
                                                        // For One-time products, "setOfferToken" method shouldn't be called.
                                                        // For subscriptions, to get an offer token, call ProductDetails.subscriptionOfferDetails()
                                                        // for a list of offers that are available to the user
                                                        .build()
                                                )
                                            }
                                            "subs" -> {
                                                productDetailsParamsList = listOf(
                                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                                        .setProductDetails(item.productDetails)
                                                        // For One-time products, "setOfferToken" method shouldn't be called.
                                                        // For subscriptions, to get an offer token, call ProductDetails.subscriptionOfferDetails()
                                                        // for a list of offers that are available to the user
                                                        .setOfferToken(item.productDetails.subscriptionOfferDetails?.get(0)?.offerToken!!)
                                                        .build()
                                                )
                                            }
                                        }

                                        if (uid != null){
                                            val billingFlowParams = BillingFlowParams.newBuilder()
                                                .setProductDetailsParamsList(productDetailsParamsList)
                                                .setIsOfferPersonalized(true)
                                                .setObfuscatedAccountId(uid)
                                                .build()
// Launch the billing flow
                                            val billingResult = billingClient.launchBillingFlow(this@StoreActivity, billingFlowParams)
                                        }

                                    }
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(PaddingValues(8.dp)),
                                    horizontalAlignment = Alignment.CenterHorizontally ,
                                    verticalArrangement = Arrangement.Center ,
                                ) {
                                    Image(
                                        painter = painterResource(item.image) ,
                                        contentDescription = "" ,
                                        modifier = Modifier
                                            .size(48.dp)
                                    )
                                    Text(text = item.name)
                                    Text(text = item.description)
                                    Text(text = item.price)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase?>?
    ) {
        if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                // Process the purchase as described in the next section.
            }
        } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user canceling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    /*val products = listOf<Token>(
        Token(
            R.drawable.token_24dp_ffd700_fill0_wght400_grad0_opsz24 ,
            "name" ,
            "title" ,
            "description" ,
            "$0.42"
        ) ,
        Token(
            R.drawable.token_24dp_ffd700_fill0_wght400_grad0_opsz24 ,
            "name" ,
            "title" ,
            "description" ,
            "$0.70"
        ) ,
        Token(
            R.drawable.token_24dp_ffd700_fill0_wght400_grad0_opsz24 ,
            "name" ,
            "title" ,
            "description" ,
            "$0.50"
        ) ,
        Token(
            R.drawable.token_24dp_ffd700_fill0_wght400_grad0_opsz24 ,
            "name" ,
            "title" ,
            "description" ,
            "$0.90"
        )
    )
    PhotokenTheme(darkTheme = true) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2) ,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) ,
                contentPadding = PaddingValues(8.dp) ,
                verticalArrangement = Arrangement.spacedBy(8.dp) ,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f), // kare kutular için
                        elevation = CardDefaults.cardElevation(4.dp) ,
                        onClick = {
                            val productDetailsParamsList = listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                    .setProductDetails(item.productDetails)
                                    // For One-time products, "setOfferToken" method shouldn't be called.
                                    // For subscriptions, to get an offer token, call ProductDetails.subscriptionOfferDetails()
                                    // for a list of offers that are available to the user
                                    .build()
                            )

                            val billingFlowParams = BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(productDetailsParamsList)
                                .build()

// Launch the billing flow
                            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize() ,
                            horizontalAlignment = Alignment.CenterHorizontally ,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(item.image) ,
                                contentDescription = "" ,
                                modifier = Modifier
                                    .size(48.dp)
                            )
                            Text(text = item.name)
                            Text(text = item.title)
                            Text(text = item.description)
                            Text(text = item.price)
                        }
                    }
                }
            }
        }
    }*/
}