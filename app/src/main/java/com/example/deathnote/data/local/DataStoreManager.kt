package com.example.deathnote.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        val IS_GUEST_MODE = booleanPreferencesKey("is_guest_mode")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val isGuestMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_GUEST_MODE] ?: true
    }

    suspend fun setGuestMode(isGuest: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_GUEST_MODE] = isGuest
        }
    }
}
