package ec.edu.unl.blockstudy.login

import ec.edu.unl.blockstudy.login.events.LoginEvents

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
}