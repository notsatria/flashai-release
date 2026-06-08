package com.notsatria.flashai.ui.screens.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notsatria.flashai.ui.components.AuthTextField
import com.notsatria.flashai.ui.components.FlashButton
import com.notsatria.flashai.ui.theme.FlashColors
import com.notsatria.flashai.ui.theme.FlashTypography
import com.notsatria.flashai.ui.theme.FlashcardTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val viewModel: RegisterViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.showSnackbar.collect { message ->
            focusManager.clearFocus(force = true)
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(uiState.isRegisterSuccess) {
        if (uiState.isRegisterSuccess) {
            onNavigateToHome()
        }
    }

    RegisterScreenContent(
        modifier,
        onNavigateToLogin = onNavigateToLogin,
        uiState = uiState,
        onEmailChange = { viewModel.onEmailChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onRegister = {
            viewModel.register()
        },
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun RegisterScreenContent(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {},
    uiState: RegisterUiState = RegisterUiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onRegister: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val emailBringIntoViewRequester = remember { BringIntoViewRequester() }
    val passwordBringIntoViewRequester = remember { BringIntoViewRequester() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Mulai Belajar Sekarang!",
                style = FlashTypography.titleLarge.copy(fontSize = 32.sp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Gabung dengan ribuan pelajar pintar lainnya.",
                style = FlashTypography.bodyMedium
            )
            Spacer(Modifier.height(32.dp))
            Surface(
                modifier = Modifier.padding(horizontal = 16.dp),
                border = BorderStroke(color = FlashColors.Gray200, width = 1.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    AuthTextField(
                        modifier = Modifier
                            .bringIntoViewRequester(emailBringIntoViewRequester)
                            .onFocusEvent { focusState ->
                                if (focusState.isFocused) {
                                    coroutineScope.launch {
                                        delay(KEYBOARD_SCROLL_DELAY_MS)
                                        emailBringIntoViewRequester.bringIntoView()
                                    }
                                }
                            },
                        label = "Email",
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        placeholder = "contoh@email.com",
                        icon = Icons.Outlined.Email
                    )
                    Spacer(Modifier.height(16.dp))
                    AuthTextField(
                        modifier = Modifier
                            .bringIntoViewRequester(passwordBringIntoViewRequester)
                            .onFocusEvent { focusState ->
                                if (focusState.isFocused) {
                                    coroutineScope.launch {
                                        delay(KEYBOARD_SCROLL_DELAY_MS)
                                        passwordBringIntoViewRequester.bringIntoView()
                                    }
                                }
                            },
                        label = "Password",
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        icon = Icons.Outlined.Lock,
                        placeholder = "Password",
                        isPassword = true
                    )
                    Spacer(Modifier.height(16.dp))
                    FlashButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Daftar Akun",
                        onClick = onRegister,
                        isLoading = uiState.isLoading,
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sudah punya akun?")
                Spacer(Modifier.width(4.dp))
                Text(
                    "Masuk",
                    color = FlashColors.Indigo500,
                    modifier = Modifier.clickable {
                        onNavigateToLogin()
                    })
            }
        }
    }
}

private const val KEYBOARD_SCROLL_DELAY_MS = 250L

@Preview
@Composable
private fun Preview() {
    FlashcardTheme {
        RegisterScreenContent()
    }
}
