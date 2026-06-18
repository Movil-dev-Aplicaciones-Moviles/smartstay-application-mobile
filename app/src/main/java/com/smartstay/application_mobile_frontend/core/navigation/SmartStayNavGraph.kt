package com.smartstay.application_mobile_frontend.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartstay.application_mobile_frontend.core.datastore.TokenManager
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.UserPermissions
import com.smartstay.application_mobile_frontend.feature.iam.presentation.changepassword.ChangePasswordScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.createuser.CreateUserScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.edituser.EditUserScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.login.LoginScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.userdetail.UserDetailScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.userlist.UserListScreen
import com.smartstay.application_mobile_frontend.feature.payments.presentation.screens.PaymentCheckoutDemoScreen
import com.smartstay.application_mobile_frontend.feature.payments.presentation.viewmodel.PaymentCheckoutViewModel
import com.smartstay.application_mobile_frontend.feature.profile.presentation.create.CreateProfileScreen
import com.smartstay.application_mobile_frontend.feature.profile.presentation.detail.ProfileDetailScreen
import com.smartstay.application_mobile_frontend.feature.profile.presentation.list.ProfileListScreen
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking

object Routes {
    const val LOGIN = "login"
    const val USER_LIST = "user_list"
    const val CREATE_USER = "create_user"
    const val USER_DETAIL = "user_detail/{userId}"
    const val EDIT_USER = "edit_user/{userId}"
    const val CHANGE_PASSWORD = "change_password"
    const val PROFILE_LIST = "profile_list"
    const val CREATE_PROFILE = "create_profile/{userEmail}"
    const val PROFILE_DETAIL = "profile_detail/{profileId}"
    const val PAYMENT_DEMO = "payments/demo"
    const val PAYMENT_CHECKOUT =
        "payments/checkout?bookingId={bookingId}&hotelId={hotelId}&roomId={roomId}&amount={amount}&hotelName={hotelName}"

    fun userDetail(userId: Int): String = "user_detail/$userId"

    fun editUser(userId: Int): String = "edit_user/$userId"
    fun profileDetail(profileId: Int): String = "profile_detail/$profileId"

    fun createProfile(email: String): String = "create_profile/$email"

    fun paymentCheckout(
        bookingId: Int? = null,
        hotelId: Int? = null,
        roomId: Int? = null,
        amount: Double? = null,
        hotelName: String? = null
    ): String {
        val encodedHotelName = hotelName.orEmpty().replace(" ", "%20")
        return "payments/checkout" +
            "?bookingId=${bookingId ?: -1}" +
            "&hotelId=${hotelId ?: -1}" +
            "&roomId=${roomId ?: -1}" +
            "&amount=${amount ?: -1.0}" +
            "&hotelName=$encodedHotelName"
    }
}

object NavArgs {
    const val USER_ID = "userId"
    const val PROFILE_ID = "profileId"
    const val BOOKING_ID = "bookingId"
    const val HOTEL_ID = "hotelId"
    const val ROOM_ID = "roomId"
    const val AMOUNT = "amount"
    const val HOTEL_NAME = "hotelName"
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SmartStayNavGraphEntryPoint {
    val tokenManager: TokenManager
}

@Composable
fun SmartStayNavGraph(navController: NavHostController) {
    val context = LocalContext.current

    val tokenManager: TokenManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SmartStayNavGraphEntryPoint::class.java
        ).tokenManager
    }

    val startDestination: String = remember {
        val hasToken = runBlocking { tokenManager.getToken() != null }
        val role = runBlocking { tokenManager.getRole() } ?: ""
        val currentUserId = runBlocking { tokenManager.getUserId() } ?: 0

        if (hasToken) {
            val permissions = UserPermissions(role)
            if (permissions.canManageUsers) {
                Routes.USER_LIST
            } else {
                Routes.profileDetail(currentUserId)
            }
        } else {
            Routes.LOGIN
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Routes.LOGIN) {
            LoginScreen(navController = navController)
        }

        composable(route = Routes.USER_LIST) {
            UserListScreen(navController = navController)
        }

        composable(route = Routes.CREATE_USER) {
            CreateUserScreen(navController = navController)
        }

        composable(
            route = Routes.USER_DETAIL,
            arguments = listOf(navArgument(NavArgs.USER_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt(NavArgs.USER_ID) ?: return@composable
            val actorRole = remember {
                runBlocking { tokenManager.getRole() } ?: ""
            }
            UserDetailScreen(
                navController = navController,
                userId = userId,
                actorRole = actorRole
            )
        }

        composable(
            route = Routes.EDIT_USER,
            arguments = listOf(navArgument(NavArgs.USER_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt(NavArgs.USER_ID) ?: return@composable
            EditUserScreen(
                navController = navController,
                userId = userId
            )
        }

        composable(route = Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(navController = navController)
        }

        composable(route = Routes.PROFILE_LIST) {
            ProfileListScreen(navController = navController)
        }

        composable(
            route = Routes.CREATE_PROFILE,
            arguments = listOf(navArgument("userEmail") { type = NavType.StringType })
        ) { backStackEntry ->
            val emailParam = backStackEntry.arguments?.getString("userEmail") ?: ""
            CreateProfileScreen(navController = navController, prefilledEmail = emailParam)
        }

        composable(
            route = Routes.PROFILE_DETAIL,
            arguments = listOf(navArgument(NavArgs.PROFILE_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getInt(NavArgs.PROFILE_ID) ?: 0
            ProfileDetailScreen(
                navController = navController,
                profileId = profileId
            )
        }

        composable(route = Routes.PAYMENT_DEMO) {
            val paymentCheckoutViewModel: PaymentCheckoutViewModel = hiltViewModel()

            PaymentCheckoutDemoScreen(
                viewModel = paymentCheckoutViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.PAYMENT_CHECKOUT,
            arguments = listOf(
                navArgument(NavArgs.BOOKING_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument(NavArgs.HOTEL_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument(NavArgs.ROOM_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument(NavArgs.AMOUNT) {
                    type = NavType.FloatType
                    defaultValue = -1f
                },
                navArgument(NavArgs.HOTEL_NAME) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val paymentCheckoutViewModel: PaymentCheckoutViewModel = hiltViewModel()
            val bookingId = backStackEntry.arguments?.getInt(NavArgs.BOOKING_ID).toNullableId()
            val hotelId = backStackEntry.arguments?.getInt(NavArgs.HOTEL_ID).toNullableId()
            val roomId = backStackEntry.arguments?.getInt(NavArgs.ROOM_ID).toNullableId()
            val amount = backStackEntry.arguments
                ?.getFloat(NavArgs.AMOUNT)
                ?.takeIf { it >= 0f }
                ?.toDouble()
            val hotelName = backStackEntry.arguments
                ?.getString(NavArgs.HOTEL_NAME)
                ?.takeIf { it.isNotBlank() }

            PaymentCheckoutDemoScreen(
                viewModel = paymentCheckoutViewModel,
                onBack = { navController.popBackStack() },
                bookingId = bookingId,
                hotelId = hotelId,
                roomId = roomId,
                amount = amount,
                hotelName = hotelName
            )
        }
    }
}

private fun Int?.toNullableId(): Int? = this?.takeIf { it > 0 }
