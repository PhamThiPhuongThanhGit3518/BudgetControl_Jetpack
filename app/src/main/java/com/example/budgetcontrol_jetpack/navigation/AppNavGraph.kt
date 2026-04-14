package com.example.budgetcontrol_jetpack.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetcontrol_jetpack.R
import com.example.budgetcontrol_jetpack.MyApp
import com.example.budgetcontrol_jetpack.ui.screen.auth.AuthScreen
import com.example.budgetcontrol_jetpack.ui.screen.category.CategoryEditorScreen
import com.example.budgetcontrol_jetpack.ui.screen.category.CategoryListScreen
import com.example.budgetcontrol_jetpack.ui.screen.category.CategoryTransactionHistoryDialog
import com.example.budgetcontrol_jetpack.ui.screen.dashboard.DashboardScreen
import com.example.budgetcontrol_jetpack.ui.screen.transaction.TransactionEditorScreen
import com.example.budgetcontrol_jetpack.ui.screen.transaction.TransactionListScreen
import com.example.budgetcontrol_jetpack.viewmodel.auth.AuthViewModel
import com.example.budgetcontrol_jetpack.viewmodel.category.CategoryEditorViewModel
import com.example.budgetcontrol_jetpack.viewmodel.category.CategoryListViewModel
import com.example.budgetcontrol_jetpack.viewmodel.dashboard.DashboardViewModel
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionEditorViewModel
import com.example.budgetcontrol_jetpack.viewmodel.transaction.TransactionListViewModel
import com.example.clean.containers.CategoryContainer
import com.example.clean.containers.DashboardContainer
import com.example.clean.containers.TransactionContainer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as MyApp

    val transactionContainer = remember { TransactionContainer(app.repositoryContainer) }
    val categoryContainer = remember { CategoryContainer(app.repositoryContainer) }
    val dashboardContainer = remember { DashboardContainer(app.repositoryContainer) }
    val authVm = remember { AuthViewModel(app.repositoryContainer.authRepository) }
    val authState by authVm.uiState.collectAsState()
    val googleWebClientId = remember {
        context.getString(R.string.google_web_client_id)
    }
    val googleSignInClient = remember {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleWebClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, options)
    }
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        runCatching {
            val account = GoogleSignIn
                .getSignedInAccountFromIntent(result.data)
                .getResult(ApiException::class.java)
            val googleIdToken = account.idToken
            if (googleIdToken.isNullOrBlank()) {
                authVm.showError("Google chưa trả ID token. Kiểm tra Web client ID trong Firebase.")
                return@rememberLauncherForActivityResult
            }
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    authResult.user?.getIdToken(false)
                        ?.addOnSuccessListener { tokenResult ->
                            val token = tokenResult.token
                            if (token.isNullOrBlank()) {
                                authVm.showError("Không lấy được Firebase token")
                            } else {
                                authVm.firebaseLogin(token)
                            }
                        }
                        ?.addOnFailureListener {
                            authVm.showError(it.message ?: "Không lấy được Firebase token")
                        }
                }
                .addOnFailureListener {
                    authVm.showError(it.message ?: "Đăng nhập Google thất bại")
                }
        }.onFailure {
            authVm.showError(it.message ?: "Đăng nhập Google thất bại")
        }
    }
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun logout() {
        authVm.logout()
        FirebaseAuth.getInstance().signOut()
        googleSignInClient.signOut()
        navController.navigate(Destinations.AUTH) {
            popUpTo(0) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBottomBar,
        drawerContent = {
            AccountDrawerContent(
                displayName = authState.displayName,
                onLogoutClick = {
                    scope.launch {
                        drawerState.close()
                        logout()
                    }
                }
            )
        }
    ) {
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
                startDestination = Destinations.AUTH,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Destinations.AUTH) {
                    LaunchedEffect(authState.isAuthenticated) {
                        if (authState.isAuthenticated) {
                            navController.navigate(Destinations.HOME) {
                                popUpTo(Destinations.AUTH) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                    AuthScreen(
                        isLoading = authState.isLoading,
                        errorMessage = authState.errorMessage,
                        onPhoneLogin = { phoneNumber, password ->
                            authVm.login(phoneNumber, password)
                        },
                        onPhoneRegister = { phoneNumber, password, displayName ->
                            authVm.register(phoneNumber, password, displayName)
                        },
                        onGoogleClick = {
                            if (googleWebClientId == "YOUR_WEB_CLIENT_ID") {
                                authVm.showError("Chưa cấu hình Google Web client ID trong strings.xml")
                            } else {
                                googleLauncher.launch(googleSignInClient.signInIntent)
                            }
                        }
                    )
                }

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
                        },
                        onAvatarClick = {
                            scope.launch { drawerState.open() }
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

}

private data class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
private fun AccountDrawerContent(
    displayName: String,
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFFFFFFFF),
        modifier = Modifier
            .width(304.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color(0xFF5FB7CF),
                    modifier = Modifier.size(52.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tài khoản",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF707783)
                    )
                    Text(
                        text = displayName.ifBlank { "BudgetControl" },
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF545454),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Đăng xuất",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
