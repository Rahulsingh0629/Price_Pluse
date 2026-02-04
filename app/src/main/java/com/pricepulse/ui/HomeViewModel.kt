package com.pricepulse.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pricepulse.data.PricePulseRepository
import com.pricepulse.model.PriceHistoryResponse
import com.pricepulse.model.ProductCreateRequest
import com.pricepulse.model.ProductDetailResponse
import com.pricepulse.model.ProductResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ScreenState {
    data object Login : ScreenState()
    data object Register : ScreenState()
    data object ProductList : ScreenState()
    data class ProductDetail(val productId: String) : ScreenState()
}

data class HomeUiState(
    val screen: ScreenState = ScreenState.Login,
    val isLoading: Boolean = false,
    val products: List<ProductResponse> = emptyList(),
    val selectedProduct: ProductDetailResponse? = null,
    val priceHistory: PriceHistoryResponse? = null,
    val errorMessage: String? = null,
    val disclaimer: String = "Prices are scraped from public listings and may change.",
)

class HomeViewModel(
    private val repository: PricePulseRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun goTo(screen: ScreenState) {
        _uiState.value = _uiState.value.copy(screen = screen)
        if (screen is ScreenState.ProductList) {
            loadProducts()
        }
    }

    fun loadProducts() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.loadProducts() }
                .onSuccess { products ->
                    _uiState.value = _uiState.value.copy(isLoading = false, products = products)
                    val disclaimer = products.firstOrNull()?.disclaimer
                    if (!disclaimer.isNullOrBlank()) {
                        _uiState.value = _uiState.value.copy(disclaimer = disclaimer)
                    }
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load products",
                    )
                }
        }
    }

    fun openProduct(productId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.loadProduct(productId) }
                .onSuccess { product ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedProduct = product,
                        screen = ScreenState.ProductDetail(productId),
                        disclaimer = product.disclaimer,
                    )
                    loadHistory(productId)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load product",
                    )
                }
        }
    }

    fun loadHistory(productId: String) {
        viewModelScope.launch {
            runCatching { repository.loadHistory(productId) }
                .onSuccess { history ->
                    _uiState.value = _uiState.value.copy(priceHistory = history)
                }
        }
    }

    fun addProduct(name: String, imageUrl: String?, amazonUrl: String, flipkartUrl: String) {
        val request = ProductCreateRequest(
            name = name,
            image_url = imageUrl,
            store_urls = mapOf(
                "amazon" to amazonUrl,
                "flipkart" to flipkartUrl,
            ),
        )
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { repository.addProduct(request) }
                .onSuccess { product ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedProduct = product,
                        screen = ScreenState.ProductDetail(product.id),
                        disclaimer = product.disclaimer,
                    )
                    loadProducts()
                    loadHistory(product.id)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to add product",
                    )
                }
        }
    }
}
