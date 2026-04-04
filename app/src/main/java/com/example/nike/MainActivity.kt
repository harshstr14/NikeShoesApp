package com.example.nike

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.nike.screens.ScreensActivity
import com.example.nike.ui.theme.NikeTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val isLoggedIn = firebaseUser != null

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = 0xFFFF000000.toInt()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = 0xFFFF000000.toInt()
            )
        )

        setContent {
            NikeTheme {
                SplashScreen {
                    if (isLoggedIn) {
                        startActivity(Intent(this, MainScreen::class.java))
                    } else {
                        startActivity(Intent(this, ScreensActivity::class.java))
                    }
                    finish()
                }
            }
        }
    }
}

@Composable
private fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.black))
        , contentAlignment = Alignment.Center
    ){
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes (R.raw.nike)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(180.dp)
                .clip(RectangleShape),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer {
                        scaleX = 1.2f
                        scaleY = 1.2f
                    }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreview() {
    NikeTheme {
        SplashScreen(onTimeout = {})
    }
}