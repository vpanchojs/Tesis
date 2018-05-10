package ec.edu.unl.blockstudy.login.ui

/**
 * Created by victor on 15/1/18.
 */
interface LoginView {
    fun showMessagge(message: String)
    fun showProgressDialog(message: Int)
    fun hideProgressDialog();
    fun navigationMain();
}