package ec.com.dovic.aprendiendo.util

import ec.com.dovic.aprendiendo.newQuestion.events.QuestionEvents

/**
 * Created by victor on 6/3/18.
 */
interface Presenter {
    fun onSuscribe()

    fun onUnSuscribe()
}