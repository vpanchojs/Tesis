package ec.com.dovic.aprendiendo.menu.ui

import ec.com.dovic.aprendiendo.entities.User

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
    fun singOut(i: Int)

}