package com.calcmate.allinonecalculator.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.store by preferencesDataStore("calcmate_settings")
class AppStore(private val context: Context) {
    private val favorites = stringSetPreferencesKey("favorites")
    private val history = stringSetPreferencesKey("history")
    private val theme = stringPreferencesKey("theme")
    private val haptics = booleanPreferencesKey("haptics")
    private val precision = intPreferencesKey("precision")
    private val recent = stringPreferencesKey("recent")
    val favoritesFlow = context.store.data.map { it[favorites] ?: emptySet() }
    val historyFlow = context.store.data.map { (it[history] ?: emptySet()).toList().sortedDescending() }
    val themeFlow = context.store.data.map { it[theme] ?: "system" }
    val hapticsFlow = context.store.data.map { it[haptics] ?: true }
    val precisionFlow = context.store.data.map { it[precision] ?: 8 }
    val recentFlow = context.store.data.map { it[recent]?.split('|')?.filter { id -> id.isNotBlank() } ?: emptyList() }
    suspend fun toggleFavorite(id:String) { context.store.edit { val set=(it[favorites]?:emptySet()).toMutableSet(); if(!set.add(id))set.remove(id); it[favorites]=set } }
    suspend fun addHistory(value:String) { context.store.edit { val set=(it[history]?:emptySet()).toMutableSet(); set.add(value); it[history]=set.toList().takeLast(50).toSet() } }
    suspend fun deleteHistoryItem(value: String) { context.store.edit { val set=(it[history]?:emptySet()).toMutableSet(); set.remove(value); it[history]=set } }
    suspend fun clearHistory() { context.store.edit { it.remove(history) } }
    suspend fun setTheme(value: String) { context.store.edit { it[theme] = value } }
    suspend fun setHaptics(value: Boolean) { context.store.edit { it[haptics] = value } }
    suspend fun setPrecision(value: Int) { context.store.edit { it[precision] = value.coerceIn(0, 12) } }
    suspend fun addRecent(id: String) { context.store.edit { val values = (it[recent]?.split('|') ?: emptyList()).filter { item -> item != id }.toMutableList(); values.add(0, id); it[recent] = values.take(8).joinToString("|") } }
}
