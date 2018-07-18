package ec.com.dovic.aprendiendo.login

import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.SharePreferencesApi
import ec.com.dovic.aprendiendo.domain.listeners.OnCallbackApis
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.entities.User
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.login.events.LoginEvents

class LoginRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var sharePreferencesApi: SharePreferencesApi) : LoginRepository {

    val TAG = "LoginRepository"


    override fun enviartoken(token: String, user: User) {
        firebaseApi.autenticationFacebook(token, user, object : OnCallbackApis<User> {
            override fun onSuccess(response: User) {
                sharePreferencesApi.sesion(true, User.FACEBOOK)
                postEvent(LoginEvents.ON_SIGIN_SUCCESS_FACEBOOK, response)
            }

            override fun onError(error: Any?) {
                postEvent(LoginEvents.ON__SIGIN_ERROR, error!!)
            }

        })
    }

    override fun enviartokengoogle(idToken: String, user: User) {
        firebaseApi.autenticationGoogle(idToken, user, object : OnCallbackApis<User> {
            override fun onSuccess(response: User) {
                sharePreferencesApi.sesion(true, User.GOOGLE)
                postEvent(LoginEvents.ON__SIGIN_SUCCES_GOOGLE, response)
            }

            override fun onError(error: Any?) {
                postEvent(LoginEvents.ON__SIGIN_ERROR, error!!)
            }
        })
    }

    override fun sendEmailVerify() {
        firebaseApi.sendEmailVerify()
    }

    override fun onSignIn(email: String, password: String) {
        firebaseApi.signIn(email, password, object : OnCallbackApis<Boolean> {
            override fun onSuccess(response: Boolean) {
                if (response)
                    postEvent(LoginEvents.onSignInSuccess, "")
                else
                    postEvent(LoginEvents.onSignInSuccessNoValidEmail, "")
            }

            override fun onError(error: Any?) {
                postEvent(LoginEvents.onSignInError, error.toString())
            }
        })
    }

    override fun onInSession() {
        firebaseApi.suscribeAuth(object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(LoginEvents.onRecoverySession, "")
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
                postEvent(LoginEvents.onRecoveryPasswordSuccess, response.toString())
            }

            override fun onError(error: Any?) {
                postEvent(LoginEvents.onRecoveryPasswordError, error.toString())
            }
        })
    }

    private fun postEvent(type: Int, any: Any) {
        var event = LoginEvents(type, any)
        eventBus.post(event)
    }
}