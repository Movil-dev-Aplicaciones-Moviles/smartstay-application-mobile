package com.smartstay.application_mobile_frontend.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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
import com.smartstay.application_mobile_frontend.feature.profile.presentation.create.CreateProfileScreen
import com.smartstay.application_mobile_frontend.feature.profile.presentation.detail.ProfileDetailScreen
import com.smartstay.application_mobile_frontend.feature.profile.presentation.list.ProfileListScreen
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking

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
    const val CREATE_PROFILE = "create_profile"
    const val PROFILE_DETAIL = "profile_detail/{profileId}"

    /** Construye la ruta concreta para el detalle de usuario dado su [userId]. */
    fun userDetail(userId: Int): String = "user_detail/$userId"

    /** Construye la ruta concreta para la edición parcial de un usuario dado su [userId]. */
    fun editUser(userId: Int): String = "edit_user/$userId"
    fun profileDetail(profileId: Int): String = "profile_detail/$profileId"
}

// ---------------------------------------------------------------------------
//  Nombres de argumentos de navegación
// ---------------------------------------------------------------------------

object NavArgs {
    const val USER_ID = "userId"
    const val PROFILE_ID = "profileId"
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
fun SmartStayNavGraph(navController: NavHostController) {
    val context = LocalContext.current

    // Acceso síncrono al EntryPoint de inyección para TokenManager
    val tokenManager: TokenManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SmartStayNavGraphEntryPoint::class.java
        ).tokenManager
    }

    // Determinar destino inicial evitando flujos o placeholders intermedios roto
    val startDestination: String = remember {
        val hasToken = runBlocking { tokenManager.getToken() != null }
        val role = runBlocking { tokenManager.getRole() } ?: ""

        if (hasToken) {
            val permissions = UserPermissions(role)
            if (permissions.canManageUsers) {
                Routes.USER_LIST
            } else {
                Routes.PROFILE_DETAIL
            }
        } else {
            Routes.LOGIN
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

        // ---- Lista Operacional de Personal ----
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

        //Profile
        composable(route = Routes.PROFILE_LIST) {
            ProfileListScreen(navController = navController)
        }
        composable(route = Routes.CREATE_PROFILE) {
            CreateProfileScreen(navController = navController)
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
    }
}