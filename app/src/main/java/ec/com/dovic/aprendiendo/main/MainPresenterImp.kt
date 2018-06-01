package ec.com.dovic.aprendiendo.main

import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.main.events.MainEvents
import ec.com.dovic.aprendiendo.main.ui.MainView
import org.greenrobot.eventbus.Subscribe

class MainPresenterImp(var eventBus: EventBusInterface, var view: MainView, var interactor: MainInteractor) : MainPresenter {

    override fun onResume() {
        eventBus.register(this)

    }

    override fun onPause() {
        eventBus.unregister(this)
    }

    override fun inSession() {
        interactor.inSession()
    }

    override fun onInSessionRemove() {
        interactor.onInSessionRemove()
    }

    @Subscribe
    fun onEventMainThread(event: MainEvents) {
        when (event.type) {
            MainEvents.onRecoverySessionError -> {
                view.navigationLogin()
            }
            MainEvents.onRecoverySession -> {

            }
        }

    }
}