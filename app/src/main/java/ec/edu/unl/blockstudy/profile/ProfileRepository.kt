package ec.edu.unl.blockstudy.profile

import ec.edu.unl.blockstudy.entities.Academic
import ec.edu.unl.blockstudy.entities.Subject
import ec.edu.unl.blockstudy.entities.User

/**
 * Created by victor on 15/1/18.
 */

interface ProfileRepository {
    fun updateInfo(user: User)

    fun updateAcademic(academic: Academic)

    fun addSubject(subject: ArrayList<Subject>)

    fun removeSubject(subject: String)

    fun updatePhotoUser(photo: String)

    fun getPreferences()

    fun getAcademic()

    fun getDataUser()
}