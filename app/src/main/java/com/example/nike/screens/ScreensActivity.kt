package com.example.nike.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nike.R
import com.example.nike.ui.theme.NikeTheme

val fonts = FontFamily(
    Font(R.font.merriweathersans_bold, FontWeight.Bold),
    Font(R.font.merriweathersans_semibold, FontWeight.SemiBold),
    Font(R.font.merriweathersans_regular, FontWeight.Normal)
)

class ScreensActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            NikeTheme {
                Screens_Activity()
            }
        }
    }
}

@Composable
private fun Screens_Activity() {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.screen_main),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(125.dp))

            Image(
                painter = painterResource(R.drawable.screen1),
                contentDescription = null,
                modifier = Modifier
                    .size(320.dp)
                    .rotate(-20f)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(75.dp))

            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = "Start Journey \nWith Nike",
                fontSize = 32.sp,
                lineHeight = 34.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF1A2530),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = "Smart, Gorgeous & Fashionable \nCollection",
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF707B81),
            )

            Spacer(modifier = Modifier.height(75.dp))

            Box(
                modifier = Modifier.padding(end = 30.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF5B9EE1))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { }
                    .padding(horizontal = 24.dp, vertical = 14.dp)
                    .align(Alignment.End),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 14.sp, lineHeight = 16.sp,
                    fontFamily = fonts, fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal, color = Color(0xFFF8F9FA)
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ScreensActivityPreview() {
    NikeTheme {
        Screens_Activity()
    }
}