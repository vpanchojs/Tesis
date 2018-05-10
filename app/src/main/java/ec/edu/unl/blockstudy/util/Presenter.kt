package ec.edu.unl.blockstudy.util

import ec.edu.unl.blockstudy.newQuestion.events.QuestionEvents

/**
 * Created by victor on 6/3/18.
 */
interface Presenter {
    fun onSuscribe()

    fun onUnSuscribe()
}