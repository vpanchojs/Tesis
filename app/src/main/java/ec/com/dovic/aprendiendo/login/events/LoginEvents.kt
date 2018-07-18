package ec.com.dovic.aprendiendo.login.events

class LoginEvents(var type: Int, var any: Any) {

    companion object {
        val onSignInSuccess = 0
        val onSignInError = 1
        val onRecoverySession = 2
        val onRecoveryPasswordSuccess = 3
        val onRecoveryPasswordError = 4
        val onSignInSuccessNoValidEmail = 5
        val ON_SIGIN_SUCCESS_FACEBOOK = 6
        val ON__SIGIN_ERROR = 7
        val ON__SIGIN_SUCCES_GOOGLE = 8
    }

}