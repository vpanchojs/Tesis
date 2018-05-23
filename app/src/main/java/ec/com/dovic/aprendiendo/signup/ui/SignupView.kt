package ec.com.dovic.aprendiendo.signup.ui

/**
 * Created by Yavac on 15/1/2018.
 */
interface SignupView {

    fun hideProgressDialog();
    fun showProgressDialog(message: Int)
    fun showMessagge(message: String)
    fun navigationToMain();

}