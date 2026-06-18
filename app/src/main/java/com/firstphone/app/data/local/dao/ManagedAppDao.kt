package com.firstphone.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.firstphone.app.data.local.entities.ManagedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ManagedAppDao {

    @Query("SELECT * FROM managed_apps")
    fun observeAll(): Flow<List<ManagedAppEntity>>

    @Query("SELECT * FROM managed_apps WHERE isAlwaysAllowed = 1")
    fun observeAllowed(): Flow<List<ManagedAppEntity>>

    @Query("SELECT * FROM managed_apps WHERE isVault = 1")
    fun observeVault(): Flow<List<ManagedAppEntity>>

    @Query("SELECT * FROM managed_apps WHERE isVault = 1")
    suspend fun getVault(): List<ManagedAppEntity>

    @Query("SELECT packageName FROM managed_apps WHERE isVault = 1")
    suspend fun getVaultPackages(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(apps: List<ManagedAppEntity>)

    @Query("UPDATE managed_apps SET isAlwaysAllowed = :allowed WHERE packageName = :pkg")
    suspend fun setAllowed(pkg: String, allowed: Boolean)

    @Query("UPDATE managed_apps SET isVault = :vault WHERE packageName = :pkg")
    suspend fun setVault(pkg: String, vault: Boolean)

    @Query("DELETE FROM managed_apps")
    suspend fun clear()
}
