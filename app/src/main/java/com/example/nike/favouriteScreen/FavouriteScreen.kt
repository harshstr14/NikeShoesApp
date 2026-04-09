package com.example.nike.favouriteScreen

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.nike.R
import com.example.nike.detailsScreen.DetailsScreen
import com.example.nike.homeScreen.Shoe
import com.example.nike.homeScreen.user.UserViewModel
import com.example.nike.navigation.BottomNavRoute
import com.example.nike.pressScale
import com.example.nike.screens.fonts

@Composable
fun FavouriteScreen(navController: NavHostController, snackBarHostState: SnackbarHostState) {
    val (backInteraction, backScale) = pressScale()
    val (heartInteraction, heartScale) = pressScale()

    val viewModel: UserViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        uiState?.let {
            snackBarHostState.showSnackbar(it)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFF8F9FA))
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
                    navController.navigate(BottomNavRoute.Home.route) {
                        popUpTo(BottomNavRoute.Home.route)
                        launchSingleTop = true
                    }
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
            text = "Favourite",
            fontSize = 18.sp,
            lineHeight = 22.sp,
            fontFamily = fonts,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Normal,
            color = Color(0xFF1A2530),
        )

        Box(
            modifier = Modifier.padding(top = 15.dp, end = 20.dp)
                .size(44.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFFFFFFF))
                .clickable(
                    interactionSource = heartInteraction,
                    indication = null
                ) {

                }
                .zIndex(1f)
                .align(Alignment.TopEnd),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.favourite),
                contentDescription = "Favourite Icon",
                tint = Color(0xFF1A2530),
                modifier = Modifier.size(22.dp)
                    .graphicsLayer {
                        scaleX = heartScale
                        scaleY = heartScale
                    }
            )
        }

        ShoesSection(viewModel)
    }
}

@Composable
fun ShoesSection(viewModel: UserViewModel) {
    val context = LocalContext.current
    val state by viewModel.favoritesState.collectAsState()

    when (state) {
        is FavouriteUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
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

        is FavouriteUiState.Success -> {
            val list = (state as FavouriteUiState.Success).data

            if (list.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No favourites yet",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontFamily = fonts,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(top = 85.dp, start = 25.dp, end = 25.dp, bottom = 125.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(list) { shoe ->
                        ShoeCard(
                            shoe = shoe,
                            viewModel = viewModel,
                            onClick = {
                                val intent = Intent(context, DetailsScreen::class.java).apply {
                                    putExtra("shoe", shoe)
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }

        is FavouriteUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (state as FavouriteUiState.Error).message,
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
}

@Composable
fun ShoeCard(shoe: Shoe, viewModel: UserViewModel, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val (favouriteInteraction, favouriteScale) = pressScale()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(224.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFFFFFF))
    ) {
        Icon(
            painter = painterResource(R.drawable.favourite_fill),
            contentDescription = "Favourite Icon",
            tint = Color(0xFF1A2530),
            modifier = Modifier
                .clickable(
                    interactionSource = favouriteInteraction,
                    indication = null
                ) {
                    viewModel.toggleFavorite(shoe)
                }
                .graphicsLayer(
                    scaleX = favouriteScale,
                    scaleY = favouriteScale
                )
                .padding(12.dp)
                .size(22.dp)
                .align(Alignment.TopEnd)
        )

        Column {
            Image(
                painter = rememberAsyncImagePainter(shoe.imageURL),
                contentDescription = shoe.name,
                modifier = Modifier
                    .size(150.dp)
                    .offset(x = (-10).dp)
                    .rotate(-28f),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .offset(y = (-8).dp),
                text = shoe.name,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                maxLines = 1,
                color = Color(0xFF1A2530)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .offset(y = (-8).dp),
                text = shoe.type,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                maxLines = 1,
                color = Color(0xFF707B81)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .offset(y = (-8).dp),
                text = "$${shoe.price}",
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF1A2530),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp)
                .clip(RoundedCornerShape(topStart = 10.dp))
                .background(Color(0xFF1A2530)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = "Go",
                tint = Color(0xFFFFFFFF),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun FavouriteScreenPreview() {
    val snackBarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    FavouriteScreen(navController, snackBarHostState)
}