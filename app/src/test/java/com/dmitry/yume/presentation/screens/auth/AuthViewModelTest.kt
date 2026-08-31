package com.dmitry.yume.presentation.screens.auth

import com.dmitry.yume.domain.models.SessionState
import com.dmitry.yume.domain.repository.AuthRepository
import com.dmitry.yume.domain.repository.AuthResult
import com.dmitry.yume.domain.repository.OperationResult
import com.dmitry.yume.domain.repository.SessionRefreshResult
import com.dmitry.yume.domain.usecase.LoginUseCase
import com.dmitry.yume.domain.usecase.RegisterUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `rapid double submit starts one login request`() = runTest(dispatcher) {
        val finishLogin = CompletableDeferred<AuthResult>()
        val loginCalls = AtomicInteger()
        val repository = FakeAuthRepository(
            login = { _, _ ->
                loginCalls.incrementAndGet()
                finishLogin.await()
            }
        )
        val viewModel = AuthViewModel(
            login = LoginUseCase(repository),
            register = RegisterUseCase(repository)
        )
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password")

        viewModel.submit()
        viewModel.submit()
        runCurrent()

        assertEquals(1, loginCalls.get())
        assertEquals(true, viewModel.state.value.isLoading)

        finishLogin.complete(AuthResult.Success)
        runCurrent()
    }

    private class FakeAuthRepository(
        private val login: suspend (String, String) -> AuthResult
    ) : AuthRepository {
        override val sessionState: Flow<SessionState> =
            flowOf(SessionState.Guest)

        override suspend fun register(
            email: String,
            password: String,
            displayName: String
        ): AuthResult = error("not used")

        override suspend fun login(email: String, password: String): AuthResult =
            login.invoke(email, password)

        override suspend fun sendCode(): OperationResult = error("not used")
        override suspend fun verifyEmail(code: String): AuthResult = error("not used")
        override suspend fun logout(): OperationResult = error("not used")
        override suspend fun logoutAll(): OperationResult = error("not used")
        override suspend fun validateSession() = Unit
        override suspend fun refreshCurrentUser(): SessionRefreshResult = error("not used")
        override suspend fun updateProfile(username: String, displayName: String): OperationResult = error("not used")
        override suspend fun checkUsername(username: String): com.dmitry.yume.domain.repository.ProfileDataResult<Boolean> = error("not used")
        override suspend fun suggestUsername(displayName: String): com.dmitry.yume.domain.repository.ProfileDataResult<String> = error("not used")
        override suspend fun changePassword(currentPassword: String, newPassword: String): OperationResult = error("not used")
        override suspend fun changeEmail(newEmail: String, password: String): OperationResult = error("not used")
        override suspend fun uploadAvatar(bytes: ByteArray, mimeType: String): OperationResult = error("not used")
        override suspend fun removeAvatar(): OperationResult = error("not used")
    }
}
