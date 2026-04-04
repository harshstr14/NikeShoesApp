package com.example.nike.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nike.R
import com.example.nike.SignInScreen
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

data class OnBoardingPage(
    val image: Int,
    val title: String,
    val desc: String
)

val pages = listOf(
    OnBoardingPage(R.drawable.screen1, "Start Journey \nWith Nike", "Smart, Gorgeous & Fashionable \nCollection"),
    OnBoardingPage(R.drawable.screen1, "Follow Latest \nStyle Shoes", "There Are Many Beautiful And \nAttractive Shoes"),
    OnBoardingPage(R.drawable.screen1, "Summer Shoes \nNike 2022", "Amet Minim Lit Nodeseru Saku \nNandu sit Alique Dolor")
)

@Composable
private fun Screens_Activity() {
    val context = LocalContext.current
    val activity = context as? Activity
    val interactionSource = remember { MutableInteractionSource() }
    var currentPage by remember { mutableIntStateOf(0) }

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

            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(400)
                        ) + fadeIn()) togetherWith
                                (slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(400)
                                ) + fadeOut())
                    } else {
                        (slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(400)
                        ) + fadeIn()) togetherWith
                                (slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(400)
                                ) + fadeOut())
                    }
                },
                label = ""
            ) { pageIndex ->
                val page = pages[pageIndex]

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(page.image),
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
                        text = page.title,
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
                        text = page.desc,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontFamily = fonts,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF707B81),
                    )

                    Spacer(modifier = Modifier.height(75.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 30.dp)
                        .weight(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = currentPage == index

                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .height(5.dp)
                                .width(if (isSelected) 32.dp else 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isSelected)
                                        Color(0xFF5B9EE1)
                                    else
                                        Color(0xFFE5EEF7)
                                )
                        )
                    }
                }

                Box(
                    modifier = Modifier.padding(end = 30.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF5B9EE1))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            if (currentPage < pages.lastIndex) {
                                currentPage++
                            } else {
                                val intent = Intent(context, SignInScreen::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                }
                                context.startActivity(intent)
                            }
                        }
                        .padding(horizontal = 26.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentPage == 0) "Get Started" else "Next",
                        fontSize = 14.sp, lineHeight = 16.sp,
                        fontFamily = fonts, fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal, color = Color(0xFFF8F9FA)
                    )
                }
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