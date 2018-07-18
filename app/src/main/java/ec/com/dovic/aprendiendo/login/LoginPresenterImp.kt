package ec.com.dovic.aprendiendo.login

import android.net.Uri
import android.view.View
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.login.events.LoginEvents
import ec.com.dovic.aprendiendo.login.ui.LoginView
import org.greenrobot.eventbus.Subscribe

class LoginPresenterImp(var eventBus: EventBusInterface, var view: LoginView, var interactor: LoginInteractor) : LoginPresenter {


    override fun onResume() {
        eventBus.register(this)
    }

    override fun onPause() {
        eventBus.unregister(this)
    }

    override fun onSignIn(email: String, password: String) {
        view.showProgressDialog(R.string.signiping)
        interactor.onSignIn(email, password)
    }

    override fun onInSession() {
        interactor.onInSession()
    }

    override fun onInSessionRemove() {
        interactor.onInSessionRemove()
    }

    override fun onRecoveryPassword(email: String) {
        view.showProgressDialog(R.string.verify_email)
        interactor.onRecoveryPassword(email)
    }

    override fun sendEmailVerify() {
        interactor.sendEmailVerify()
    }

    override fun tokenFacebook(token: String, name: String?, lastname: String?, email: String?, photoUrl: Uri?) {
        view.showButtonSignIn(View.INVISIBLE, "Iniciando sessión")
        view.showProgress(View.VISIBLE)
        interactor.tokenFacebook(token, name, lastname, email, photoUrl)
    }

    override fun tokenGoogle(idToken: String, name: String?, lastname: String?, email: String?, photoUrl: Uri?) {
        view.showButtonSignIn(View.INVISIBLE, "Iniciando sessión")
        view.showProgress(View.VISIBLE)
        interactor.tokenGoogle(idToken, name, lastname, email, photoUrl)
    }

    @Subscribe
    override fun onEventLoginThread(event: LoginEvents) {
        when (event.type) {
            LoginEvents.onSignInSuccess -> {
                view.hideProgressDialog()
                view.navigationMain()
            }

            LoginEvents.onSignInError -> {
                view.showProgress(View.GONE)
                view.showButtonSignIn(View.VISIBLE, "Ingresar con...")
                view.hideProgressDialog()
                view.showMessagge(event.any.toString())
            }

            LoginEvents.onRecoverySession -> {
                view.navigationMain()
            }

            LoginEvents.ON_SIGIN_SUCCESS_FACEBOOK -> {
                view.hideProgressDialog()
                view.navigationMain()

            }
            LoginEvents.ON__SIGIN_SUCCES_GOOGLE -> {
                view.hideProgressDialog()
                view.navigationMain()
            }
            LoginEvents.ON__SIGIN_ERROR -> {
                view.showProgress(View.GONE)
                view.showButtonSignIn(View.VISIBLE, "Ingresar con...")
                view.hideProgressDialog()
                view.showMessagge(event.any.toString())
            }

            LoginEvents.onRecoveryPasswordSuccess -> {
                view.hideProgressDialog()
                view.showMessagge(event.any.toString())
            }
            LoginEvents.onSignInSuccessNoValidEmail -> {
                view.hideProgressDialog()
                view.showSnackBar("Debe validar su correo. Podemos reenviar un email con el enlace.")
            }
            LoginEvents.onRecoveryPasswordError -> {
                view.hideProgressDialog()
                view.showMessagge(event.any.toString())
            }
        }
    }
}