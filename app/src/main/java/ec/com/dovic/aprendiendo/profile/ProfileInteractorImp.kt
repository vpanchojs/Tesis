package ec.com.dovic.aprendiendo.profile

import ec.com.dovic.aprendiendo.entities.Academic
import ec.com.dovic.aprendiendo.entities.Subject
import ec.com.dovic.aprendiendo.entities.User

/**
 * Created by victor on 28/1/18.
 */
class ProfileInteractorImp(var repository: ProfileRepository) : ProfileInteractor {

    override fun updateInfo(name: String, lastname: String, email: String) {
        repository.updateInfo(User(name, lastname, email))
    }

    override fun updateAcademic(school: String, title: String) {
        repository.updateAcademic(Academic(school, title))
    }

    override fun addSubject(subject: ArrayList<Subject>) {
        repository.addSubject(subject)
    }

    override fun removeSubject(subject: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updatePhotoUser(photo: String) {
        repository.updatePhotoUser(photo)
    }

    override fun getAcademic() {
        repository.getAcademic()
    }

    override fun getDataUser() {
        repository.getDataUser()
    }

    override fun getPreferences() {
        repository.getPreferences()
    }
}