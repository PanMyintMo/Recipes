package com.example.recipes.food.data

import android.content.Context
import android.preference.PreferenceDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.recipes.food.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.recipes.food.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.recipes.food.util.Constants.Companion.PREFERENCES_BACK_ONLINE
import com.example.recipes.food.util.Constants.Companion.PREFERENCE_DIET_TYPE
import com.example.recipes.food.util.Constants.Companion.PREFERENCE_DIET_TYPE_ID
import com.example.recipes.food.util.Constants.Companion.PREFERENCE_MEAL_TYPE
import com.example.recipes.food.util.Constants.Companion.PREFERENCE_MEAL_TYPE_ID
import com.example.recipes.food.util.Constants.Companion.PREFERENCE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.concurrent.Flow
import javax.inject.Inject


@ActivityRetainedScoped
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

     private object PreferenceKeys {
        val selectedMealType = stringPreferencesKey(PREFERENCE_MEAL_TYPE)
        val selectedMealTypeId = intPreferencesKey(PREFERENCE_MEAL_TYPE_ID)
        val selectedDietType = stringPreferencesKey(PREFERENCE_DIET_TYPE)
        val selectedDietTypeId = intPreferencesKey(PREFERENCE_DIET_TYPE_ID)

        val backOnline = booleanPreferencesKey(PREFERENCES_BACK_ONLINE)


    }

    suspend fun saveMealAndDietType(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectedMealType] = mealType
            preferences[PreferenceKeys.selectedMealTypeId] = mealTypeId
            preferences[PreferenceKeys.selectedDietType] = dietType
            preferences[PreferenceKeys.selectedDietTypeId] = dietTypeId

        }
    }

    suspend fun saveBackOnline(backOnline: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.backOnline] = backOnline

        }
    }


    val readMealAndDietType: kotlinx.coroutines.flow.Flow<MealAndDietType> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw  exception
            }

        }
        .map { preferences ->

            val selectedMealType = preferences[PreferenceKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE
            val selectedMealTypeId = preferences[PreferenceKeys.selectedDietTypeId] ?: 0
            val selectedDietType = preferences[PreferenceKeys.selectedDietType] ?: DEFAULT_DIET_TYPE
            val selectedDietTypeId = preferences[PreferenceKeys.selectedDietTypeId] ?: 0
            MealAndDietType(
                selectedMealType,
                selectedMealTypeId,
                selectedDietType,
                selectedDietTypeId
            )
        }

    val readBackOnline: kotlinx.coroutines.flow.Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }

        }
        .map { preferences ->
            val backOnline = preferences[PreferenceKeys.backOnline] ?: false
            backOnline

        }

}
data class MealAndDietType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)