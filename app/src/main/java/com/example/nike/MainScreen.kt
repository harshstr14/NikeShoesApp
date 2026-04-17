package com.example.nike

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.nike.cartScreen.MyCartScreen
import com.example.nike.favouriteScreen.FavouriteScreen
import com.example.nike.googleAuthentication.GoogleSignInManager
import com.example.nike.homeScreen.HomeScreen
import com.example.nike.navigation.BottomItem
import com.example.nike.navigation.BottomNavRoute
import com.example.nike.notificationScreen.NotificationScreen
import com.example.nike.orderScreen.OrderScreen
import com.example.nike.profileScreen.ProfilePrefs
import com.example.nike.profileScreen.ProfileScreen
import com.example.nike.profileScreen.ProfileViewModel
import com.example.nike.screens.fonts
import com.example.nike.searchScreen.SearchScreen
import com.example.nike.ui.theme.NikeTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

private lateinit var googleSignInManager: GoogleSignInManager

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

        googleSignInManager = GoogleSignInManager(this)

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
    var showLogOutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .width(280.dp),
                drawerContainerColor = Color.Transparent
            ) {
                DrawerContent(navController, drawerState, onShowDialog = {
                    showLogOutDialog = true
                })
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
                                        data.visuals.message.contains("Profile") -> R.drawable.user
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
                    HomeScreen(navController, drawerState)
                }

                composable(BottomNavRoute.Favourites.route) {
                    FavouriteScreen(navController, snackBarHostState)
                }

                composable(BottomNavRoute.Notification.route) {
                    NotificationScreen(navController)
                }

                composable(BottomNavRoute.Profile.route) {
                    ProfileScreen(navController, snackBarHostState)
                }

                composable(BottomNavRoute.Search.route) {
                    SearchScreen(navController)
                }
            }

            Box(
                modifier = Modifier.fillMaxSize().zIndex(2f)
            ) {
                if(showLogOutDialog) {
                    IOSStyleBottomDialog(
                        title = "Log Out",
                        message = "Are you sure you want to log out? You will need to log in again.",
                        confirmText = "Log Out",
                        onConfirm = {
                            googleSignInManager.signOut {
                                Toast.makeText(context, "Signed out!", Toast.LENGTH_SHORT).show()
                            }

                            val intent = Intent(context, SignInScreen::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                            activity?.finish()

                            scope.launch {
                                ProfilePrefs.clear(context)
                            }
                            showLogOutDialog = false
                        },
                        onDismiss = {
                            showLogOutDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun IOSStyleBottomDialog(
    title: String,
    message: String,
    confirmText: String = "Delete",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 22.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF8F9FA),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = title,
                        fontFamily = fonts,
                        fontSize = 17.sp, lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF1A2530),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = message,
                        fontFamily = fonts,
                        fontSize = 13.sp, lineHeight = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Normal,
                        color = Color(0xFF707B81)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF707B81).copy(alpha = 0.2f))
                                .clickable { onDismiss() }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dismissText,
                                color = Color(0xFF707B81),
                                fontSize = 15.sp, lineHeight = 15.sp,
                                fontFamily = fonts, fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Normal
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF5B9EE1))
                                .clickable { onConfirm() }
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = confirmText,
                                fontSize = 15.sp, lineHeight = 15.sp,
                                fontFamily = fonts, fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Normal,
                                color = Color(0xFFFFFFFF)
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun DrawerContent(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: ProfileViewModel = viewModel(),
    onShowDialog: () -> Unit
) {
    val imageUrl by viewModel.profileImageUrl.collectAsStateWithLifecycle()
    val name by viewModel.userName.collectAsState()

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(uid) {
        uid?.let { viewModel.silentRefresh(it) }
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

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

        AsyncImage(
            model = imageUrl,
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.logo),
            error = painterResource(R.drawable.logo),
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
                text = name,
                color = Color(0xFFFFFFFF),
                fontSize = 20.sp,
                lineHeight = 22.sp,
                fontFamily = fonts,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        DrawerItem(
            title = "Profile",
            icon = R.drawable.user,
            isSelected = currentRoute == "profile"
        ) {
            scope.launch {
                drawerState.close()
            }

            if (currentRoute != "profile") {
                navController.navigate("profile") {
                    launchSingleTop = true
                }
            }
        }

        DrawerItem(
            title = "Home Page",
            icon = R.drawable.home,
            isSelected = currentRoute == "home"
        ) {
            scope.launch {
                drawerState.close()
            }

            if (currentRoute != "home") {
                navController.navigate("home") {
                    launchSingleTop = true
                }
            }
        }

        DrawerItem(
            title = "My Cart",
            icon = R.drawable.cart,
            isSelected = false
        ) {
            scope.launch {
                drawerState.close()
            }
            val intent = Intent(context, MyCartScreen::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            context.startActivity(intent)
        }

        DrawerItem(
            title = "Favorite",
            icon = R.drawable.favourite,
            isSelected = currentRoute == "favourites"
        ) {
            scope.launch {
                drawerState.close()
            }

            if (currentRoute != "favourites") {
                navController.navigate("favourites") {
                    launchSingleTop = true
                }
            }
        }

        DrawerItem(
            title = "Orders",
            icon = R.drawable.delivery_icon,
            isSelected = false
        ) {
            scope.launch {
                drawerState.close()
            }
            val intent = Intent(context, OrderScreen::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            context.startActivity(intent)
        }

        DrawerItem(
            title = "Notifications",
            icon = R.drawable.notification,
            isSelected = currentRoute == "notification"
        ) {
            scope.launch {
                drawerState.close()
            }

            if (currentRoute != "notification") {
                navController.navigate("notification") {
                    launchSingleTop = true
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
            thickness = 1.dp,
            color = Color(0xFF2D3B48)
        )

        Spacer(modifier = Modifier.height(12.dp))

        DrawerItem(
            title = "Sign Out",
            icon = R.drawable.signout_icon,
            isSelected = false
        ) {
            onShowDialog()
            scope.launch {
                drawerState.close()
            }
        }
    }
}

@Composable
fun DrawerItem(title: String, icon: Int, isSelected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    val textSize = if (isSelected) 17.sp else 15.sp
    val iconSize = if (isSelected) 26.dp else 24.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            painter = painterResource(icon), contentDescription = title,
            tint = Color(0xFF707B81)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = Color(0xFFFFFFFF),
            fontSize = textSize,
            lineHeight = 18.sp,
            fontFamily = fonts,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
        )
    }
}

@Composable
private fun BottomNavBar(navController: NavController) {
    val context = LocalContext.current
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
            .zIndex(1f)
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
                        val isSelected = currentRoute?.let { it == cartItem.route }
                            ?: (cartItem.route == BottomNavRoute.Home.route)

                        if (!isSelected) {
                            val intent = Intent(context, MyCartScreen::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            context.startActivity(intent)
                        }
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