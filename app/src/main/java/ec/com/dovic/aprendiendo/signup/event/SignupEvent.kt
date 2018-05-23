package ec.com.dovic.aprendiendo.signup.event

import javax.inject.Inject

/**
 * Created by Yavac on 15/1/2018.
 */
class SignupEvent{

    companion object {
        const val SUCCESS_SIGNUP = 1
        const val ERROR_SIGNUP = 0
    }

    private var eventType: Int = 0
    private var eventMsg: String? = null


    fun getEventType() : Int = eventType

    fun setEventType(eventType: Int) {
        this.eventType = eventType
    }

    fun getEventMsg(): String? = eventMsg

    fun setEventMsg(eventMsg: String) {
        this.eventMsg = eventMsg
    }


}