package com.firstphone.app.data.repository

import com.firstphone.app.data.local.dao.ManagedAppDao
import com.firstphone.app.data.local.entities.ManagedAppEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val dao: ManagedAppDao
) {
    fun observeAll(): Flow<List<ManagedAppEntity>> = dao.observeAll()
    fun observeAllowed(): Flow<List<ManagedAppEntity>> = dao.observeAllowed()
    fun observeVault(): Flow<List<ManagedAppEntity>> = dao.observeVault()

    suspend fun getVaultPackages(): List<String> = dao.getVaultPackages()
    suspend fun getVault(): List<ManagedAppEntity> = dao.getVault()

    suspend fun saveDiscoveredApps(apps: List<ManagedAppEntity>) {
        // Merge with any existing rows by package
        dao.upsertAll(apps)
    }

    suspend fun setAllowed(pkg: String, allowed: Boolean) = dao.setAllowed(pkg, allowed)
    suspend fun setVault(pkg: String, vault: Boolean) = dao.setVault(pkg, vault)
}
