package ec.edu.unl.blockstudy.profile

import ec.edu.unl.blockstudy.entities.Subject

/**
 * Created by victor on 15/1/18.
 */
interface ProfileInteractor {
    fun updateInfo(name: String, lastname: String, email: String)

    fun updateAcademic(school: String, title: String)

    fun addSubject(subject: ArrayList<Subject>)

    fun removeSubject(subject: String)

    fun updatePhotoUser(photo: String)

    fun getPreferences()

    fun getAcademic()

    fun getDataUser()
}