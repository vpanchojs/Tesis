package ec.com.dovic.aprendiendo.login

import android.net.Uri
import ec.com.dovic.aprendiendo.entities.User

/**
 * Created by victor on 15/1/18.
 */
class LoginInteractorImp(var repository: LoginRepository) : LoginInteractor {

    override fun onSignIn(email: String, password: String) {
        repository.onSignIn(email, password)
    }

    override fun onInSession() {
        repository.onInSession()
    }

    override fun onInSessionRemove() {
        repository.onInSessionRemove()
    }

    override fun onRecoveryPassword(email: String) {
        repository.onRecoveryPassword(email)
    }

    override fun sendEmailVerify() {
        repository.sendEmailVerify()
    }

    override fun tokenFacebook(token: String, name: String?, lastname: String?, email: String?, photoUrl: Uri?) {
        val user = User()
        user.photo = photoUrl.toString()
        user.lastname = lastname!!
        user.name = name!!
        user.email = email!!
        repository.enviartoken(token, user)
    }

    override fun tokenGoogle(idToken: String, name: String?, lastname: String?, email: String?, photoUrl: Uri?) {
        val user = User()
        user.photo = photoUrl.toString()
        user.lastname = lastname!!
        user.name = name!!
        user.email = email!!
        repository.enviartokengoogle(idToken, user)
    }
}