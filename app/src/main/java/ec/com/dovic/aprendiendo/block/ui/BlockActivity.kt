package ec.com.dovic.aprendiendo.block.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.block.BlockPresenter
import ec.com.dovic.aprendiendo.block.adapter.AnswerSelectAdapter
import ec.com.dovic.aprendiendo.database.AnswerBd
import ec.com.dovic.aprendiendo.database.QuestionBd
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.util.GlideApp
import kotlinx.android.synthetic.main.activity_block.*
import javax.inject.Inject


class BlockActivity : AppCompatActivity(), BlockView, View.OnClickListener {

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btn_unlock -> {
                if (validateAnswers()) {
                    finish()
                } else {
                    visibilyWindowsInfo(View.VISIBLE)
                    //getQuestion()
                    setDataQuestion(randomQuestions())
                }
            }
            R.id.fab_change_question -> {
                setDataQuestion(randomQuestions())
            }
            R.id.btn_change_1 -> {
                visibilyWindowsInfo(View.GONE)
            }
        }
    }

    private fun visibilyWindowsInfo(visible: Int) {
        cl_windows_info.visibility = visible
    }

    private fun validateAnswers(): Boolean {
        var validated = true
        adapter.data.forEach {
            if (it.correct != it.select)
                validated = false
        }

        return validated
    }

    lateinit var questionnaireList: List<QuestionnaireBd>
    lateinit var adapter: AnswerSelectAdapter
    var questionsList = ArrayList<QuestionBd>()
    var answersList = ArrayList<AnswerBd>()


    companion object {
        const val QUESTIONNAIRE_PATH_PARAM = "questionnaire"
    }

    @Inject
    lateinit var presenter: BlockPresenter
    lateinit var myApplication: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block)
        questionnaireList = intent.getParcelableArrayListExtra(QUESTIONNAIRE_PATH_PARAM)
        //Log.e("BLOQUEO", "Numero de questions path ${questionPathList.size}")
        setupInject()
        setupRecycler()
        presenter.onSuscribe()
        btn_unlock.setOnClickListener(this)
        fab_change_question.setOnClickListener(this)
        btn_change_1.setOnClickListener(this)
        getQuestion()
    }


    fun getQuestion() {
        var idsQuestionnaire = ArrayList<Long>()
        questionnaireList.forEach {
            idsQuestionnaire.add(it.id)
        }
        presenter.getQuestion(idsQuestionnaire)

    }


    fun randomQuestions(): QuestionBd {
        var num_pregunta = (Math.random() * questionsList.size).toInt()
        return questionsList.get(num_pregunta)
    }


    private fun setupInject() {
        myApplication = getApplication() as MyApplication
        myApplication.getBlockComponent(this).inject(this)
    }

    private fun setupRecycler() {
        adapter = AnswerSelectAdapter(answersList)
        rv_answer.layoutManager = LinearLayoutManager(this)

        val mDividerItemDecoration = DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL)
        rv_answer.addItemDecoration(mDividerItemDecoration)
        rv_answer.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onUnSuscribe()
    }

    fun setDataQuestion(question: QuestionBd) {

        tv_statament.setText(question.statement)
        if (question.photoUrl.isBlank()) {
            iv_photo_question.visibility = View.GONE
        } else {
            iv_photo_question.visibility = View.VISIBLE
            GlideApp.with(this)
                    .load(question.photoUrl)
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .centerCrop()
                    .error(R.drawable.ic_person_black_24dp)
                    .into(iv_photo_question)
        }

        Log.e("BLoqueo", "la pregunta es ${question.statement}")
        answersList.clear()
        answersList.addAll(question.answers)
        adapter.notifyDataSetChanged()
    }

    override fun hideProgressDialog() {

    }

    override fun showProgressDialog(message: Int) {

    }

    override fun showMessagge(message: String) {

    }

    override fun setQuestionsAll(questions: ArrayList<QuestionBd>) {
        questionsList.addAll(questions)
        setDataQuestion(randomQuestions())
    }
}
