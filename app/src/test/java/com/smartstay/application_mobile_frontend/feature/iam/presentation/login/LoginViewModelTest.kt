package com.smartstay.application_mobile_frontend.feature.iam.presentation.login

import com.smartstay.application_mobile_frontend.feature.iam.data.dto.SignInRequest
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.AuthenticatedUser
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.MockitoAnnotations
import retrofit2.HttpException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @Mock
    private lateinit var iamRepository: IamRepository

    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(iamRepository = iamRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signIn exitoso emite Success`() {
        // Given
        val user = User(
            id = 1,
            username = "testuser",
            role = "ChainAdmin",
            status = "Active"
        )
        val authenticatedUser = AuthenticatedUser(
            token = "jwt-test-token",
            user = user
        )
        `when`(iamRepository.signIn(any<SignInRequest>())).thenReturn(authenticatedUser)

        // When
        viewModel.signIn("testuser", "password123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(state is LoginUiState.Success) {
            "Expected Success but got ${state::class.simpleName}"
        }
        val successState = state as LoginUiState.Success
        assert(successState.authenticatedUser.token == "jwt-test-token") {
            "Expected token jwt-test-token but got ${successState.authenticatedUser.token}"
        }
        assert(successState.authenticatedUser.user.username == "testuser") {
            "Expected username testuser but got ${successState.authenticatedUser.user.username}"
        }
    }

    @Test
    fun `signIn fallido emite Error`() {
        // Given
        `when`(iamRepository.signIn(any<SignInRequest>())).thenThrow(HttpException::class.java)

        // When
        viewModel.signIn("testuser", "wrongpass")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assert(state is LoginUiState.Error) {
            "Expected Error but got ${state::class.simpleName}"
        }
    }

    @Test
    fun `signIn en curso emite Loading`() {
        // Given
        // `_uiState.value = LoginUiState.Loading` se ejecuta sincrónicamente
        // en el hilo llamante antes de lanzar la corrutina. Como usamos
        // StandardTestDispatcher, la corrutina dentro de viewModelScope.launch
        // no se ejecuta hasta advanceUntilIdle(). Por lo tanto, después de
        // llamar a signIn() el estado ya es Loading.

        // When
        viewModel.signIn("testuser", "password123")

        // Then — el estado se establece sincrónicamente antes del launch
        assert(viewModel.uiState.value is LoginUiState.Loading) {
            "Expected Loading but got ${viewModel.uiState.value::class.simpleName}"
        }
    }

    @Test
    fun `init establece estado Idle`() {
        // When — el ViewModel ya fue creado en setUp

        // Then
        val state = viewModel.uiState.value
        assert(state is LoginUiState.Idle) {
            "Expected Idle but got ${state::class.simpleName}"
        }
    }
}
