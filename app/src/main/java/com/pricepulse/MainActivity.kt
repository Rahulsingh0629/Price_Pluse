package com.pricepulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pricepulse.data.PricePulseRepository
import com.pricepulse.data.PricePulseService
import com.pricepulse.model.ProductResult
import com.pricepulse.model.StoreOffer
import com.pricepulse.ui.HomeViewModel
import com.pricepulse.ui.theme.PricePulseTheme
import com.pricepulse.ui.theme.PulseBlue
import com.pricepulse.ui.theme.PulseGray
import com.pricepulse.ui.theme.PulseOrange
import com.pricepulse.ui.theme.PulseTeal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PricePulseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PricePulseApp()
                }
            }
        }
    }
}

private enum class AppScreen {
    Login,
    Register,
    Home,
}

@Composable
private fun PricePulseApp() {
    var screen by rememberSaveable { mutableStateOf(AppScreen.Login) }
    val repository = remember { PricePulseRepository(PricePulseService.createApi()) }
    val viewModel = remember { HomeViewModel(repository) }

    Crossfade(targetState = screen, label = "screen") { current ->
        when (current) {
            AppScreen.Login -> LoginScreen(
                onLogin = {
                    viewModel.loadDemoIfNeeded()
                    screen = AppScreen.Home
                },
                onRegister = { screen = AppScreen.Register },
            )
            AppScreen.Register -> RegisterScreen(
                onRegister = {
                    viewModel.loadDemoIfNeeded()
                    screen = AppScreen.Home
                },
                onBackToLogin = { screen = AppScreen.Login },
            )
            AppScreen.Home -> HomeScreen(
                viewModel = viewModel,
                onLogout = { screen = AppScreen.Login },
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

@Composable
private fun HomeScreen(viewModel: HomeViewModel, onLogout: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeaderSection(onLogout = onLogout)
        }
        item {
            SearchSection(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                onSearch = viewModel::search,
            )
        }
        item {
            HighlightSection()
        }
        item {
            CategoryFilters(
                selected = uiState.category,
                onSelect = viewModel::onCategoryChange,
            )
        }
        item {
            AnimatedVisibility(visible = uiState.isLoading) {
                Text(
                    text = "Fetching live prices...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
            }
        }
        item {
            AnimatedVisibility(visible = uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        items(uiState.results) { item ->
            ComparisonCard(result = item)
        }
        item {
            RoadmapSection()
        }
    }
}

@Composable
private fun HeaderSection(onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "PricePulse",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Compare prices across Amazon, Flipkart, Myntra, and more.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
        }
        TextButton(onClick = onLogout) {
            Text(text = "Logout")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Search products (e.g., Sony Headphones)")
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        )
        Button(onClick = onSearch, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Compare Now")
        }
    }
}

@Composable
private fun HighlightSection() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PulseGray),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "True Cost Snapshot",
                style = MaterialTheme.typography.titleMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TrueCostItem(label = "Listed", value = "₹10,000")
                TrueCostItem(label = "Shipping", value = "+₹200")
                TrueCostItem(label = "Bank Offer", value = "-₹500")
            }
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Text(
                text = "₹9,700 true cost",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = PulseBlue,
            )
        }
    }
}

@Composable
private fun CategoryFilters(selected: String, onSelect: (String) -> Unit) {
    val categories = listOf("All", "Electronics", "Fashion", "Home", "Beauty", "Grocery")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { category ->
            AssistChip(
                onClick = { onSelect(category) },
                label = { Text(text = category) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (category == selected) PulseBlue.copy(alpha = 0.12f) else PulseGray,
                    labelColor = if (category == selected) PulseBlue else MaterialTheme.colorScheme.onSurface,
                ),
            )
        }
    }
}

@Composable
private fun TrueCostItem(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ComparisonCard(result: ProductResult) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = result.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                result.offers.take(2).forEach { offer ->
                    StorePrice(offer = offer)
                }
            }
            PriceHistoryBar(
                current = result.priceHistory.current,
                lowest = result.priceHistory.lowest30d,
            )
        }
    }
}

@Composable
private fun StorePrice(offer: StoreOffer) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = offer.store,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
        Text(
            text = offer.price,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "Shipping ${offer.shipping}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Text(
            text = "Offer ${offer.offer}",
            style = MaterialTheme.typography.bodyMedium,
            color = PulseOrange,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun PriceHistoryBar(current: String, lowest: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Price History",
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(PulseTeal, PulseBlue),
                    ),
                    shape = RoundedCornerShape(999.dp),
                ),
        ) {}
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Current: $current",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Lowest 30d: $lowest",
                style = MaterialTheme.typography.bodyMedium,
                color = PulseTeal,
            )
        }
    }
}

@Composable
private fun RoadmapSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Roadmap Highlights",
            style = MaterialTheme.typography.titleMedium,
        )
        FeatureRow(
            icon = Icons.Default.LocalOffer,
            title = "Hidden Cost Calculator",
            description = "Show shipping + bank offers to reveal the true price.",
        )
        FeatureRow(
            icon = Icons.Default.History,
            title = "Price Drop Alerts",
            description = "Track snapshots and alert when price falls.",
        )
        FeatureRow(
            icon = Icons.Default.ShoppingCart,
            title = "Unified Wishlist",
            description = "Save items from any store in one list.",
        )
    }
}

@Composable
private fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PulseBlue.copy(alpha = 0.1f)),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PulseBlue,
                modifier = Modifier.padding(12.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
    }
}
