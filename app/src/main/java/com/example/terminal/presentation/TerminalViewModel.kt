package com.example.terminal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terminal.data.ApiFactory
import com.example.terminal.presentation.TerminalUiState.Initial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TerminalViewModel : ViewModel() {

    private val api = ApiFactory.api

    private val _state = MutableStateFlow<TerminalUiState>(Initial)
    val state = _state.asStateFlow()

    init {
        loadBatList()
    }

    private fun loadBatList() {
        viewModelScope.launch {
            val barList = api.getBars().bars
            _state.value = TerminalUiState.Content(barList)
        }
    }
}