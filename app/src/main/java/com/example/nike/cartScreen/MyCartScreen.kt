package com.example.nike.cartScreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.nike.R
import com.example.nike.checkoutScreen.CheckoutScreen
import com.example.nike.detailsScreen.DetailsScreen
import com.example.nike.homeScreen.user.UserViewModel
import com.example.nike.pressScale
import com.example.nike.screens.fonts
import com.example.nike.ui.theme.NikeTheme
import java.util.Locale

class MyCartScreen : ComponentActivity() {
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
                MyCart_Screen()
            }
        }
    }
}

@Composable
private fun MyCart_Screen(viewModel: UserViewModel = viewModel()) {
    val context = LocalContext.current
    val activity = context as? Activity
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val (backInteraction, backScale) = pressScale()
    val interactionSource = remember { MutableInteractionSource() }

    val cartState by viewModel.cartState.collectAsState()

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
                text = "My Cart",
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF1A2530),
            )

            val list = (cartState as? CartUiState.Success)?.data ?: emptyList()
            val subtotal = list.sumOf { it.price * it.quantity }
            val shipping = if (list.isEmpty()) 0.0 else 40.90
            val total = subtotal + shipping

            when (cartState) {
                is CartUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .padding(top = 60.dp, bottom = 240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes (R.raw.nike_logo_animation)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(140.dp)
                                .clip(RectangleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier
                                    .size(140.dp)
                                    .graphicsLayer {
                                        scaleX = 1.2f
                                        scaleY = 1.2f
                                    }
                            )
                        }
                    }
                }

                is CartUiState.Success -> {
                    if (list.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .padding(top = 60.dp, bottom = 240.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cart is empty",
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontFamily = fonts,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                color = Color(0xFF1A2530)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.TopCenter)
                                .padding(top = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 250.dp)
                        ) {
                            items(list, key = { it.id }) { item ->
                                CartItem(
                                    image = item.imageURL,
                                    name = item.name,
                                    price = item.price,
                                    size = item.shoeSize,
                                    quantity = item.quantity,

                                    onIncrease = {
                                        viewModel.increaseQuantity(item)
                                    },

                                    onDecrease = {
                                        viewModel.decreaseQuantity(item.id.toString())
                                    },

                                    onDelete = {
                                        viewModel.removeFromCart(item.id.toString())
                                    },

                                    onClick = {
                                        val intent = Intent(context, DetailsScreen::class.java).apply {
                                            putExtra("shoe", item)
                                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        }
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }

                is CartUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .padding(top = 60.dp, bottom = 240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (cartState as CartUiState.Error).message,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = fonts,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = Color(0xFF1A2530)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .align(Alignment.BottomCenter)
                    .height(240.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 26.dp, start = 25.dp)
                        .align(Alignment.TopStart),
                    text = "Subtotal",
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF707B81)
                )

                Text(
                    modifier = Modifier
                        .padding(top = 26.dp, end = 25.dp)
                        .align(Alignment.TopEnd),
                    text = "$ ${String.format(Locale.US, "%.2f", subtotal)}",
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                Text(
                    modifier = Modifier
                        .padding(top = 66.dp, start = 25.dp)
                        .align(Alignment.TopStart),
                    text = "Shopping",
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF707B81)
                )

                Text(
                    modifier = Modifier
                        .padding(top = 66.dp, end = 25.dp)
                        .align(Alignment.TopEnd),
                    text = "$ ${String.format(Locale.US, "%.2f", shipping)}",
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                DashedLine(
                    modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 105.dp)
                )

                Text(
                    modifier = Modifier
                        .padding(top = 120.dp, start = 25.dp)
                        .align(Alignment.TopStart),
                    text = "Total Cost",
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                Text(
                    modifier = Modifier
                        .padding(top = 120.dp, end = 25.dp)
                        .align(Alignment.TopEnd),
                    text = "$ ${String.format(Locale.US, "%.2f", total)}",
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                Box(
                    modifier = Modifier
                        .padding(start = 25.dp, end = 25.dp, top = 168.dp)
                        .height(52.dp).fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF5B9EE1))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            val intent = Intent(context, CheckoutScreen::class.java).apply {
                                putExtra("subTotal", subtotal)
                                putExtra("totalCost", total)
                                putExtra("shipping", shipping)
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            context.startActivity(intent)
                        }
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Checkout",
                        fontSize = 15.sp, lineHeight = 16.sp,
                        fontFamily = fonts, fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal, color = Color(0xFFFFFFFF)
                    )
                }
            }
        }
    }
}

@Composable
fun CartItem(
    image: String,
    name: String,
    price: Double,
    size: Int,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val (deleteInteraction, deleteScale) = pressScale()
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 20.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(85.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier.size(85.dp)
                    .rotate(-20f)
                    .offset(x = (-2).dp, y = (-5).dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF1A2530)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$ $price",
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF1A2530)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantityButton("-", onClick = onDecrease)

                Text(
                    text = quantity.toString(),
                    modifier = Modifier.padding(horizontal = 15.dp),
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530)
                )

                QuantityButton("+", onClick = onIncrease)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$size",
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF1A2530)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Icon(
                painter = painterResource(R.drawable.delete_icon),
                contentDescription = null,
                tint = Color(0xFFFF6B6B),
                modifier = Modifier
                    .size(20.dp)
                    .clickable(
                        interactionSource = deleteInteraction,
                        indication = null
                    ) { onDelete() }
                    .graphicsLayer(
                        scaleX = deleteScale,
                        scaleY = deleteScale
                    )
            )
        }
    }
}

@Composable
fun QuantityButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(
                if (text == "+") Color(0xFF5B9EE1) else Color(0xFFFFFFFF)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (text == "+") Color(0xFFFFFFFF) else Color(0xFF828A89),
            fontSize = 16.sp,
            lineHeight = 16.sp,
            fontFamily = fonts,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Normal
        )
        Icon(
            painter = painterResource(if (text == "+") R.drawable.plus_icon else R.drawable.minus_icon),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(8.dp)
        )
    }
}

@Composable
fun DashedLine(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFEEEEEE)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = size.height,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(14f, 14f),
                0f
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MyCartScreenPreview() {
    NikeTheme {
        MyCart_Screen()
    }
}