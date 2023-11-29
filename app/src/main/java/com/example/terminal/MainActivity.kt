package com.example.terminal

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terminal.presentation.TerminalUiState
import com.example.terminal.presentation.TerminalViewModel
import com.example.terminal.ui.theme.TerminalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TerminalTheme {
                val viewModel: TerminalViewModel = viewModel()
                val state = viewModel.state.collectAsState()

                when (val currentState = state.value) {
                    is TerminalUiState.Initial -> Unit
                    is TerminalUiState.Content -> {
                        Log.d("MainActivity", currentState.barList.toString())
                    }
                }
            }
        }
    }
}