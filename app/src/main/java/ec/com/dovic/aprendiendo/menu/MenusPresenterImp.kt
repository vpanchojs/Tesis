package ec.com.dovic.aprendiendo.menu

import android.util.Log
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.entities.User
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.menu.events.MenusEvents
import ec.com.dovic.aprendiendo.menu.ui.MenusView
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

    override fun crearCuestionario() {
        interactor.crearCuestionario()
    }

    @Subscribe
    override fun onEventMenuThread(event: MenusEvents) {
        when (event.type) {
            MenusEvents.ON_SIGNOUT_SUCCESS -> {
                view.singOut(event.any as Int)
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