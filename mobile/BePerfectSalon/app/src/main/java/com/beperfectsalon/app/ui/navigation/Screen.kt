package com.beperfectsalon.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Signup : Screen("signup")

    data object Home : Screen("home")
    data object Services : Screen("services")
    data object MyBookings : Screen("my_bookings")
    data object Offers : Screen("offers")
    data object Profile : Screen("profile")

    data object Gallery : Screen("gallery")
    data object About : Screen("about")

    data object ServiceDetail : Screen("service_detail/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail/$serviceId"
    }

    data object BookAppointment : Screen("book_appointment")
}

data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.Services, "Services", Icons.Filled.Spa),
    BottomNavItem(Screen.MyBookings, "Bookings", Icons.Filled.CalendarMonth),
    BottomNavItem(Screen.Offers, "Offers", Icons.Filled.LocalOffer),
    BottomNavItem(Screen.Profile, "Profile", Icons.Filled.Person),
)
