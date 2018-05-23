package ec.com.dovic.aprendiendo.signup

import ec.com.dovic.aprendiendo.entities.User

/**
 * Created by Yavac on 15/1/2018.
 */
interface SignupRepository {
    fun onSignUp(user: User)
}