package ec.com.dovic.aprendiendo.profile

import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.entities.Academic
import ec.com.dovic.aprendiendo.entities.Subject
import ec.com.dovic.aprendiendo.entities.User
import ec.com.dovic.aprendiendo.lib.base.EventBusInterface
import ec.com.dovic.aprendiendo.profile.events.ProfileEvents
import ec.com.dovic.aprendiendo.profile.ui.ProfileView
import org.greenrobot.eventbus.Subscribe

class ProfilePresenterImp(var eventBus: EventBusInterface, var view: ProfileView, var interactor: ProfileInteractor) : ProfilePresenter {

    override fun getAcademic() {
        interactor.getAcademic()
    }

    override fun onResume() {
        eventBus.register(this)
    }

    override fun onPause() {
        eventBus.unregister(this)
    }

    override fun updateInfo(name: String, lastname: String, email: String) {
        view.showProgressDialog(R.string.message_update_user)
        interactor.updateInfo(name, lastname, email)
    }

    override fun updateAcademic(school: String, title: String) {
        view.showProgressDialog(R.string.message_update_academic)
        interactor.updateAcademic(school, title)
    }

    override fun addSubject(subject: ArrayList<Subject>) {
        interactor.addSubject(subject)
    }

    override fun removeSubject(subject: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updatePhotoUser(photo: String) {
        view.showProgressDialog(R.string.message_update_photo_user)
        interactor.updatePhotoUser(photo)
    }

    override fun getDataUser() {
        view.showProgressDialog(R.string.message_get_profile)
        interactor.getDataUser()
    }

    override fun getPreferences() {
        interactor.getPreferences()
    }

    @Subscribe
    override fun onEventProfileThread(event: ProfileEvents) {
        when (event.type) {
            ProfileEvents.ON_UPDATE_USER_SUCCESS -> {
                view.hideProgressDialog()
                view.setInfoUser(event.any as User)
                view.showMessagge("Información Actualizada")
            }
            ProfileEvents.ON_UPDATE_USER_ERROR -> {
                view.hideProgressDialog()
                view.showMessagge(event.message)
            }
            ProfileEvents.ON_UPDATE_PHOTO_USER_SUCCESS -> {
                view.hideProgressDialog()
                view.setPhoto()
                view.showMessagge("Foto Actualizada")
            }
            ProfileEvents.ON_UPDATE_PHOTO_USER_ERROR -> {
                view.hideProgressDialog()
                view.showMessagge(event.message)
            }
            ProfileEvents.ON_UPDATE_ACADEMIC_SUCCESS -> {
                view.hideProgressDialog()
                view.setAcademic(event.any as ArrayList<Academic>)
                view.showMessagge("Formación Academica actualizada")
            }
            ProfileEvents.ON_UPDATE_ACADEMIC_ERROR -> {
                view.hideProgressDialog()
                view.showMessagge(event.message)
            }
            ProfileEvents.ON_GET_MY_PROFILE_SUCCESS -> {
                view.hideProgressDialog()
                view.setDataProfile(event.any as User)
            }
            ProfileEvents.ON_SET_SUBJECTS_SUCCESS -> {
                view.hideProgressDialog()
                view.showMessagge("No se puedo cargar la información")

            }
            ProfileEvents.ON_SET_SUBJECTS_ERROR -> {
                view.hideProgressDialog()
                view.showMessagge(event.message)
            }

            ProfileEvents.ON_GET_SUBJECTS_SUCCESS -> {
                view.hideProgressDialog()
                view.setPreferences(event.any as ArrayList<Subject>)
            }
            ProfileEvents.ON_GET_SUBJECTS_ERROR -> {
                view.hideProgressDialog()
                view.showMessagge(event.message)
            }

            ProfileEvents.ON_GET_ACADEMIC_SUCCESS -> {
                view.hideProgressDialog()
                view.setAcademic(event.any as ArrayList<Academic>)
            }
            ProfileEvents.ON_GET_ACADEMIC_ERROR -> {
                view.hideProgressDialog()
                view.showMessagge(event.message)
            }

        }
    }
}