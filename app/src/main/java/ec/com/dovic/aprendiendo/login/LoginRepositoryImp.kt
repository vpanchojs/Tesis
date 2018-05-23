package ec.com.dovic.aprendiendo.login

import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.SharePreferencesApi
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.login.events.LoginEvents

class LoginRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi) : LoginRepository {

    val TAG = "LoginRepository"
    override fun onSignIn(email: String, password: String) {
        firebaseApi.signIn(email, password, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(LoginEvents.onSignInSuccess, "", Any())
            }

            override fun onError(error: Any?) {
                postEvent(LoginEvents.onSignInError, error.toString(), Any())
            }
        })
    }

    override fun onInSession() {
        firebaseApi.suscribeAuth(object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(LoginEvents.onRecoverySession, "", Any())
            }

            override fun onError(error: Any?) {

            }
        })
    }

    override fun onInSessionRemove() {
        firebaseApi.unSuscribeAuth()
    }

    override fun onRecoveryPassword(email: String) {
        firebaseApi.recoveryPasword(email, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(LoginEvents.onRecoveryPasswordSuccess, response.toString(), Any())
            }

            override fun onError(error: Any?) {
                postEvent(LoginEvents.onRecoveryPasswordError, error.toString(), Any())
            }
        })
    }

    private fun postEvent(type: Int, message: String, any: Any) {
        var event = LoginEvents(type, any, message)
        eventBus.post(event)
    }
}