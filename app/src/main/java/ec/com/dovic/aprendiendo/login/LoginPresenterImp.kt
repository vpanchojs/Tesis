package ec.com.dovic.aprendiendo.login

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

    @Subscribe
    override fun onEventLoginThread(event: LoginEvents) {
        when (event.type) {
            LoginEvents.onSignInSuccess -> {
                view.hideProgressDialog()
                view.navigationMain()
            }
            LoginEvents.onSignInError -> {
                view.hideProgressDialog()
                view.showMessagge(event.message)
            }
            LoginEvents.onRecoverySession -> {
                view.navigationMain()
            }
            LoginEvents.onRecoveryPasswordSuccess -> {
                view.hideProgressDialog()
                view.showMessagge(event.message)
            }
            LoginEvents.onRecoveryPasswordError -> {
                view.hideProgressDialog()
                view.showMessagge(event.message)
            }
        }
    }
}