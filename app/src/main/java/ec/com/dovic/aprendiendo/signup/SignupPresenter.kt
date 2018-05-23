package ec.com.dovic.aprendiendo.signup

import ec.com.dovic.aprendiendo.signup.event.SignupEvent


interface SignupPresenter {

    fun onResume()
    fun onPause()
    fun onDestroy()
    fun onSignUp(name: String, lastname: String, emai: String, password: String)
    fun onEventSignupThread(signupEvent: SignupEvent)

}