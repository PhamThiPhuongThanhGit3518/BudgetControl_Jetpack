package com.example.budgetcontrol_jetpack.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetcontrol_jetpack.MyApp
import com.example.budgetcontrol_jetpack.ui.screen.category.CategoryEditorScreen
import com.example.budgetcontrol_jetpack.ui.screen.category.CategoryListScreen
import com.example.budgetcontrol_jetpack.ui.screen.category.CategoryTransactionHistoryDialog
import com.example.budgetcontrol_jetpack.ui.screen.dashboard.DashboardScreen
import com.example.budgetcontrol_jetpack.ui.screen.transaction.TransactionEditorScreen
import com.example.budgetcontrol_jetpack.ui.screen.transaction.TransactionListScreen
import com.example.budgetcontrol_jetpack.viewmodel.category.CategoryEditorViewModel
import com.example.budgetcontrol_jetpack.viewmodel.category.CategoryListViewModel
import com.example.budgetcontrol_jetpack.viewmodel.dashboard.DashboardViewModel
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionEditorViewModel
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionListViewModel
import com.example.clean.containers.CategoryContainer
import com.example.clean.containers.DashboardContainer
import com.example.clean.containers.TransactionContainer

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as MyApp

    val transactionContainer = remember { TransactionContainer(app.repositoryContainer) }
    val categoryContainer = remember { CategoryContainer(app.repositoryContainer) }
    val dashboardContainer = remember { DashboardContainer(app.repositoryContainer) }
    var transactionEditorId by remember { mutableStateOf<Long?>(null) }
    var categoryEditorId by remember { mutableStateOf<Long?>(null) }

    val bottomDestinations = listOf(
        BottomNavDestination(Destinations.HOME, "Trang chủ", Icons.Default.Home),
        BottomNavDestination(Destinations.DASHBOARD, "Thống kê", Icons.Default.BarChart),
        BottomNavDestination(Destinations.CATEGORIES, "Danh mục", Icons.Default.Category)
    )
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination
    val showBottomBar = bottomDestinations.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color(0xFFFFFBFF)) {
                    bottomDestinations.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFD46A16),
                                selectedTextColor = Color(0xFFD46A16),
                                unselectedIconColor = Color(0xFF9E9CA3),
                                unselectedTextColor = Color(0xFF77737D),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinations.HOME) {
                val vm = remember {
                    TransactionListViewModel(
                        useCases = transactionContainer.useCases
                    )
                }

                TransactionListScreen(
                    viewModel = vm,
                    onAddClick = {
                        transactionEditorId = 0L
                    },
                    onEditClick = { id ->
                        transactionEditorId = id
                    }
                )
            }

            composable(Destinations.CATEGORIES) {
                val categoryVm = remember {
                    CategoryListViewModel(
                        useCases = categoryContainer.useCases
                    )
                }
                val transactionVm = remember {
                    TransactionListViewModel(
                        useCases = transactionContainer.useCases
                    )
                }
                var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
                val categoryState by categoryVm.uiState.collectAsState()
                val transactionState by transactionVm.uiState.collectAsState()

                CategoryListScreen(
                    viewModel = categoryVm,
                    onAddClick = {
                        categoryEditorId = 0L
                    },
                    onCategoryClick = { id ->
                        selectedCategoryId = id
                    },
                    onEditClick = { id ->
                        categoryEditorId = id
                    }
                )

                val selectedCategory = categoryState.categories.firstOrNull {
                    it.id == selectedCategoryId
                }
                if (selectedCategory != null) {
                    CategoryTransactionHistoryDialog(
                        category = selectedCategory,
                        transactions = transactionState.transactions.filter {
                            it.categoryId == selectedCategory.id
                        },
                        onDismiss = { selectedCategoryId = null }
                    )
                }
            }

            composable(Destinations.DASHBOARD) {
                val vm = remember {
                    DashboardViewModel(
                        useCases = dashboardContainer.useCases
                    )
                }

                DashboardScreen(
                    viewModel = vm
                )
            }
        }

        transactionEditorId?.let { id ->
            val vm = remember(id) {
                TransactionEditorViewModel(
                    useCases = transactionContainer.useCases,
                    categoryUseCases = categoryContainer.useCases
                )
            }

            LaunchedEffect(id) {
                vm.load(id)
            }

            TransactionEditorScreen(
                viewModel = vm,
                onDone = { transactionEditorId = null },
                onDismiss = { transactionEditorId = null }
            )
        }

        categoryEditorId?.let { id ->
            val vm = remember(id) {
                CategoryEditorViewModel(
                    useCases = categoryContainer.useCases
                )
            }

            LaunchedEffect(id) {
                vm.load(id)
            }

            CategoryEditorScreen(
                viewModel = vm,
                onDone = { categoryEditorId = null },
                onDismiss = { categoryEditorId = null }
            )
        }
    }
}

private data class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)
