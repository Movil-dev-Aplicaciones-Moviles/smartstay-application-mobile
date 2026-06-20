package com.smartstay.application_mobile_frontend.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.smartstay.application_mobile_frontend.core.datastore.TokenManager
import com.smartstay.application_mobile_frontend.core.datastore.TokenManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    /**
     * Proveedor de DataStore<Preferences>.
     * Crea una instancia singleton de DataStore con el archivo "auth_prefs".
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("auth_prefs")
        }
    }

    /**
     * Binding de TokenManager a su implementación concreta TokenManagerImpl.
     */
    @Provides
    @Singleton
    fun bindTokenManager(impl: TokenManagerImpl): TokenManager {
        return impl
    }
}
