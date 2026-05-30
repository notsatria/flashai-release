package com.notsatria.flashcard.ui.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notsatria.flashcard.ui.components.AuthTextField
import com.notsatria.flashcard.ui.components.FlashButton
import com.notsatria.flashcard.ui.screens.register.RegisterScreenContent
import com.notsatria.flashcard.ui.theme.FlashColors
import com.notsatria.flashcard.ui.theme.FlashTypography
import com.notsatria.flashcard.ui.theme.FlashcardTheme

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    LoginScreenContent()
}

@Composable
fun LoginScreenContent(modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Mulai Belajar Sekarang!", style = FlashTypography.titleLarge.copy(fontSize = 32.sp))
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
                Column( Modifier.padding(16.dp)) {
                    AuthTextField(
                        label = "Email",
                        value = "",
                        onValueChange = {},
                        placeholder = "contoh@email.com",
                        icon = Icons.Outlined.Email
                    )
                    Spacer(Modifier.height(16.dp))
                    AuthTextField(
                        label = "Password",
                        value = "",
                        onValueChange = {},
                        icon = Icons.Outlined.Lock,
                        isPassword = true
                    )
                    Spacer(Modifier.height(16.dp))
                    FlashButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Masuk",
                        onClick = {},
                        isLoading = false,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    FlashcardTheme {
        RegisterScreenContent()
    }
}