package ec.com.dovic.aprendiendo.signup

import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.entities.User
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.signup.event.SignupEvent


/**
 * Created by Yavac on 15/1/2018.
 */
class SignupRepositoryImpl(var firebaseApi: FirebaseApi,
                           var eventBusInterface: EventBusInterface) : SignupRepository {

    override fun onSignUp(user: User) {
        firebaseApi.signUp(user, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(SignupEvent.SUCCESS_SIGNUP, "", Any())
            }

            override fun onError(error: Any?) {
                postEvent(SignupEvent.ERROR_SIGNUP, error.toString(), Any())
            }
        })
    }

    private fun postEvent(type: Int, message: String, any: Any) {
        val event = SignupEvent()
        event.setEventType(type)
        event.setEventMsg(message)
        eventBusInterface.post(event)
    }

}