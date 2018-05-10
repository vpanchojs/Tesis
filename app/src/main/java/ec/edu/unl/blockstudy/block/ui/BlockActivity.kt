package ec.edu.unl.blockstudy.block.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.block.BlockPresenter
import ec.edu.unl.blockstudy.block.adapter.AnswerSelectAdapter
import ec.edu.unl.blockstudy.block.adapter.onAnswerAdapterListener
import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.entities.QuestionPath
import kotlinx.android.synthetic.main.activity_block.*
import javax.inject.Inject


class BlockActivity : AppCompatActivity(), BlockView, onAnswerAdapterListener, View.OnClickListener {

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btn_unlock -> {
                finish()
            }
            R.id.fab_change_question -> {
                getQuestion()
            }
        }
    }

    lateinit var questionPathList: List<QuestionPath>
    lateinit var adapter: AnswerSelectAdapter
    var answersList = ArrayList<Answer>()


    companion object {
        const val QUESTIONS_PATH_PARAM = "questionsPath"
    }

    @Inject
    lateinit var presenter: BlockPresenter
    lateinit var myApplication: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block)
        questionPathList = intent.getParcelableArrayListExtra(QUESTIONS_PATH_PARAM)
        //Log.e("BLOQUEO", "Numero de questions path ${questionPathList.size}")
        setupInject()
        setupRecycler()
        presenter.onSuscribe()
        btn_unlock.setOnClickListener(this)
        fab_change_question.setOnClickListener(this)
        getQuestion()
    }


    fun getQuestion() {
        presenter.getQuestion(randomQuestionsPath().path)
    }

    fun randomQuestionsPath(): QuestionPath {
        var num_pregunta = (Math.random() * questionPathList.size).toInt()
        return questionPathList.get(num_pregunta)
    }

    private fun setupInject() {
        myApplication = getApplication() as MyApplication
        myApplication.getBlockComponent(this).inject(this)
    }

    private fun setupRecycler() {
        adapter = AnswerSelectAdapter(answersList, this)
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

    override fun setDataQuestion(question: Question) {
        tv_statament.setText(question.statement)
        answersList.clear()
        answersList.addAll(question.answers)
        adapter.notifyDataSetChanged()
        Log.e("BLoqueo", "la pregunta es ${question.statement}")
    }

    override fun hideProgressDialog() {

    }

    override fun showProgressDialog(message: Int) {

    }

    override fun showMessagge(message: String) {

    }
}
