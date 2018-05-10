package ec.edu.unl.blockstudy.signup

import ec.edu.unl.blockstudy.entities.User

/**
 * Created by Yavac on 15/1/2018.
 */
class SignupInteractorImpl(var signupRepository: SignupRepository) : SignupInteractor {

    override fun onSignUp(name: String, lastname: String, emai: String, password: String) {
        signupRepository.onSignUp(User("", name, lastname, "", emai, password))

    }


}