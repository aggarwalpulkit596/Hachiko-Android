package com.hachiko.hachiko.sharedPreferences
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by rahul on 17/10/18.
 */

class PrefManager(context:Context) {
    internal var context:Context
    internal var pref:SharedPreferences
    internal var editor:SharedPreferences.Editor
    internal var PRIVATE_MODE = 0
    init{
        this.context = context
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }
    fun saveString(key:String, data:String) {
        editor.putString(key, data)
        editor.commit()
    }
    fun saveBoolean(key:String, `val`:Boolean) {
        editor.putBoolean(key, `val`)
        editor.commit()
    }
    fun getBoolean(key:String):Boolean {
        return pref.getBoolean(key, false)
    }
    fun getString(key:String):String {
        return pref.getString(key, null)
    }
    companion object {
        private val PREF_NAME = "hachiko"
        val SIGN_IN_METHOD="sign in method"
    }
}