package com.veltrix

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.veltrix.ui.components.BottomNavBar
import com.veltrix.ui.screens.AnalyticsScreen
import com.veltrix.ui.screens.DashboardScreen
import com.veltrix.ui.screens.LoginScreen
import com.veltrix.ui.screens.SettingsScreen
import com.veltrix.ui.screens.SplashScreen
import com.veltrix.ui.screens.StaffDirectoryScreen
import com.veltrix.ui.theme.springDampingRatio
import com.veltrix.ui.theme.springStiffness
import com.veltrix.ui.theme.VeltrixBackground
import com.veltrix.viewmodel.MainViewModel

enum class AppTab(val label: String, val icon: ImageVector) {
    Dashboard("Home", Icons.Filled.Home),
    Staff("Staff", Icons.Filled.Person),
    Analytics("Stats", Icons.Filled.BarChart),
    Settings("Settings", Icons.Filled.Settings)
}

@Composable
fun VeltrixApp(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()

    VeltrixBackground {
        when {
            state.isSplashVisible -> SplashScreen(onAnimationFinished = viewModel::finishSplash)
            !state.isLoggedIn -> LoginScreen(
                email = state.email,
                password = state.password,
                isLoading = state.isLoginLoading,
                errorMessage = state.authError,
                onEmailChange = viewModel::updateEmail,
                onPasswordChange = viewModel::updatePassword,
                onEmailLoginClick = viewModel::login,
                onGoogleTokenReceived = viewModel::loginWithGoogle
            )
            else -> MainShell(
                selectedTab = state.selectedTab,
                onTabSelected = viewModel::selectTab,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
            ) { selected ->
                when (selected) {
                    AppTab.Dashboard -> DashboardScreen(
                        state = state,
                        onSearchChange = viewModel::updateSearchQuery,
                        modifier = Modifier.fillMaxSize()
                    )
                    AppTab.Staff -> StaffDirectoryScreen(
                        state = state,
                        onSearchChange = viewModel::updateSearchQuery,
                        onAddEmployee = viewModel::addEmployee,
                        onUpdateEmployee = viewModel::updateEmployee,
                        modifier = Modifier.fillMaxSize()
                    )
                    AppTab.Analytics -> AnalyticsScreen(
                        state = state,
                        modifier = Modifier.fillMaxSize()
                    )
                    AppTab.Settings -> SettingsScreen(
                        state = state,
                        onDarkModeChange = viewModel::toggleDarkMode,
                        onNotificationsChange = viewModel::toggleNotifications,
                        onLogout = viewModel::logout,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun MainShell(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    contentPadding: PaddingValues,
    content: @Composable (AppTab) -> Unit
) {
    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        bottomBar = {
            BottomNavBar(
                tabs = AppTab.entries,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(contentPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    slideInVertically(
                        animationSpec = androidx.compose.animation.core.spring(
                            stiffness = springStiffness,
                            dampingRatio = springDampingRatio
                        )
                    ) { it / 8 } + fadeIn(
                        animationSpec = androidx.compose.animation.core.spring(
                            stiffness = springStiffness,
                            dampingRatio = springDampingRatio
                        )
                    ) togetherWith
                        slideOutVertically(
                            animationSpec = androidx.compose.animation.core.spring(
                                stiffness = springStiffness,
                                dampingRatio = springDampingRatio
                            )
                        ) { -it / 12 } + fadeOut(
                            animationSpec = androidx.compose.animation.core.spring(
                                stiffness = springStiffness,
                                dampingRatio = springDampingRatio
                            )
                        )
                },
                label = "app-tabs"
            ) { target ->
                content(target)
            }
        }
    }
}
