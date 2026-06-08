package com.notsatria.flashai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.notsatria.flashai.navigation.AppNavigation
import com.notsatria.flashai.ui.theme.FlashcardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardTheme {
                AppNavigation()
            }
        }
    }
}
