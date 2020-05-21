package hu.bme.aut.tvseries.utils

import android.content.Context
import android.preference.PreferenceManager

class PrefUtils {
    companion object {

        private const val USER_LOGGED_IN = "user_logged_in"

        fun getUserLoggedIn(context: Context): Boolean {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getBoolean(USER_LOGGED_IN, false)
        }

        fun setUserLggedIn(logged: Boolean, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putBoolean(USER_LOGGED_IN, logged)
            editor.apply()
        }

        private const val USER_ROLE = "user_token"

        fun getRole(context: Context): String? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(USER_ROLE, "")
        }

        fun setRole(role: String, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(USER_ROLE, role)
            editor.apply()
        }

        private const val USER_ID = "user_id"

        fun getUserID(context: Context): String? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(USER_ID, "")
        }

        fun setUserID(id: String, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(USER_ID, id)
            editor.apply()
        }

        private const val TOKEN = "token"

        fun getToken(context: Context): String? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(TOKEN, "")
        }

        fun setToken(id: String, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(TOKEN, id)
            editor.apply()
        }

        private const val USER_FOLLOWED = "user_followed"

        fun getUserFollowed(context: Context): Set<String>? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getStringSet(USER_FOLLOWED, setOf())
        }

        fun setUserFollowed(id: Set<String>, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putStringSet(USER_FOLLOWED, id)
            editor.apply()
        }

        private const val USER_WATCHED = "user_watched"

        fun getUserWatched(context: Context): Set<String>? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getStringSet(USER_WATCHED, setOf())
        }

        fun setUserWatched(id: Set<String>, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putStringSet(USER_WATCHED, id)
            editor.apply()
        }
    }
}