package com.example.nike.navigation

sealed class BottomNavRoute(val route: String) {
    object Home : BottomNavRoute("home")
    object Favourites : BottomNavRoute("favourites")
    object Cart : BottomNavRoute("cart")
    object Notification : BottomNavRoute("notification")
    object Profile : BottomNavRoute("profile")
    object Search : BottomNavRoute("search")
}
