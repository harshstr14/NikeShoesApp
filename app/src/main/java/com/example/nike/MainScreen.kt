package com.example.nike

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nike.favouriteScreen.FavouriteScreen
import com.example.nike.homeScreen.HomeScreen
import com.example.nike.navigation.BottomItem
import com.example.nike.navigation.BottomNavRoute
import com.example.nike.notificationScreen.NotificationScreen
import com.example.nike.profileScreen.ProfileScreen
import com.example.nike.screens.fonts
import com.example.nike.ui.theme.NikeTheme

class MainScreen : ComponentActivity() {
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
                Main_Screen()
            }
        }
    }
}

@Composable
private fun Main_Screen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .width(280.dp),
                drawerContainerColor = Color.Transparent
            ) {
                DrawerContent()
            }
        }
    ) {
        Scaffold(
            containerColor = colorResource(id = R.color.background_color),
            modifier = Modifier.background(Color(0xFFF8F9FA)),
            bottomBar = {
                BottomNavBar(navController = navController)
            },
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
            NavHost(
                navController = navController,
                startDestination = BottomNavRoute.Home.route,
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),

                enterTransition = {
                    scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(
                            durationMillis = 320,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(220)
                    )
                },

                exitTransition = {
                    scaleOut(
                        targetScale = 1.05f,
                        animationSpec = tween(
                            durationMillis = 220,
                            easing = FastOutLinearInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(160)
                    )
                },

                popEnterTransition = {
                    scaleIn(
                        initialScale = 0.95f,
                        animationSpec = tween(300)
                    ) + fadeIn()
                },

                popExitTransition = {
                    scaleOut(
                        targetScale = 0.92f,
                        animationSpec = tween(220)
                    ) + fadeOut()
                }
            ) {
                composable(BottomNavRoute.Home.route) {
                    HomeScreen()
                }

                composable(BottomNavRoute.Favourites.route) {
                    FavouriteScreen()
                }

                composable(BottomNavRoute.Cart.route) {

                }

                composable(BottomNavRoute.Notification.route) {
                    NotificationScreen()
                }

                composable(BottomNavRoute.Profile.route) {
                    ProfileScreen()
                }
            }
        }
    }
}

@Composable
fun DrawerContent() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(
                color = Color(0xFF1A2530),
                shape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp)
            )
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .offset(x = (-3).dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column {
            Text(
                text = "Hey 👋",
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                color = Color(0xFF707B81)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Alisson Becker",
                color = Color(0xFFFFFFFF),
                fontSize = 20.sp,
                lineHeight = 22.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        DrawerItem("Profile", R.drawable.user)
        DrawerItem("Home Page", R.drawable.home)
        DrawerItem("My Cart", R.drawable.cart)
        DrawerItem("Favorite", R.drawable.favourite)
        DrawerItem("Orders", R.drawable.delivery_icon)
        DrawerItem("Notifications", R.drawable.notification)

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
            thickness = 1.dp,
            color = Color(0xFF2D3B48)
        )

        Spacer(modifier = Modifier.height(12.dp))

        DrawerItem("Sign Out", R.drawable.signout_icon)
    }
}

@Composable
fun DrawerItem(title: String, icon: Int) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(icon), contentDescription = title,
            tint = Color(0xFF707B81)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = Color(0xFFFFFFFF),
            fontSize = 16.sp,
            lineHeight = 18.sp,
            fontFamily = fonts,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
        )
    }
}

@Composable
private fun BottomNavBar(navController: NavController) {
    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    val (homeInteraction, homeScale) = pressScale()
    val (cartInteraction, cartScale) = pressScale()
    val (profileInteraction, profileScale) = pressScale()

    val items = listOf(
        BottomItem(
            BottomNavRoute.Home.route,
            "Home",
            R.drawable.home,
        ),
        BottomItem(
            BottomNavRoute.Favourites.route,
            "Favourites",
            R.drawable.favourite
        ),
        BottomItem(
            BottomNavRoute.Cart.route,
            "Cart",
            R.drawable.cart
        ),
        BottomItem(
            BottomNavRoute.Notification.route,
            "Notification",
            R.drawable.notification
        ),
        BottomItem(
            BottomNavRoute.Profile.route,
            "Profile",
            R.drawable.user
        )
    )

    Box(
        modifier = Modifier.fillMaxWidth()
            .background(Color.Transparent)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.bottom_nav_background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.take(2).forEach { item ->
                    val isSelected = currentRoute?.let { it == item.route }
                        ?: (item.route == BottomNavRoute.Home.route)

                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.label,
                        tint = if (isSelected) Color(0xFF5B9EE1) else Color(0xFF707B81),
                        modifier = Modifier
                            .padding(horizontal = 18.dp)
                            .size(26.dp)
                            .clickable(
                                interactionSource = homeInteraction,
                                indication = null
                            ) {
                                if (!isSelected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                    )
                }
            }

            val cartItem = items[2]

            Icon(
                painter = painterResource(cartItem.icon),
                contentDescription = cartItem.label,
                tint = Color.White,
                modifier = Modifier
                    .size(55.dp)
                    .offset(y = (-24).dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF5B9EE1).copy(alpha = 0.6f),
                                    Color(0xFF5B9EE1).copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                radius = size.maxDimension
                            ),
                            radius = size.maxDimension
                        )
                    }
                    .background(Color(0xFF5B9EE1), shape = CircleShape)
                    .padding(14.dp)
                    .clickable(
                        interactionSource = cartInteraction,
                        indication = null
                    ) {
                        navController.navigate(cartItem.route)
                    }
            )

            Row(
                modifier = Modifier.padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.takeLast(2).forEach { item ->
                    val isSelected = currentRoute?.let { it == item.route }
                        ?: (item.route == BottomNavRoute.Home.route)

                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.label,
                        tint = if (isSelected) Color(0xFF5B9EE1) else Color(0xFF707B81),
                        modifier = Modifier
                            .padding(horizontal = 18.dp)
                            .size(26.dp)
                            .clickable(
                                interactionSource = profileInteraction,
                                indication = null
                            ) {
                                if (!isSelected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    NikeTheme {
        Main_Screen()
    }
}