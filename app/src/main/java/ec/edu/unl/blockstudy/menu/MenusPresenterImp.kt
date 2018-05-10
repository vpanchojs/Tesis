package ec.edu.unl.blockstudy.menu

import android.util.Log
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.User
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.menu.events.MenusEvents
import ec.edu.unl.blockstudy.menu.ui.MenusView
import org.greenrobot.eventbus.Subscribe

class MenusPresenterImp(var eventBus: EventBusInterface, var view: MenusView, var interactor: MenusInteractor) : MenusPresenter {

    override fun onResume() {
        eventBus.register(this)

    }

    override fun onPause() {
        eventBus.unregister(this)
    }

    override fun onSingOut() {
        interactor.onSingOut()
    }

    override fun getMyProfile() {
        interactor.getMyProfile()
    }

    override fun onUpdatePassword(password: String, passwordOld: String) {
        view.showProgressDialog(R.string.update_password)
        interactor.onUpdatePassword(password, passwordOld)
    }

    @Subscribe
    override fun onEventMenuThread(event: MenusEvents) {
        when (event.type) {
            MenusEvents.ON_SIGNOUT_SUCCESS -> {
                view.navigationToLogin()
            }
            MenusEvents.ON_UPDATE_PASSWORD_SUCCESS -> {
                view.hideProgressDialog()
                view.showMessagge(event.message)
            }
            MenusEvents.ON_UPDATE_PASSWORD_ERROR -> {
                view.hideProgressDialog()
                view.showMessagge(event.message)
            }
            MenusEvents.ON_GET_MY_PROFILE_SUCCESS -> {
                Log.e("profile", "llegue")
                view.setDataProfile(event.any as User)
            }
            MenusEvents.ON_GET_MY_PROFILE_ERROR -> {
                view.showMessagge(event.message)
            }

        }
    }
}