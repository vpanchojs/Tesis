package ec.com.dovic.aprendiendo.main

import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.main.events.MainEvents

/**
 * Created by victor on 27/1/18.
 */
class MainRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi) : MainRepository {

    override fun inSession() {
        firebaseApi.suscribeAuth(object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(MainEvents.onRecoverySession, Any())
            }

            override fun onError(error: Any?) {
                postEvent(MainEvents.onRecoverySessionError, error!!)
            }
        })
    }

    override fun onInSessionRemove() {
        firebaseApi.unSuscribeAuth()
    }

    private fun postEvent(type: Int, any: Any) {
        var event = MainEvents(type, any)
        eventBus.post(event)
    }
}