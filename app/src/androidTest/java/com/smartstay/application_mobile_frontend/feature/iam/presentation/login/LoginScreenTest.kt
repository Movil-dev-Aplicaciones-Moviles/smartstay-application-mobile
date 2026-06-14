package com.smartstay.application_mobile_frontend.feature.iam.presentation.login

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smartstay.application_mobile_frontend.core.navigation.Routes
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.AssignRoleRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.ChangePasswordRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.CreateUserRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.SignInRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.SignUpRequest
import com.smartstay.application_mobile_frontend.feature.iam.data.dto.UpdateUserRequest
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.AuthenticatedUser
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas instrumentadas de UI para [LoginScreen].
 *
 * Verifican la presencia de campos, botones, navegación entre pantallas
 * y respuesta a los distintos estados del ViewModel de login.
 */
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---------------------------------------------------------------------------
    // Fake del repositorio IAM
    // ---------------------------------------------------------------------------

    /**
     * Implementación falsa de [IamRepository] que permite controlar el resultado
     * de [signIn] y registrar si fue invocado, sin depender de Hilt ni de la red.
     */
    private class FakeIamRepository : IamRepository {

        /** Resultado que devolverá (o lanzará) la siguiente llamada a [signIn]. */
        var signInResult: Result<AuthenticatedUser> = Result.success(
            AuthenticatedUser(
                token = "fake-jwt-token",
                user = User(
                    id = 1,
                    username = "testuser",
                    role = "Guest",
                    status = "Active"
                )
            )
        )

        var signInCalled = false
            private set

        override suspend fun signIn(request: SignInRequest): AuthenticatedUser {
            signInCalled = true
            return signInResult.getOrThrow()
        }

        // ── Métodos no usados en los tests de login ──────────────────────────
        override suspend fun signUp(request: SignUpRequest): AuthenticatedUser =
            throw UnsupportedOperationException()

        override suspend fun changePassword(request: ChangePasswordRequest) =
            throw UnsupportedOperationException()

        override suspend fun getUsers(): List<User> =
            throw UnsupportedOperationException()

        override suspend fun getUserById(userId: Int): User =
            throw UnsupportedOperationException()

        override suspend fun createUser(request: CreateUserRequest): User =
            throw UnsupportedOperationException()

        override suspend fun updateUser(userId: Int, request: UpdateUserRequest): User =
            throw UnsupportedOperationException()

        override suspend fun deactivateUser(userId: Int) =
            throw UnsupportedOperationException()

        override suspend fun assignRole(userId: Int, request: AssignRoleRequest) =
            throw UnsupportedOperationException()
    }

    // ---------------------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------------------

    /**
     * Test 1: La pantalla muestra los campos de usuario y contraseña.
     *
     * Verifica que los labels "Usuario" y "Contraseña" estén visibles
     * en la interfaz de login.
     */
    @Test
    fun screenShowsUsernameAndPasswordFields() {
        val viewModel = LoginViewModel(FakeIamRepository())

        composeTestRule.setContent {
            LoginScreen(
                navController = rememberNavController(),
                viewModel = viewModel
            )
        }

        composeTestRule
            .onNodeWithText("Usuario")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Contraseña")
            .assertIsDisplayed()
    }

    /**
     * Test 2: El botón "Ingresar" está presente y habilitado.
     *
     * En estado Idle (inicial) el botón debe estar visible y
     * habilitado para recibir interacción del usuario.
     */
    @Test
    fun loginButtonIsPresentAndEnabled() {
        val viewModel = LoginViewModel(FakeIamRepository())

        composeTestRule.setContent {
            LoginScreen(
                navController = rememberNavController(),
                viewModel = viewModel
            )
        }

        composeTestRule
            .onNodeWithText("Ingresar")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    /**
     * Test 3: El enlace "Regístrate" navega a la pantalla de registro.
     *
     * Al hacer clic en "¿No tienes cuenta? Regístrate", la ruta actual
     * del NavHost debe cambiar a [Routes.SIGN_UP].
     */
    @Test
    fun registerLinkNavigatesToSignUp() {
        val viewModel = LoginViewModel(FakeIamRepository())
        lateinit var navController: NavHostController

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Routes.LOGIN
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(navController = navController, viewModel = viewModel)
                }
                composable(Routes.SIGN_UP) {
                    Text("Crear Cuenta")
                }
            }
        }

        composeTestRule
            .onNodeWithText("¿No tienes cuenta? Regístrate")
            .performClick()

        composeTestRule.waitForIdle()

        assertEquals(
            Routes.SIGN_UP,
            navController.currentBackStackEntry?.destination?.route
        )
    }

    /**
     * Test 4: El estado [LoginUiState.Success] dispara navegación al Dashboard.
     *
     * Cuando el ViewModel emite Success tras un signIn exitoso, la pantalla
     * debe navegar automáticamente a [Routes.DASHBOARD].
     */
    @Test
    fun successStateNavigatesToDashboard() {
        val fakeRepo = FakeIamRepository()
        val viewModel = LoginViewModel(fakeRepo)
        lateinit var navController: NavHostController

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Routes.LOGIN
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(navController = navController, viewModel = viewModel)
                }
                composable(Routes.DASHBOARD) {
                    Text("Dashboard")
                }
            }
        }

        // Llenar campos con credenciales válidas
        val textFields = composeTestRule.onAllNodes(hasSetTextAction())
        textFields[0].performTextInput("admin")
        textFields[1].performTextInput("secret123")

        // Disparar signIn
        composeTestRule.onNodeWithText("Ingresar").performClick()

        // Esperar a que el ViewModel procese el signIn y se ejecute la navegación
        composeTestRule.waitForIdle()

        assertEquals(
            Routes.DASHBOARD,
            navController.currentBackStackEntry?.destination?.route
        )
    }

    /**
     * Test 5: El estado [LoginUiState.Error] muestra un Snackbar con el mensaje de error.
     *
     * Cuando el repositorio lanza una excepción, el ViewModel emite Error y
     * la pantalla debe mostrar un Snackbar con el texto del error.
     */
    @Test
    fun errorStateShowsSnackbar() {
        val fakeRepo = FakeIamRepository().apply {
            signInResult = Result.failure(
                RuntimeException("Credenciales inválidas")
            )
        }
        val viewModel = LoginViewModel(fakeRepo)

        composeTestRule.setContent {
            LoginScreen(
                navController = rememberNavController(),
                viewModel = viewModel
            )
        }

        // Llenar campos para que el botón llame a signIn
        val textFields = composeTestRule.onAllNodes(hasSetTextAction())
        textFields[0].performTextInput("mal_user")
        textFields[1].performTextInput("mal_pass")

        composeTestRule.onNodeWithText("Ingresar").performClick()

        // Esperar a que el error se procese y el Snackbar aparezca
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Credenciales inválidas")
            .assertIsDisplayed()
    }

    /**
     * Test 6: Campos vacíos no invocan signIn en el ViewModel.
     *
     * Al presionar "Ingresar" sin haber llenado los campos, el ViewModel
     * no debe recibir la llamada a signIn.
     */
    @Test
    fun emptyFieldsDoNotCallSignIn() {
        val fakeRepo = FakeIamRepository()
        val viewModel = LoginViewModel(fakeRepo)

        composeTestRule.setContent {
            LoginScreen(
                navController = rememberNavController(),
                viewModel = viewModel
            )
        }

        // Hacer clic sin llenar ningún campo
        composeTestRule.onNodeWithText("Ingresar").performClick()

        composeTestRule.waitForIdle()

        assertFalse(
            "signIn no debe ser llamado cuando los campos están vacíos",
            fakeRepo.signInCalled
        )
    }
}
