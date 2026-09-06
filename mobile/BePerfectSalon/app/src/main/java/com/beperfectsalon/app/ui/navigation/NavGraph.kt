package com.beperfectsalon.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.beperfectsalon.app.ui.screens.about.AboutScreen
import com.beperfectsalon.app.ui.screens.auth.LoginScreen
import com.beperfectsalon.app.ui.screens.auth.SignupScreen
import com.beperfectsalon.app.ui.screens.booking.BookAppointmentScreen
import com.beperfectsalon.app.ui.screens.gallery.GalleryScreen
import com.beperfectsalon.app.ui.screens.home.HomeScreen
import com.beperfectsalon.app.ui.screens.mybookings.MyBookingsScreen
import com.beperfectsalon.app.ui.screens.offers.OffersScreen
import com.beperfectsalon.app.ui.screens.profile.ProfileScreen
import com.beperfectsalon.app.ui.screens.services.ServiceDetailScreen
import com.beperfectsalon.app.ui.screens.services.ServicesScreen
import com.beperfectsalon.app.ui.screens.splash.SplashScreen

private const val MAIN_ROUTE = "main"

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigate = { loggedIn ->
                val destination = if (loggedIn) MAIN_ROUTE else Screen.Login.route
                navController.navigate(destination) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(MAIN_ROUTE) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onGoToSignup = { navController.navigate(Screen.Signup.route) },
                onContinueAsGuest = {
                    navController.navigate(MAIN_ROUTE) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                onSignedUp = {
                    navController.navigate(MAIN_ROUTE) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onGoToLogin = { navController.popBackStack() },
            )
        }

        composable(MAIN_ROUTE) {
            MainScaffold(
                onOpenService = { serviceId -> navController.navigate(Screen.ServiceDetail.createRoute(serviceId)) },
                onProceedToBooking = { navController.navigate(Screen.BookAppointment.route) },
                onOpenGallery = { navController.navigate(Screen.Gallery.route) },
                onOpenAbout = { navController.navigate(Screen.About.route) },
                onLoginRequired = {
                    navController.navigate(Screen.Login.route) { popUpTo(MAIN_ROUTE) { inclusive = true } }
                },
                onLoggedOut = {
                    navController.navigate(Screen.Login.route) { popUpTo(MAIN_ROUTE) { inclusive = true } }
                },
            )
        }

        composable(
            route = Screen.ServiceDetail.route,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId").orEmpty()
            ServiceDetailScreen(
                serviceId = serviceId,
                onProceedToBooking = { navController.navigate(Screen.BookAppointment.route) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.BookAppointment.route) {
            BookAppointmentScreen(
                onBookingConfirmed = {
                    navController.navigate(MAIN_ROUTE) { popUpTo(MAIN_ROUTE) { inclusive = true } }
                },
            )
        }

        composable(Screen.Gallery.route) {
            GalleryScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}

/** Bottom-nav shell for the five main tabs, with its own nested NavHost. */
@Composable
private fun MainScaffold(
    onOpenService: (String) -> Unit,
    onProceedToBooking: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenAbout: () -> Unit,
    onLoginRequired: () -> Unit,
    onLoggedOut: () -> Unit,
) {
    val tabNavController = rememberNavController()

    Scaffold(
        bottomBar = { SalonBottomBar(tabNavController) },
    ) { padding ->
        NavHost(
            navController = tabNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onBookNow = { tabNavController.navigateToTab(Screen.Services.route) },
                    onSeeAllServices = { tabNavController.navigateToTab(Screen.Services.route) },
                    onOpenService = onOpenService,
                    onOpenGallery = onOpenGallery,
                    onOpenAbout = onOpenAbout,
                )
            }
            composable(Screen.Services.route) {
                ServicesScreen(onOpenService = onOpenService, onProceedToBooking = onProceedToBooking)
            }
            composable(Screen.MyBookings.route) {
                MyBookingsScreen(onLoginRequired = onLoginRequired)
            }
            composable(Screen.Offers.route) {
                OffersScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(onLoggedOut = onLoggedOut, onOpenAbout = onOpenAbout, onLoginRequired = onLoginRequired)
            }
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun SalonBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = { navController.navigateToTab(item.screen.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
            )
        }
    }
}
