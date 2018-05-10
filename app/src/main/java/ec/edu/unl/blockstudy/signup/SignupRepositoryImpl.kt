package ec.edu.unl.blockstudy.signup

import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.User
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.signup.event.SignupEvent


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