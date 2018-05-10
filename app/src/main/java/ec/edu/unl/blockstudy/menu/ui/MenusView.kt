package ec.edu.unl.blockstudy.menu.ui

import ec.edu.unl.blockstudy.entities.User

/**
 * Created by victor on 27/1/18.
 */
interface MenusView {
    fun hideProgressDialog();
    fun showProgressDialog(message: Int)
    fun showMessagge(message: String)
    fun navigationToProfile();
    fun navigationToTermsAndConditions()
    fun navigationToLogin()
    fun setDataProfile(user: User)

}