// core/navigation/SmartStayNavGraph.kt
package com.smartstay.application_mobile_frontend.core.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartstay.application_mobile_frontend.PaymentReturnData
import com.smartstay.application_mobile_frontend.core.datastore.TokenManager
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.UserPermissions
import com.smartstay.application_mobile_frontend.feature.iam.presentation.changepassword.ChangePasswordScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.createuser.CreateUserScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.edituser.EditUserScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.login.LoginScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.userdetail.UserDetailScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.userlist.UserListScreen
import com.smartstay.application_mobile_frontend.feature.payments.presentation.screens.PaymentCheckoutDemoScreen
import com.smartstay.application_mobile_frontend.feature.payments.presentation.screens.PaymentReturnScreen
import com.smartstay.application_mobile_frontend.feature.payments.presentation.viewmodel.PaymentCheckoutViewModel
import com.smartstay.application_mobile_frontend.feature.payments.presentation.viewmodel.PaymentReturnViewModel
import com.smartstay.application_mobile_frontend.feature.profile.presentation.create.CreateProfileScreen
import com.smartstay.application_mobile_frontend.feature.profile.presentation.detail.ProfileDetailScreen
import com.smartstay.application_mobile_frontend.feature.profile.presentation.list.ProfileListScreen
import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.admin.AddHotelScreen
import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.admin.EditHotelScreen
import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.admin.AddRoomScreen
import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.RoomListScreen
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import androidx.navigation.compose.rememberNavController
import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.HotelListScreen

import com.smartstay.application_mobile_frontend.feature.accommodation.presentation.HotelListViewModel
//import com.smartstay.application_mobile_frontend.feature.iam.presentation.screens.SignInScreen
//import com.smartstay.application_mobile_frontend.feature.iam.presentation.viewmodel.IamViewModel
//import com.smartstay.application_mobile_frontend.feature.options.presentation.HotelListScreen
import com.smartstay.application_mobile_frontend.feature.options.presentation.OptionsScreen
import com.smartstay.application_mobile_frontend.feature.options.presentation.OptionsViewModel

// ---------------------------------------------------------------------------
//  Constantes de rutas del módulo IAM (Operacional y Administrativo)
// ---------------------------------------------------------------------------

/**
 * Aloja todas las rutas de navegación reales y estancas del módulo IAM.
 */
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
    const val PAYMENT_RETURN = "payments/return/{status}?bookingId={bookingId}&amount={amount}"
    const val ADD_HOTEL = "add_hotel"
    const val EDIT_HOTEL = "edit_hotel/{hotelId}"
    const val ADD_ROOM = "add_room/{hotelId}"
    const val ROOM_LIST = "room_list/{hotelId}?hotelName={hotelName}"
    const val PAYMENT_CHECKOUT =
        "payments/checkout?bookingId={bookingId}&hotelId={hotelId}&roomId={roomId}&amount={amount}&hotelName={hotelName}"

    const val ACCOMMODATION_OPTIONS = "accommodation_options"

    const val DASHBOARD = "dashboard"
    const val MAIN = "main"

    /** Construye la ruta concreta para el detalle de usuario dado su [userId]. */
    fun userDetail(userId: Int): String = "user_detail/$userId"

    /** Construye la ruta concreta para la edición parcial de un usuario dado su [userId]. */
    fun editUser(userId: Int): String = "edit_user/$userId"
    fun profileDetail(profileId: Int): String = "profile_detail/$profileId"

    fun createProfile(email: String): String = "create_profile/$email"

    fun paymentReturn(status: String, bookingId: Int? = null, amount: Double? = null): String {
        return "payments/return/$status" +
            "?bookingId=${bookingId ?: -1}" +
            "&amount=${amount ?: -1.0}"
    }

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

// ---------------------------------------------------------------------------
//  Nombres de argumentos de navegación
// ---------------------------------------------------------------------------


object NavArgs {
    const val USER_ID = "userId"
    const val PROFILE_ID = "profileId"
    const val BOOKING_ID = "bookingId"
    const val HOTEL_ID = "hotelId"
    const val ROOM_ID = "roomId"
    const val AMOUNT = "amount"
    const val HOTEL_NAME = "hotelName"
    const val PAYMENT_STATUS = "status"
}

// ---------------------------------------------------------------------------
//  EntryPoint para acceso a TokenManager desde el grafo
// ---------------------------------------------------------------------------

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SmartStayNavGraphEntryPoint {
    val tokenManager: TokenManager
}

// ---------------------------------------------------------------------------
//  Grafo de navegación principal - SmartStay Operacional
// ---------------------------------------------------------------------------

/**
 * Grafo de navegación principal de la aplicación administrativa interna.
 *
 * Decide el destino inicial según la existencia de una sesión persistida:
 * - Si hay token → Redirección directa a la zona de trabajo: [Routes.USER_LIST]
 * - Si no hay token → [Routes.LOGIN]
 */
@Composable
fun SmartStayNavGraph(
    navController: NavHostController,
    paymentReturnData: PaymentReturnData? = null,
    onPaymentReturnConsumed: () -> Unit = {}
) {
    val context = LocalContext.current

    // Acceso síncrono al EntryPoint de inyección para TokenManager
    val tokenManager: TokenManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SmartStayNavGraphEntryPoint::class.java
        ).tokenManager
    }

    val startDestination: String = remember {
        val hasToken = runBlocking { tokenManager.getToken() != null }
        if (hasToken) Routes.MAIN else Routes.LOGIN
    }

    LaunchedEffect(paymentReturnData) {
        paymentReturnData?.let { data ->
            navController.navigate(
                Routes.paymentReturn(
                    status = data.status,
                    bookingId = data.bookingId,
                    amount = data.amount
                )
            ) {
                launchSingleTop = true
            }
            onPaymentReturnConsumed()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ---- Login ----
        composable(route = Routes.LOGIN) {
            LoginScreen(navController = navController)
        }

        // ---- CONTENEDOR PRINCIPAL CON TABS ----
        composable(route = Routes.MAIN) {
            val role = remember { runBlocking { tokenManager.getRole() } ?: "" }
            val currentUserId = remember { runBlocking { tokenManager.getUserId() } ?: 0 }

            MainScreen(
                rootNavController = navController,
                role = role,
                currentUserId = currentUserId
            )
        }

        // ---- Lista Operacional de Personal (Mantenida para navegación directa si fuera necesario) ----
        composable(route = Routes.USER_LIST) {
            UserListScreen(navController = navController)
        }

        // ---- Formulario Administrativo: Registro de Personal ----
        composable(route = Routes.CREATE_USER) {
            CreateUserScreen(navController = navController)
        }

        // ---- Detalle de Identidades y Gestión Contextual ----
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
        composable(route = Routes.DASHBOARD) {
            val hotelListViewModel: HotelListViewModel = hiltViewModel()
            val uiState by hotelListViewModel.uiState.collectAsState()
            val role = remember { runBlocking { tokenManager.getRole() } ?: "" }

            HotelListScreen(
                uiState = uiState,
                role = role,
                onRefresh = hotelListViewModel::fetchAllHotels,
                onLogout = {
                    hotelListViewModel.logout(onSuccess = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    })
                },
                onHotelSelected = { hotelId ->
                    navController.navigate("room_list/$hotelId")
                },
                onEditHotelSelected = { hotelId ->
                    navController.navigate("edit_hotel/$hotelId")
                },
                onAddHotel = {
                    navController.navigate(Routes.ADD_HOTEL)
                },
                onAddRoom = { hotelId ->
                    navController.navigate("add_room/$hotelId")
                },
                onNavigateToOptions = {
                    navController.navigate(Routes.ACCOMMODATION_OPTIONS)
                }
            )
        }


        // ---- Formulario de Edición Parcial de Cuentas ----
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

        // ---- Cambio Autónomo de Contraseña ----
        composable(route = Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(navController = navController)
        }

        // Nuevo Bounded Context conectado a la API
        composable("accommodation_options") {
            val optionsViewModel: OptionsViewModel = hiltViewModel()
            OptionsScreen(viewModel = optionsViewModel)
        }

        //Profile
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

        composable(route = Routes.ADD_HOTEL) {
            AddHotelScreen(navController = navController)
        }

        composable(
            route = Routes.EDIT_HOTEL,
            arguments = listOf(navArgument("hotelId") { type = NavType.IntType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            EditHotelScreen(navController = navController, hotelId = hotelId)
        }

        composable(
            route = Routes.ADD_ROOM,
            arguments = listOf(navArgument("hotelId") { type = NavType.IntType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            AddRoomScreen(navController = navController, hotelId = hotelId)
        }

        composable(
            route = Routes.ROOM_LIST,
            arguments = listOf(
                navArgument("hotelId") { type = NavType.IntType },
                navArgument("hotelName") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            val hotelName = backStackEntry.arguments?.getString("hotelName")
            RoomListScreen(
                navController = navController,
                hotelId = hotelId,
                hotelName = hotelName,
                onRoomSelected = { room ->
                    navController.navigate(
                        Routes.paymentCheckout(
                            hotelId = room.hotelId,
                            roomId = room.id,
                            amount = room.price,
                            hotelName = hotelName
                        )
                    )
                }
            )
        }

        composable(
            route = Routes.PAYMENT_RETURN,
            arguments = listOf(
                navArgument(NavArgs.PAYMENT_STATUS) { type = NavType.StringType },
                navArgument(NavArgs.BOOKING_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument(NavArgs.AMOUNT) {
                    type = NavType.FloatType
                    defaultValue = -1f
                }
            )
        ) { backStackEntry ->
            val status = backStackEntry.arguments?.getString(NavArgs.PAYMENT_STATUS).orEmpty()
            val bookingId = backStackEntry.arguments?.getInt(NavArgs.BOOKING_ID).toNullableId()
            val amount = backStackEntry.arguments
                ?.getFloat(NavArgs.AMOUNT)
                ?.takeIf { it >= 0f }
                ?.toDouble()
            val paymentReturnViewModel: PaymentReturnViewModel = hiltViewModel()

            PaymentReturnScreen(
                status = status,
                bookingId = bookingId,
                amount = amount,
                viewModel = paymentReturnViewModel,
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
