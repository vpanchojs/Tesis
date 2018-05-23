package ec.com.dovic.aprendiendo.login.events

class LoginEvents(var type: Int, var any: Any, var message: String) {

    companion object {
        val onSignInSuccess = 0
        val onSignInError = 1
        val onRecoverySession = 2
        val onRecoveryPasswordSuccess = 3
        val onRecoveryPasswordError = 4
    }

}