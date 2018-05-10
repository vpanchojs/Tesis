package ec.edu.unl.blockstudy.login

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
}