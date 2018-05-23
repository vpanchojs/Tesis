package ec.com.dovic.aprendiendo.block.ui

import ec.com.dovic.aprendiendo.database.QuestionBd

interface BlockView {
    fun hideProgressDialog();
    fun showProgressDialog(message: Int)
    fun showMessagge(message: String)
    fun setQuestionsAll(questions: ArrayList<QuestionBd>)
}