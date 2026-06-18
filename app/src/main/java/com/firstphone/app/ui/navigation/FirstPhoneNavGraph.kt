package com.firstphone.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.firstphone.app.ui.screens.appselection.AlwaysAllowedScreen
import com.firstphone.app.ui.screens.appselection.VaultAppsScreen
import com.firstphone.app.ui.screens.dashboard.DashboardScreen
import com.firstphone.app.ui.screens.dashboard.SetupGateViewModel
import com.firstphone.app.ui.screens.parent.ChangePinScreen
import com.firstphone.app.ui.screens.parent.OverrideScreen
import com.firstphone.app.ui.screens.parent.ParentGateScreen
import com.firstphone.app.ui.screens.parent.ParentSettingsScreen
import com.firstphone.app.ui.screens.parent.VacationModeScreen
import com.firstphone.app.ui.screens.parent.WeekendModeScreen
import com.firstphone.app.ui.screens.permissions.PermissionsScreen
import com.firstphone.app.ui.screens.pin.CreatePinScreen
import com.firstphone.app.ui.screens.timelimit.ChooseTimeScreen
import com.firstphone.app.ui.screens.welcome.WelcomeScreen

@Composable
fun FirstPhoneNavGraph() {
    val nav = rememberNavController()
    val gate: SetupGateViewModel = hiltViewModel()
    val start by gate.startDestination.collectAsState()

    NavHost(navController = nav, startDestination = start) {
        composable(Routes.WELCOME) { WelcomeScreen(onGetStarted = { nav.navigate(Routes.CREATE_PIN) }) }

        composable(Routes.CREATE_PIN) {
            CreatePinScreen(onPinCreated = { nav.navigate(Routes.PERMISSIONS) })
        }

        composable(Routes.PERMISSIONS) {
            PermissionsScreen(onAllGranted = { nav.navigate(Routes.ALWAYS_ALLOWED) })
        }

        composable(Routes.ALWAYS_ALLOWED) {
            AlwaysAllowedScreen(onNext = { nav.navigate(Routes.VAULT_APPS) })
        }

        composable(Routes.VAULT_APPS) {
            VaultAppsScreen(onNext = { nav.navigate(Routes.CHOOSE_TIME) })
        }

        composable(Routes.CHOOSE_TIME) {
            ChooseTimeScreen(onSaved = {
                nav.navigate(Routes.DASHBOARD) {
                    popUpTo(Routes.WELCOME) { inclusive = true }
                }
            })
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onOpenParentSettings = { nav.navigate(Routes.parentGate(Routes.PARENT_SETTINGS)) }
            )
        }

        composable(
            route = Routes.PARENT_GATE,
            arguments = listOf(navArgument("next") { type = NavType.StringType; defaultValue = Routes.PARENT_SETTINGS })
        ) { backStack ->
            val next = backStack.arguments?.getString("next") ?: Routes.PARENT_SETTINGS
            ParentGateScreen(
                onSuccess = {
                    nav.navigate(next) {
                        popUpTo(Routes.DASHBOARD)
                    }
                },
                onCancel = { nav.popBackStack() }
            )
        }

        composable(Routes.PARENT_SETTINGS) {
            ParentSettingsScreen(
                onChangePin = { nav.navigate(Routes.CHANGE_PIN) },
                onEditAllowed = { nav.navigate(Routes.ALWAYS_ALLOWED) },
                onEditVault = { nav.navigate(Routes.VAULT_APPS) },
                onChangeLimits = { nav.navigate(Routes.CHOOSE_TIME) },
                onWeekendMode = { nav.navigate(Routes.WEEKEND_MODE) },
                onVacationMode = { nav.navigate(Routes.VACATION_MODE) },
                onOverride = { nav.navigate(Routes.OVERRIDE) },
                onBack = { nav.popBackStack() }
            )
        }

        composable(Routes.CHANGE_PIN) { ChangePinScreen(onDone = { nav.popBackStack() }) }
        composable(Routes.WEEKEND_MODE) { WeekendModeScreen(onDone = { nav.popBackStack() }) }
        composable(Routes.VACATION_MODE) { VacationModeScreen(onDone = { nav.popBackStack() }) }
        composable(Routes.OVERRIDE) { OverrideScreen(onDone = { nav.popBackStack() }) }
    }
}
