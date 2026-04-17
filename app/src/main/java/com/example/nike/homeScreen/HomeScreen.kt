package com.example.nike.homeScreen

import android.content.Intent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.nike.R
import com.example.nike.cartScreen.MyCartScreen
import com.example.nike.detailsScreen.DetailsScreen
import com.example.nike.homeScreen.banner.BannerViewModel
import com.example.nike.homeScreen.shoes.ShoesViewModel
import com.example.nike.navigation.BottomNavRoute
import com.example.nike.pressScale
import com.example.nike.screens.fonts
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController, drawerState: DrawerState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val (menuInteraction, menuScale) = pressScale()
    val (cartInteraction, cartScale) = pressScale()

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
                    interactionSource = menuInteraction,
                    indication = null
                ) {
                    scope.launch {
                        drawerState.open()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.menu_icon),
                contentDescription = "Menu Icon",
                tint = Color(0xFF1A2530),
                modifier = Modifier.size(16.dp)
                    .graphicsLayer {
                        scaleX = menuScale
                        scaleY = menuScale
                    }
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Store location",
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF707B81),
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.location_icon),
                    contentDescription = "Menu Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.padding(bottom = 2.dp).size(16.dp)
                )

                Spacer(modifier = Modifier.width(3.dp))

                Text(
                    text = "Mondolibug, Sylhet",
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    color = Color(0xFF1A2530),
                )
            }
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

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 25.dp, end = 25.dp, top = 75.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFFFFFFF))
                    .clickable(
                        interactionSource = cartInteraction,
                        indication = null
                    ) {
                        navController.navigate(BottomNavRoute.Search.route) {
                            launchSingleTop = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(start = 18.dp, end = 12.dp)
                            .size(26.dp),
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search Icon",
                        tint = Color(0xFF707B81)
                    )

                    Text(
                        text = "Looking for shoes",
                        fontSize = 15.sp,
                        lineHeight = 18.sp,
                        fontFamily = fonts,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF707B81),
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            AutoImageSlider()

            CategoryChips()

            Spacer(modifier = Modifier.height(15.dp))

            ShoesSection()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoImageSlider(viewModel: BannerViewModel = viewModel()) {
    val context = LocalContext.current
    val bannerImages by viewModel.bannerImages.observeAsState(emptyList())
    val bannerShoes by viewModel.banners.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(false)
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.fetchBanners()
    }

    if (bannerImages.isEmpty()) return

    val fakePageCount = Int.MAX_VALUE
    val startPage = fakePageCount / 2

    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { fakePageCount }
    )

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3500)

            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(
                    pagerState.currentPage + 1,
                    animationSpec = tween(
                        durationMillis = 900,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    if (!loading && bannerImages.isNotEmpty()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 10.dp),
            pageSpacing = 12.dp
        ) { page ->
            val realIndex = page % bannerImages.size
            val shoe = bannerShoes[realIndex]

            Image(
                painter = rememberAsyncImagePainter(bannerImages[realIndex]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
                    .height(225.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
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

@Composable
fun CategoryChips(viewModel: ShoesViewModel = viewModel()) {
    val categories = listOf("All","Air Jordan 1","Air Force 1","Dunk","Blazer","V2K")
    var selectedCategory by remember { mutableStateOf("All") }
    val interactionSource = remember { MutableInteractionSource() }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected) Color(0xFF1A2530) else Color(0xFFFFFFFF)
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        selectedCategory = category
                        viewModel.selectCategory(category)
                    }
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color(0xFFFFFFFF) else Color(0xFF1A2530),
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                )
            }
        }
    }
}

@Composable
fun ShoesSection(viewModel: ShoesViewModel = viewModel()) {
    val context = LocalContext.current
    val shoes by viewModel.shoes.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(false)

    LaunchedEffect(Unit) {
        viewModel.selectCategory("All")
    }

    if (loading) {
        Text(
            text = "Loading...",
            modifier = Modifier.padding(20.dp)
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 25.dp, end = 25.dp, bottom = 125.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(shoes) { shoe ->
                ShoeCard(
                    shoe = shoe,
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

@Composable
fun ShoeCard(shoe: Shoe, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

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
private fun HomeScreenPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()
    HomeScreen(navController, drawerState)
}