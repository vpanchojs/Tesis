package ec.com.dovic.aprendiendo.profile

import ec.com.dovic.aprendiendo.entities.Academic
import ec.com.dovic.aprendiendo.entities.Subject
import ec.com.dovic.aprendiendo.entities.User

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