package com.pricepulse.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pricepulse.data.PricePulseRepository
import com.pricepulse.model.ProductResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val query: String = "Sony Headphones",
    val category: String = "All",
    val isLoading: Boolean = false,
    val results: List<ProductResult> = emptyList(),
    val errorMessage: String? = null,
)

class HomeViewModel(
    private val repository: PricePulseRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun onCategoryChange(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun search() {
        val current = _uiState.value
        _uiState.value = current.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                repository.search(current.query, current.category)
            }.onSuccess { results ->
                _uiState.value = _uiState.value.copy(isLoading = false, results = results)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Something went wrong",
                )
            }
        }
    }

    fun loadDemoIfNeeded() {
        if (_uiState.value.results.isEmpty()) {
            search()
        }
    }
}
