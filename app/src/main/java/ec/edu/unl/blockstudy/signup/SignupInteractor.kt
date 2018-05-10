package ec.edu.unl.blockstudy.signup

/**
 * Created by Yavac on 15/1/2018.
 */
interface SignupInteractor {

    fun onSignUp(name: String, lastname: String, emai: String, password: String)

}