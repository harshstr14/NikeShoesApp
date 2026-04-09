package com.example.nike.detailsScreen

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.example.nike.R
import com.example.nike.cartScreen.MyCartScreen
import com.example.nike.homeScreen.Shoe
import com.example.nike.homeScreen.user.UserViewModel
import com.example.nike.pressScale
import com.example.nike.screens.fonts
import com.example.nike.ui.theme.NikeTheme

class DetailsScreen : ComponentActivity() {
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

        val shoe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("shoe", Shoe::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("shoe")
        }

        setContent {
            NikeTheme {
                Details_Screen(shoe)
            }
        }
    }
}

@Composable
private fun Details_Screen(shoe: Shoe?) {
    val context = LocalContext.current
    val activity = context as? Activity
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val (backInteraction, backScale) = pressScale()
    val (cartInteraction, cartScale) = pressScale()
    val (likeInteraction, likeScale) = pressScale()
    val interactionSource = remember { MutableInteractionSource() }

    val sizes = listOf(38, 39, 40, 41, 42, 43)
    var selectedSize by remember { mutableIntStateOf(40) }
    val allImages = listOfNotNull(shoe?.imageURL) + (shoe?.shoeImages ?: emptyList())
    val images = allImages.drop(1)
    val pagerState = rememberPagerState(pageCount = { images.size })

    val viewModel: UserViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        uiState?.let {
            snackBarHostState.showSnackbar(it)
        }
    }

    val shoeId = shoe?.id?.toString() ?: ""

    val isFavorite by viewModel
        .observeFavorite(shoeId)
        .collectAsState(initial = false)

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
                                ambientColor = Color(0xFFF8F9FA),
                                spotColor = Color(0xFFF8F9FA)
                            ),
                        containerColor = Color(0xFFF8F9FA),
                        shape = RoundedCornerShape(9.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painter = painterResource(
                                when {
                                    data.visuals.message.contains("favourite") -> R.drawable.favourite
                                    data.visuals.message.contains("cart") -> R.drawable.cart
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

            Box(
                modifier = Modifier.padding(top = 15.dp, end = 20.dp)
                    .size(44.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFFFFF))
                    .clickable(
                        interactionSource = cartInteraction,
                        indication = null
                    ) {
                        val intent = Intent(context, MyCartScreen::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        context.startActivity(intent)
                    }
                    .zIndex(1f)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.cart),
                    contentDescription = "Cart Icon",
                    tint = Color(0xFF1A2530),
                    modifier = Modifier.size(22.dp)
                        .graphicsLayer {
                            scaleX = cartScale
                            scaleY = cartScale
                        }
                )
            }

            Box(
                modifier = Modifier.padding(top = 75.dp, end = 20.dp)
                    .size(44.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFFFFF))
                    .clickable(
                        interactionSource = likeInteraction,
                        indication = null
                    ) {
                        shoe?.let {
                            val item = Shoe(
                                description = it.description,
                                id = it.id,
                                imageURL = it.imageURL,
                                name = it.name,
                                type = it.type,
                                price = it.price,
                                shoeImages = it.shoeImages,
                                productDetails = it.productDetails,
                                quantity = 1,
                                shoeSize = selectedSize
                            )

                            viewModel.toggleFavorite(item)
                        }
                    }
                    .align(Alignment.TopEnd)
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(if (isFavorite) R.drawable.favourite_fill else R.drawable.favourite),
                    contentDescription = "Favourite Icon",
                    tint = Color(0xFF1A2530),
                    modifier = Modifier
                        .size(22.dp)
                        .graphicsLayer {
                            scaleX = likeScale
                            scaleY = likeScale
                        }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) { page ->
                val isFirst = page == 0

                AsyncImage(
                    model = images[page],
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isFirst) {
                                Modifier
                                    .rotate(-20f)
                                    .offset(x = (-16).dp, y = (-15).dp)
                            } else {
                                Modifier
                            }
                        ),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(1.0f)
                    .padding(top = 295.dp)
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    text = shoe?.name ?: "",
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    maxLines = 1,
                    color = Color(0xFF1A2530)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    text = shoe?.type ?: "",
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    maxLines = 1,
                    color = Color(0xFF707B81)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    text = "$${shoe?.price}",
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    text = shoe?.description ?: "",
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Normal,
                    maxLines = 3,
                    color = Color(0xFF707B81)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    text = "Gallery",
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )

                val shoes = shoe?.shoeImages ?: emptyList()

                Spacer(modifier = Modifier.height(10.dp))

                ShoeRow(shoes)

                Spacer(modifier = Modifier.height(16.dp))

                SizeHeader()

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LazyRow(
                        modifier = Modifier.wrapContentWidth(),
                        contentPadding = PaddingValues(horizontal = 25.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sizes) { size ->
                            SizeItem(
                                textSize = size,
                                isSelected = size == selectedSize,
                                onClick = { selectedSize = size }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp)
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Price",
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontFamily = fonts,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF707B81)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$${shoe?.price}",
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        fontFamily = fonts,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Normal,
                        maxLines = 1,
                        color = Color(0xFF1A2530)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF5B9EE1))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            shoe?.let {
                                val item = Shoe(
                                    description = it.description,
                                    id = it.id,
                                    imageURL = it.imageURL,
                                    name = it.name,
                                    type = it.type,
                                    price = it.price,
                                    shoeImages = it.shoeImages,
                                    productDetails = it.productDetails,
                                    quantity = 1,
                                    shoeSize = selectedSize
                                )

                                viewModel.addToCart(item)
                            }
                        }
                        .padding(horizontal = 26.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text =  "Add To Cart",
                        fontSize = 14.sp, lineHeight = 16.sp,
                        fontFamily = fonts, fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal, color = Color(0xFFF8F9FA)
                    )
                }
            }
        }
    }
}

@Composable
fun SizeHeader() {
    var selectedUnit by remember { mutableStateOf("EU") }
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Size",
            fontSize = 18.sp,
            lineHeight = 20.sp,
            fontFamily = fonts,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal,
            color = Color(0xFF1A2530),
        )

        Spacer(modifier = Modifier.weight(1f))

        listOf("EU", "US", "UK").forEach { unit ->
            val isSelected = unit == selectedUnit

            Text(
                text = unit,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { selectedUnit = unit },
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                color = if (isSelected) Color(0xFF1A2530) else Color(0xFF707B81),
            )
        }
    }
}

@Composable
fun SizeItem(
    textSize: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) Color(0xFF5B9EE1) else Color(0xFFF8F9FA),
        label = ""
    )

    val textColor by animateColorAsState(
        if (isSelected) Color(0xFFFFFFFF) else Color(0xFF707B81),
        label = ""
    )

    Box(
        modifier = Modifier
            .size(45.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = textSize.toString(),
            color = textColor,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            fontFamily = fonts,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
        )
    }
}

@Composable
fun ShoeRow(shoes: List<String>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(shoes) { shoe ->
            ShoeItem(image = shoe)
        }
    }
}

@Composable
fun ShoeItem(image: String) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8F9FA)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun DetailsScreenPreview() {
    NikeTheme {
        Details_Screen(shoe = Shoe())
    }
}