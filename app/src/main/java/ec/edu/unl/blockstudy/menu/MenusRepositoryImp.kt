package ec.edu.unl.blockstudy.menu

import com.google.firebase.auth.FirebaseUser
import ec.edu.unl.blockstudy.domain.FirebaseApi
import ec.edu.unl.blockstudy.domain.SharePreferencesApi
import ec.edu.unl.blockstudy.domain.listeners.onDomainApiActionListener
import ec.edu.unl.blockstudy.entities.User
import ec.edu.unl.blockstudy.lib.base.EventBusInterface
import ec.edu.unl.blockstudy.menu.events.MenusEvents

/**
 * Created by victor on 27/1/18.
 */
class MenusRepositoryImp(var eventBus: EventBusInterface, var firebaseApi: FirebaseApi, var sharePreferencesApi: SharePreferencesApi) : MenusRepository {

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