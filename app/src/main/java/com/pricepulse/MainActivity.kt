package com.pricepulse

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.pricepulse.data.PricePulseRepository
import com.pricepulse.data.PricePulseService
import com.pricepulse.model.ProductDetailResponse
import com.pricepulse.model.ProductResponse
import com.pricepulse.model.StorePriceResponse
import com.pricepulse.ui.HomeViewModel
import com.pricepulse.ui.ScreenState
import com.pricepulse.ui.theme.PricePulseTheme
import com.pricepulse.ui.theme.PulseGray
import com.pricepulse.ui.theme.PulseTeal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PricePulseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PricePulseApp(openUrl = ::openUrl)
                }
            }
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        ContextCompat.startActivity(this, intent, null)
    }
}

@Composable
private fun PricePulseApp(openUrl: (String) -> Unit) {
    val repository = remember { PricePulseRepository(PricePulseService.createApi()) }
    val viewModel = remember { HomeViewModel(repository) }
    val uiState by viewModel.uiState.collectAsState()

    Crossfade(targetState = uiState.screen, label = "screen") { screen ->
        when (screen) {
            ScreenState.Login -> LoginScreen(
                onLogin = { viewModel.goTo(ScreenState.ProductList) },
                onRegister = { viewModel.goTo(ScreenState.Register) },
            )
            ScreenState.Register -> RegisterScreen(
                onRegister = { viewModel.goTo(ScreenState.ProductList) },
                onBackToLogin = { viewModel.goTo(ScreenState.Login) },
            )
            ScreenState.ProductList -> ProductListScreen(
                products = uiState.products,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                onProductClick = viewModel::openProduct,
                onAddProduct = viewModel::addProduct,
                disclaimer = uiState.disclaimer,
            )
            is ScreenState.ProductDetail -> ProductDetailScreen(
                product = uiState.selectedProduct,
                history = uiState.priceHistory?.history.orEmpty(),
                disclaimer = uiState.disclaimer,
                onBack = { viewModel.goTo(ScreenState.ProductList) },
                openUrl = openUrl,
                isLoading = uiState.isLoading,
            )
        }
    }
}

@Composable
private fun LoginScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    AuthScaffold(
        title = "Welcome back",
        subtitle = "Log in to compare prices with true-cost clarity.",
        primaryActionLabel = "Login",
        secondaryActionLabel = "Create an account",
        onPrimaryAction = onLogin,
        onSecondaryAction = onRegister,
    )
}

@Composable
private fun RegisterScreen(onRegister: () -> Unit, onBackToLogin: () -> Unit) {
    AuthScaffold(
        title = "Create your account",
        subtitle = "Track prices, wishlists, and alerts across all stores.",
        primaryActionLabel = "Register",
        secondaryActionLabel = "Back to login",
        onPrimaryAction = onRegister,
        onSecondaryAction = onBackToLogin,
        showNameField = true,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthScaffold(
    title: String,
    subtitle: String,
    primaryActionLabel: String,
    secondaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    showNameField: Boolean = false,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedVisibility(visible = showNameField) {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Full name") },
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Email") },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onPrimaryAction,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = primaryActionLabel)
        }
        TextButton(onClick = onSecondaryAction, modifier = Modifier.fillMaxWidth()) {
            Text(text = secondaryActionLabel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListScreen(
    products: List<ProductResponse>,
    isLoading: Boolean,
    errorMessage: String?,
    onProductClick: (String) -> Unit,
    onAddProduct: (String, String?, String, String) -> Unit,
    disclaimer: String,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var imageUrl by rememberSaveable { mutableStateOf("") }
    var amazonUrl by rememberSaveable { mutableStateOf("") }
    var flipkartUrl by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(text = "Products", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            Text(
                text = disclaimer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PulseGray),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Add product to track", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Product name") },
                    )
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Image URL (optional)") },
                    )
                    OutlinedTextField(
                        value = amazonUrl,
                        onValueChange = { amazonUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Amazon product URL") },
                    )
                    OutlinedTextField(
                        value = flipkartUrl,
                        onValueChange = { flipkartUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Flipkart product URL") },
                    )
                    Button(
                        onClick = {
                            onAddProduct(name, imageUrl.ifBlank { null }, amazonUrl, flipkartUrl)
                            name = ""
                            imageUrl = ""
                            amazonUrl = ""
                            flipkartUrl = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = "Track Product")
                    }
                }
            }
        }
        item {
            AnimatedVisibility(visible = isLoading) {
                Text(
                    text = "Fetching prices...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
            }
        }
        item {
            AnimatedVisibility(visible = errorMessage != null) {
                Text(
                    text = errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        items(products) { product ->
            Card(
                onClick = { onProductClick(product.id) },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Created ${product.created_at}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductDetailScreen(
    product: ProductDetailResponse?,
    history: List<StorePriceResponse>,
    disclaimer: String,
    onBack: () -> Unit,
    openUrl: (String) -> Unit,
    isLoading: Boolean,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(text = product?.name ?: "Product", style = MaterialTheme.typography.titleMedium)
            }
        }
        item {
            Text(
                text = disclaimer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )
        }
        item {
            AnimatedVisibility(visible = isLoading) {
                Text(
                    text = "Loading prices...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
            }
        }
        if (product != null) {
            item {
                PriceComparisonSection(product = product, openUrl = openUrl)
            }
            item {
                PriceHistorySection(history = history)
            }
        }
    }
}

@Composable
private fun PriceComparisonSection(product: ProductDetailResponse, openUrl: (String) -> Unit) {
    val cheapest = product.latest_prices.minOfOrNull { it.price }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Latest prices", style = MaterialTheme.typography.titleMedium)
        product.latest_prices.forEach { price ->
            ElevatedPriceCard(price = price, openUrl = openUrl, isCheapest = price.price == cheapest)
        }
    }
}

@Composable
private fun ElevatedPriceCard(price: StorePriceResponse, openUrl: (String) -> Unit, isCheapest: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = price.store, style = MaterialTheme.typography.titleMedium)
                if (isCheapest) {
                    AssistChip(
                        onClick = {},
                        label = { Text(text = "Cheapest") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = PulseTeal.copy(alpha = 0.12f),
                            labelColor = PulseTeal,
                        ),
                    )
                }
            }
            Text(text = "₹${price.price}", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Last updated ${price.fetched_at}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            Button(onClick = { openUrl(price.product_url) }) {
                Text(text = "Open in store")
            }
        }
    }
}

@Composable
private fun PriceHistorySection(history: List<StorePriceResponse>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Price history", style = MaterialTheme.typography.titleMedium)
        }
        if (history.isEmpty()) {
            Text(
                text = "No history yet. Check back after the daily refresh.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        } else {
            history.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = entry.store, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "₹${entry.price}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = entry.fetched_at,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
            }
        }
    }
}
