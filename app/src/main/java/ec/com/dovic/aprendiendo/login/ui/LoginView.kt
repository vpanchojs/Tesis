package ec.com.dovic.aprendiendo.login.ui

/**
 * Created by victor on 15/1/18.
 */
interface LoginView {
    fun showMessagge(message: String)
    fun showProgressDialog(message: Int)
    fun hideProgressDialog()
    fun navigationMain()
    fun showSnackBar(message: String)
    fun showButtonSignIn(visible: Int, message: String)
    fun showProgress(visible: Int)
}