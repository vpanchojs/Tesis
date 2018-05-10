package ec.edu.unl.blockstudy.profile.ui

import ec.edu.unl.blockstudy.entities.Academic
import ec.edu.unl.blockstudy.entities.Subject
import ec.edu.unl.blockstudy.entities.User

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

}