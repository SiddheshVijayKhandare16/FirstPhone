package com.firstphone.app.di

import android.content.Context
import androidx.room.Room
import com.firstphone.app.data.local.FirstPhoneDatabase
import com.firstphone.app.data.local.dao.ManagedAppDao
import com.firstphone.app.data.local.dao.SettingsDao
import com.firstphone.app.data.local.dao.UsageLogDao
import com.firstphone.app.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): FirstPhoneDatabase =
        Room.databaseBuilder(ctx, FirstPhoneDatabase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideSettingsDao(db: FirstPhoneDatabase): SettingsDao = db.settingsDao()
    @Provides fun provideManagedAppDao(db: FirstPhoneDatabase): ManagedAppDao = db.managedAppDao()
    @Provides fun provideUsageLogDao(db: FirstPhoneDatabase): UsageLogDao = db.usageLogDao()
}
