package ec.com.dovic.aprendiendo.menu

import com.google.firebase.auth.FirebaseUser
import ec.com.dovic.aprendiendo.domain.FirebaseApi
import ec.com.dovic.aprendiendo.domain.listeners.onDomainApiActionListener
import ec.com.dovic.aprendiendo.entities.User
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.menu.events.MenusEvents

/**
 * Created by victor on 27/1/18.
 */
class MenusRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi) : MenusRepository {

    override fun crearCuestionario() {
       // firebaseApi.crearCuestionarios()
    }

    override fun onSingOut() {
        firebaseApi.signOut()
        postEvent(MenusEvents.ON_SIGNOUT_SUCCESS, "", Any())
    }

    override fun onUpdatePassword(password: String, passwordOld: String) {
        firebaseApi.updatePassword(password, passwordOld, object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {
                postEvent(MenusEvents.ON_UPDATE_PASSWORD_SUCCESS, response.toString(), Any())
            }

            override fun onError(error: Any?) {
                postEvent(MenusEvents.ON_UPDATE_PASSWORD_ERROR, error.toString(), Any())
            }
        })
    }

    override fun getMyProfile() {
        firebaseApi.getResumeProfile(object : onDomainApiActionListener {
            override fun onSuccess(response: Any?) {

                var firebaseUser = response as FirebaseUser
                var user = User()
                user.name = firebaseUser.displayName.toString()
                user.email = firebaseUser.email.toString()
                user.photo = if (firebaseUser.photoUrl == null) "" else firebaseUser.photoUrl.toString()
                postEvent(MenusEvents.ON_GET_MY_PROFILE_SUCCESS, "", user)
            }

            override fun onError(error: Any?) {
                postEvent(MenusEvents.ON_GET_MY_PROFILE_ERROR, error.toString(), Any())
            }
        })
    }

    private fun postEvent(type: Int, message: String, any: Any) {
        var event = MenusEvents(type, any, message)
        eventBus.post(event)
    }
}