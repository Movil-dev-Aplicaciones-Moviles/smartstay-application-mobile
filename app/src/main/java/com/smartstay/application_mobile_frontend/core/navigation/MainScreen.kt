package com.smartstay.application_mobile_frontend.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.UserPermissions
import com.smartstay.application_mobile_frontend.feature.iam.presentation.userlist.UserListScreen
import com.smartstay.application_mobile_frontend.feature.profile.presentation.list.ProfileListScreen
import com.smartstay.application_mobile_frontend.feature.profile.presentation.detail.ProfileDetailScreen
import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.HotelListScreen
import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.HotelListViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartstay.application_mobile_frontend.feature.options.presentation.OptionsScreen
import com.smartstay.application_mobile_frontend.feature.options.presentation.OptionsViewModel

sealed class TabItem(val route: String, val label: String, val icon: ImageVector) {
    object Personnel : TabItem(Routes.USER_LIST, "Personal", Icons.Default.People)
    object EmployeeProfiles : TabItem(Routes.PROFILE_LIST, "Fichas", Icons.Default.Badge)
    object Explore : TabItem(Routes.DASHBOARD, "Explorar", Icons.Default.Explore)
    object Management : TabItem(Routes.DASHBOARD, "Gestión", Icons.Default.Explore)
    object MyProfile : TabItem("my_profile_tab", "Perfil", Icons.Default.Person)
}

@Composable
fun MainScreen(
    rootNavController: NavHostController,
    role: String,
    currentUserId: Int
) {
    val navController = rememberNavController()
    val userRole = role.lowercase().trim()
    val permissions = remember(userRole) { UserPermissions(userRole) }

    val tabs = remember(userRole) {
        if (permissions.canManageUsers) {
            listOf(TabItem.Personnel, TabItem.EmployeeProfiles, TabItem.Management, TabItem.MyProfile)
        } else {
            listOf(TabItem.Explore, TabItem.MyProfile)
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (permissions.canManageUsers) Routes.USER_LIST else TabItem.Explore.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.USER_LIST) {
                UserListScreen(navController = rootNavController)
            }
            composable(Routes.PROFILE_LIST) {
                ProfileListScreen(navController = rootNavController)
            }
            composable(Routes.DASHBOARD) {
                val hotelListViewModel: HotelListViewModel = hiltViewModel()
                val uiState by hotelListViewModel.uiState.collectAsState()
                HotelListScreen(
                    uiState = uiState,
                    role = userRole,
                    onRefresh = hotelListViewModel::fetchAllHotels,
                    onLogout = {
                        hotelListViewModel.logout(onSuccess = {
                            rootNavController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        })
                    },
                    onHotelSelected = { hotelId ->
                        rootNavController.navigate("room_list/$hotelId")
                    },
                    onEditHotelSelected = { hotelId ->
                        rootNavController.navigate("edit_hotel/$hotelId")
                    },
                    onAddHotel = {
                        rootNavController.navigate(Routes.ADD_HOTEL)
                    },
                    onAddRoom = { hotelId ->
                        rootNavController.navigate("add_room/$hotelId")
                    },
                    onNavigateToOptions = {
                        rootNavController.navigate(Routes.ACCOMMODATION_OPTIONS)
                    }
                )
            }
            composable(TabItem.MyProfile.route) {
                ProfileDetailScreen(
                    navController = rootNavController,
                    profileId = currentUserId
                )
            }
        }
    }
}
