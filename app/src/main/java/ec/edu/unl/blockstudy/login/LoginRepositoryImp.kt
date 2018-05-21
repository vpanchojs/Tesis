package ec.edu.unl.blockstudy.login

import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.login.events.LoginEvents

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