package ec.com.dovic.aprendiendo.domain

import android.content.SharedPreferences
import android.util.Log

/**
 * Created by victor on 21/1/18.
 */
class SharePreferencesApi(var sharedPreferences: SharedPreferences) {
    val KEY_TOKEN_ACCESS = "97e64d26-3b04-417a-8ee2-e761c04b855b"
    val KEY_TOKEN_NOTIFICATION = "74864c0b-076a-4044-85b8-af834f1b53f7"
    val KEY_IN_SESESION = "12be06b6-e0e6-45af-a800-82e4273736df"
    val KEY_PLATFORM = "12be06b6-e0e6-45af-a800-af834f1b53f7"


    fun saveTokenAndSession(inSession: Boolean, tokenAccess: String) {
        sharedPreferences.edit().putBoolean(KEY_IN_SESESION, inSession)
                .putString(KEY_TOKEN_ACCESS, tokenAccess).commit()
    }

    fun getTokenAccess() {
        sharedPreferences.getString(KEY_TOKEN_ACCESS, "")
    }


    fun getInSession(): Boolean {
        return sharedPreferences.getBoolean(KEY_IN_SESESION, false)
    }

    fun getInSessionPlatform(): Int {
        return sharedPreferences.getInt(KEY_PLATFORM, -1)
    }

    fun sesion(session: Boolean, platform: Int) {
        //sharedPreferences.edit().putBoolean(KEY_IN_SESESION, session).apply()

        sharedPreferences.edit().putInt(KEY_PLATFORM, platform).apply()
        Log.e("share", "codigo " + platform)
    }


}