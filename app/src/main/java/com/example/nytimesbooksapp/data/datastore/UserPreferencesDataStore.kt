package com.example.nytimesbooksapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
        private val IS_DARK_MODE = booleanPreferencesKey("isDarkMode")

        private val SELECTED_DATE = stringPreferencesKey("selected_date")
    }


    val isDarkMode : Flow<Boolean> = context.dataStore.data
        .map { pref ->
            pref[IS_DARK_MODE] ?: false
        }
    suspend fun setTheme(isDark: Boolean){
        context.dataStore.edit { pref ->
            pref[IS_DARK_MODE] = isDark
        }
    }

    val lastSyncTime: Flow<Long> = context.dataStore.data
        .map { prefs -> prefs[LAST_SYNC_TIME] ?: 0L }

    suspend fun updateLastSyncTime(timestamp: Long) {
        context.dataStore.edit { prefs ->
            prefs[LAST_SYNC_TIME] = timestamp
        }
    }
    val selectedDate : Flow<String> = context.dataStore.data
        .map { prefs-> prefs[SELECTED_DATE] ?:""  }

    suspend fun selectDate(date : String){
        context.dataStore.edit { prefs->
            prefs[SELECTED_DATE] = date
        }

    }


}
