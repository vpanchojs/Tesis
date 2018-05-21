package ec.edu.unl.blockstudy.block.ui

import ec.edu.unl.blockstudy.database.QuestionBd

interface BlockView {
    fun hideProgressDialog();
    fun showProgressDialog(message: Int)
    fun showMessagge(message: String)
    fun setQuestionsAll(questions: ArrayList<QuestionBd>)
}