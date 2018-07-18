package ec.com.dovic.aprendiendo.login

import ec.com.dovic.aprendiendo.entities.User

/**
 * Created by victor on 15/1/18.
 */

interface LoginRepository {
    fun onSignIn(email: String, password: String)
    fun onInSession()
    fun onInSessionRemove()
    fun onRecoveryPassword(email: String)
    fun sendEmailVerify()
    fun enviartoken(token: String, user: User)
    fun enviartokengoogle(idToken: String, user: User)
}