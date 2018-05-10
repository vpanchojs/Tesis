package ec.edu.unl.blockstudy.login

/**
 * Created by victor on 15/1/18.
 */
interface LoginInteractor {
    fun onSignIn(email: String, password: String)
    fun onInSession()
    fun onInSessionRemove()
    fun onRecoveryPassword(email: String)
}