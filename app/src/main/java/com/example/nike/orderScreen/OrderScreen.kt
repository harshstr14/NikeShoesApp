package com.example.nike.orderScreen

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.nike.R
import com.example.nike.pressScale
import com.example.nike.screens.fonts
import com.example.nike.ui.theme.NikeTheme

class OrderScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = 0xFFF8F9FA.toInt()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = 0xFFF8F9FA.toInt()
            )
        )

        setContent {
            NikeTheme {
                Order_Screen()
            }
        }
    }
}

@Composable
private fun Order_Screen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val (backInteraction, backScale) = pressScale()
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        containerColor = colorResource(id = R.color.background_color),
        modifier = Modifier.background(Color(0xFFF8F9FA)),
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 15.dp)
            ) { data ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Snackbar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(10.dp),
                                ambientColor = Color(0xFFFFFFFF),
                                spotColor = Color(0xFFFFFFFF)
                            ),
                        containerColor = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(9.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painter = painterResource(
                                when {
                                    data.visuals.message.contains("name") -> R.drawable.user_icon
                                    data.visuals.message.contains("email") -> R.drawable.email_icon
                                    data.visuals.message.contains("Email") -> R.drawable.email_icon
                                    data.visuals.message.contains("password") -> R.drawable.password_icon
                                    data.visuals.message.contains("Password") -> R.drawable.password_icon
                                    else -> {
                                        R.drawable.alert_icon
                                    }
                                }
                            ), contentDescription = "Icons",
                                tint = Color(0xFF5B9EE1), modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = data.visuals.message,
                                fontFamily = fonts,
                                fontWeight = FontWeight.SemiBold,
                                fontStyle = FontStyle.Normal,
                                fontSize = 13.sp,
                                color = Color(0xFF1A2530)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.padding(top = 15.dp, start = 20.dp)
                    .size(44.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFFFFF))
                    .clickable(
                        interactionSource = backInteraction,
                        indication = null
                    ) {
                        activity?.finish()
                    }
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_icon),
                    contentDescription = "Back Icon",
                    tint = Color(0xFF1A2530),
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .size(15.dp)
                        .graphicsLayer {
                            scaleX = backScale
                            scaleY = backScale
                        }
                )
            }

            Text(
                modifier = Modifier
                    .padding(top = 26.dp)
                    .align(Alignment.TopCenter),
                text = "Orders",
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF1A2530),
            )

            val orders = List(4) {
                Order(
                    id = "987456",
                    date = "28 Dec 2025",
                    time = "02:25 pm",
                    price = "$150",
                    items = "4 Items"
                )
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF6F7F9))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        OrderItem(order)
                    }
                }
            }
        }
    }
}

data class Order(
    val id: String,
    val date: String,
    val time: String,
    val price: String,
    val items: String
)

@Composable
fun OrderItem(order: Order) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F9FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.box_icon),
                    contentDescription = null,
                    tint = Color(0xFF5B9EE1),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Order ID : ${order.id}",
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.calender_icon),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF707B81)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = order.date,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        fontFamily = fonts,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF707B81)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        painter = painterResource(R.drawable.time_icon),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF707B81)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = order.time,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        fontFamily = fonts,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF707B81)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = order.items,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF707B81)
                )
            }

            Text(
                text = order.price,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF1A2530)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun OrderScreenPreview() {
    NikeTheme {
       Order_Screen()
    }
}