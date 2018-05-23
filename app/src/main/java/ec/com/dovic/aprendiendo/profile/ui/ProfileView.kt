package ec.com.dovic.aprendiendo.profile.ui

import ec.com.dovic.aprendiendo.entities.Academic
import ec.com.dovic.aprendiendo.entities.Subject
import ec.com.dovic.aprendiendo.entities.User

/**
 * Created by victor on 15/1/18.
 */
interface ProfileView {
    fun showMessagge(message: Any)
    fun showProgressDialog(message: Any)
    fun hideProgressDialog();
    fun setPreferences(preferences: ArrayList<Subject>)
    fun setAcademic(academics: ArrayList<Academic>)
    fun setPhoto()
    fun setInfoUser(user: User)
    fun setDataProfile(user: User)
    fun setPhoto(toString: String)

}