package com.smartstay.application_mobile_frontend.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.smartstay.application_mobile_frontend.R
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartstay.application_mobile_frontend.core.datastore.TokenManager
import com.smartstay.application_mobile_frontend.feature.iam.presentation.changepassword.ChangePasswordScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.login.LoginScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.signup.SignUpScreen
import com.smartstay.application_mobile_frontend.feature.iam.presentation.userdetail.UserDetailScreen
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking

// ---------------------------------------------------------------------------
//  Constantes de rutas del módulo IAM
// ---------------------------------------------------------------------------

/**
 * Aloja todas las rutas de navegación del módulo IAM.
 */
object Routes {
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val DASHBOARD = "dashboard"
    const val USER_LIST = "user_list"

    /** Ruta con argumento dinámico [userId] de tipo Int. */
    const val USER_DETAIL = "user_detail/{userId}"

    /** Ruta con argumento dinámico [userId] de tipo Int. */
    const val EDIT_USER = "edit_user/{userId}"

    const val CHANGE_PASSWORD = "change_password"

    /** Construye la ruta concreta para [UserDetail] dado un [userId]. */
    fun userDetail(userId: Int): String = "user_detail/$userId"

    /** Construye la ruta concreta para [EditUser] dado un [userId]. */
    fun editUser(userId: Int): String = "edit_user/$userId"
}

// ---------------------------------------------------------------------------
//  Nombres de argumentos de navegación
// ---------------------------------------------------------------------------

/**
 * Aloja los nombres de los argumentos de navegación utilizados en las rutas IAM.
 */
object NavArgs {
    const val USER_ID = "userId"
}

// ---------------------------------------------------------------------------
//  EntryPoint para acceso a TokenManager desde Composable
// ---------------------------------------------------------------------------

/**
 * Punto de entrada Hilt que expone [TokenManager] al grafo de navegación
 * sin necesidad de pasarlo como parámetro explícito.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface SmartStayNavGraphEntryPoint {
    val tokenManager: TokenManager
}

// ---------------------------------------------------------------------------
//  Grafo de navegación principal
// ---------------------------------------------------------------------------

/**
 * Grafo de navegación principal de SmartStay.
 *
 * Decide el destino inicial en función de la existencia de un token JWT
 * almacenado en [TokenManager]:
 *   - Si hay token → [Routes.DASHBOARD]
 *   - Si no hay token → [Routes.LOGIN]
 *
 * @param navController Controlador de navegación proporcionado por la Activity.
 */
@Composable
fun SmartStayNavGraph(navController: NavHostController) {
    val context = LocalContext.current

    // Acceso a TokenManager vía Hilt EntryPoint
    val tokenManager: TokenManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SmartStayNavGraphEntryPoint::class.java
        ).tokenManager
    }

    // Determinar destino inicial según existencia de token
    val startDestination: String = remember {
        val hasToken = runBlocking { tokenManager.getToken() != null }
        if (hasToken) Routes.DASHBOARD else Routes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ---- Login ----
        composable(route = Routes.LOGIN) {
            LoginScreen(navController = navController)
        }

        // ---- Sign Up ----
        composable(route = Routes.SIGN_UP) {
            SignUpScreen(navController = navController)
        }

        // ---- Dashboard (post‑login, placeholder para Fase 2) ----
        composable(route = Routes.DASHBOARD) {
            Scaffold { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.nav_dashboard_title),
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Button(
                        onClick = {
                            val userId = runBlocking { tokenManager.getUserId() }
                            if (userId != null) {
                                navController.navigate(Routes.userDetail(userId))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.nav_my_profile_button))
                    }

                    Button(
                        onClick = { navController.navigate(Routes.USER_LIST) },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.nav_user_list_button))
                    }

                    Button(
                        onClick = {
                            navController.navigate(Routes.userDetail(userId = 1))
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = stringResource(R.string.nav_user_detail_button))
                    }

                    Button(
                        onClick = { navController.navigate(Routes.CHANGE_PASSWORD) },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = stringResource(R.string.nav_change_password_button))
                    }

                    Button(
                        onClick = {
                            runBlocking { tokenManager.clearSession() }
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(top = 24.dp)
                    ) {
                        Text(text = stringResource(R.string.nav_logout_button))
                    }
                }
            }
        }

        // ---- Lista de usuarios (placeholder para Fase 2) ----
        composable(route = Routes.USER_LIST) {
            Scaffold { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.nav_user_list_placeholder),
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Button(
                        onClick = {
                            navController.navigate(Routes.userDetail(userId = 1))
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.nav_view_user_button))
                    }

                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = stringResource(R.string.nav_back_button))
                    }
                }
            }
        }

        // ---- Detalle de usuario ----
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

        // ---- Edición de usuario (placeholder para Fase 2, con argumento userId) ----
        composable(
            route = Routes.EDIT_USER,
            arguments = listOf(navArgument(NavArgs.USER_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt(NavArgs.USER_ID) ?: 0
            Scaffold { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.nav_edit_user_placeholder),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Editing User ID: $userId",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.nav_back_button))
                    }
                }
            }
        }

        // ---- Cambio de contraseña ----
        composable(route = Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(navController = navController)
        }
    }
}
