package ec.edu.unl.blockstudy.profile

import ec.edu.unl.blockstudy.entities.Subject
import ec.edu.unl.blockstudy.profile.events.ProfileEvents

/**
 * Created by victor on 15/1/18.
 */
interface ProfilePresenter {

    fun onResume()

    fun onPause()

    fun updateInfo(name: String, lastname: String, email: String)

    fun updateAcademic(school: String, title: String)

    fun addSubject(subject: ArrayList<Subject>)

    fun removeSubject(subject: String)

    fun updatePhotoUser(photo: String)

    fun onEventProfileThread(event: ProfileEvents)

    fun getPreferences()

    fun getAcademic()

    fun getDataUser()
}