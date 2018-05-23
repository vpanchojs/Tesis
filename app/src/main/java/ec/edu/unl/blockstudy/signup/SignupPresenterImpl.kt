package ec.edu.unl.blockstudy.signup

import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.signup.event.SignupEvent
import ec.edu.unl.blockstudy.signup.ui.SignupView
import org.greenrobot.eventbus.Subscribe

/**
 * Created by Yavac on 15/1/2018.
 */
class SignupPresenterImpl(var view: SignupView,
                          var interactor: SignupInteractor,
                          var eventBus: EventBusInterface) : SignupPresenter {

    override fun onSignUp(name: String, lastname: String, emai: String, password: String) {
        view.showProgressDialog(R.string.signuping)
        interactor.onSignUp(name, lastname, emai, password)
    }

    override fun onResume() {
        eventBus.register(this)
    }

    override fun onPause() {
        eventBus.unregister(this)
    }

    override fun onDestroy() {

    }


    @Subscribe
    override fun onEventSignupThread(event: SignupEvent) {
        view.hideProgressDialog()

        when (event.getEventType()) {
            SignupEvent.SUCCESS_SIGNUP -> {
                view.navigationToMain()
            }
            SignupEvent.ERROR_SIGNUP -> {
                view.showMessagge(event.getEventMsg()!!)
            }
        }

    }

}