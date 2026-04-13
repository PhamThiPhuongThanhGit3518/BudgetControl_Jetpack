package com.example.budgetcontrol_jetpack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetcontrol_jetpack.MyApp
import com.example.budgetcontrol_jetpack.ui.screen.category.CategoryEditorScreen
import com.example.budgetcontrol_jetpack.ui.screen.category.CategoryListScreen
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

    NavHost(
        navController = navController,
        startDestination = Destinations.TRANSACTIONS
    ) {
        composable(Destinations.TRANSACTIONS) {
            val vm = remember {
                TransactionListViewModel(
                    useCases = transactionContainer.useCases
                )
            }

            TransactionListScreen(
                viewModel = vm,
                onAddClick = {
                    navController.navigate("${Destinations.TRANSACTION_EDITOR}/0")
                },
                onEditClick = { id ->
                    navController.navigate("${Destinations.TRANSACTION_EDITOR}/$id")
                },
                onGoCategories = {
                    navController.navigate(Destinations.CATEGORIES)
                },
                onGoDashboard = {
                    navController.navigate(Destinations.DASHBOARD)
                }
            )
        }

        composable(
            route = "${Destinations.TRANSACTION_EDITOR}/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            val vm = remember {
                TransactionEditorViewModel(
                    useCases = transactionContainer.useCases
                )
            }

            LaunchedEffect(id) {
                vm.load(id)
            }

            TransactionEditorScreen(
                viewModel = vm,
                onDone = { navController.popBackStack() }
            )
        }

        composable(Destinations.CATEGORIES) {
            val vm = remember {
                CategoryListViewModel(
                    useCases = categoryContainer.useCases
                )
            }

            CategoryListScreen(
                viewModel = vm,
                onAddClick = {
                    navController.navigate("${Destinations.CATEGORY_EDITOR}/0")
                },
                onEditClick = { id ->
                    navController.navigate("${Destinations.CATEGORY_EDITOR}/$id")
                }
            )
        }

        composable(
            route = "${Destinations.CATEGORY_EDITOR}/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            val vm = remember {
                CategoryEditorViewModel(
                    useCases = categoryContainer.useCases
                )
            }

            LaunchedEffect(id) {
                vm.load(id)
            }

            CategoryEditorScreen(
                viewModel = vm,
                onDone = { navController.popBackStack() }
            )
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
}