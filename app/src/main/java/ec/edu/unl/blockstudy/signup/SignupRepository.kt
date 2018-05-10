package ec.edu.unl.blockstudy.signup

import ec.edu.unl.blockstudy.entities.User

/**
 * Created by Yavac on 15/1/2018.
 */
interface SignupRepository {
    fun onSignUp(user: User)
}