package com.firstphone.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.firstphone.app.activity.BlockActivity
import com.firstphone.app.data.repository.AppRepository
import com.firstphone.app.data.repository.SettingsRepository
import com.firstphone.app.domain.usecase.VaultStatusCalculator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The Accessibility Service observes the foreground app and, when a Vault app is detected after
 * the daily limit has been exhausted, launches [BlockActivity] which covers the screen.
 *
 * It does NOT log keystrokes or any content. It only inspects which app is currently in focus.
 */
@AndroidEntryPoint
class AppBlockerAccessibilityService : AccessibilityService() {

    @Inject lateinit var appRepository: AppRepository
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var vaultStatus: VaultStatusCalculator

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var lastForegroundPackage: String? = null
    private var lastBlockTimeMs: Long = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "First Phone accessibility service connected.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return

        val pkg = event.packageName?.toString() ?: return
        if (pkg == packageName) return // ignore ourselves
        if (pkg == lastForegroundPackage && event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            // de-bounce repeated content changes for the same window
            return
        }
        lastForegroundPackage = pkg

        scope.launch {
            val vaultPackages = appRepository.getVaultPackages()
            if (pkg !in vaultPackages) return@launch

            val blocked = vaultStatus.shouldBlockNow()
            if (blocked) {
                val now = System.currentTimeMillis()
                if (now - lastBlockTimeMs < 1500L) return@launch
                lastBlockTimeMs = now
                launchBlockScreen()
            }
        }
    }

    private fun launchBlockScreen() {
        val i = Intent(this, BlockActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        }
        startActivity(i)
    }

    override fun onInterrupt() { /* no-op */ }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object { private const val TAG = "FPAccessibility" }
}
