package com.example.terminal.presentation

import com.example.terminal.data.Bar

sealed interface TerminalUiState {

    object Initial : TerminalUiState

    data class Content(val barList: List<Bar>) : TerminalUiState
}