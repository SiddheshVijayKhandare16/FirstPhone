package com.firstphone.app.ui.navigation

object Routes {
    const val WELCOME = "welcome"
    const val CREATE_PIN = "create_pin"
    const val PERMISSIONS = "permissions"
    const val ALWAYS_ALLOWED = "always_allowed"
    const val VAULT_APPS = "vault_apps"
    const val CHOOSE_TIME = "choose_time"
    const val DASHBOARD = "dashboard"

    const val PARENT_GATE = "parent_gate?next={next}"
    fun parentGate(next: String) = "parent_gate?next=$next"

    const val PARENT_SETTINGS = "parent_settings"
    const val CHANGE_PIN = "change_pin"
    const val WEEKEND_MODE = "weekend_mode"
    const val VACATION_MODE = "vacation_mode"
    const val OVERRIDE = "override"
}
