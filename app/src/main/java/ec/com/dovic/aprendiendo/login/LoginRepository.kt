package ec.com.dovic.aprendiendo.login

/**
 * Created by victor on 15/1/18.
 */

interface LoginRepository {
    fun onSignIn(email: String, password: String)
    fun onInSession()
    fun onInSessionRemove()
    fun onRecoveryPassword(email: String)
}