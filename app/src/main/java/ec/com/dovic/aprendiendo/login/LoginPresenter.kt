package ec.com.dovic.aprendiendo.login

import android.net.Uri
import ec.com.dovic.aprendiendo.login.events.LoginEvents

/**
 * Created by victor on 15/1/18.
 */
interface LoginPresenter {

    fun onResume()

    fun onPause()

    fun onSignIn(email: String, password: String)

    fun onInSession()
    fun onInSessionRemove()

    fun onRecoveryPassword(email: String)


    fun onEventLoginThread(event: LoginEvents)

    fun sendEmailVerify()

    fun tokenFacebook(token: String, name: String?, lastname: String?, email: String?, photoUrl: Uri?)

    fun tokenGoogle(idToken: String, name: String?, lastname: String?, email: String?, photoUrl: Uri?)
}