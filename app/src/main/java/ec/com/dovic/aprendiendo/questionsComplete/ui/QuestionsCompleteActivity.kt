package ec.com.dovic.aprendiendo.questionsComplete.ui

import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import ec.com.dovic.aprendiendo.MyApplication
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.database.AnswerBd
import ec.com.dovic.aprendiendo.database.QuestionBd
import ec.com.dovic.aprendiendo.database.QuestionnaireBd
import ec.com.dovic.aprendiendo.entities.Answer
import ec.com.dovic.aprendiendo.questionsComplete.QuestionCompletePresenter
import ec.com.dovic.aprendiendo.questionsComplete.adapter.AnswerAdapter
import ec.com.dovic.aprendiendo.util.BaseActivitys
import ec.com.dovic.aprendiendo.util.GlideApp
import kotlinx.android.synthetic.main.activity_questions_view.*
import kotlinx.android.synthetic.main.fragment_questions_view.*
import javax.inject.Inject


class QuestionsCompleteActivity : AppCompatActivity(), QuestionCompleteView, ViewPager.OnPageChangeListener {

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

        /*
        tv_statament.setText(questions.get(position).statement)
//        presenter.onGetAnswersQuestion(questionaire.idCloud, questions.get(position).idCloud)

        if (!questions.get(position).photoUrl.isNullOrBlank()) {
            iv_photo_question.visibility = View.VISIBLE

            GlideApp.with(this)
                    .load(questions.get(position).photoUrl)
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .centerCrop()
                    .error(R.drawable.ic_person_black_24dp)
                    .into(iv_photo_question)

        } else {
            iv_photo_question.visibility = View.INVISIBLE
        }
        */

    }

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    var questions = ArrayList<QuestionBd>()
    lateinit var questionaire: QuestionnaireBd
    var idQuestionnaire: Long = 0
    lateinit var application: MyApplication

    companion object {
        val QUESTIONNAIRE_PARAM = "questionnaire"
    }

    @Inject
    lateinit var presenter: QuestionCompletePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions_view)
        if (intent.extras != null) {
            questionaire = intent.extras.getParcelable(QUESTIONNAIRE_PARAM)
        }
        setupInjection()
        presenter.onSuscribe()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, questions)
        container.adapter = mSectionsPagerAdapter
        tabs.setupWithViewPager(container)
        tabs.tabMode = TabLayout.MODE_SCROLLABLE
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        container.addOnPageChangeListener(this)

        presenter.onGetQuestionAll(questionaire.id)
        //setQuestions(ArrayList<QuestionBd>questionaire.questions)
    }


    private fun setupInjection() {
        application = getApplication() as MyApplication
        application.getQuestionCompleteComponent(this).inject(this)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_questions_view, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onUnSuscribe()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_more_info -> {
                var infoQuestionnaireFragment = InfoQuestionnaireFragment.newInstance(questionaire)
                infoQuestionnaireFragment.show(supportFragmentManager, "info")

            }
            R.id.action_delete -> {
                createDeleteDialog().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun createDeleteDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Mensaje de confirmación")
                .setMessage("¿Desea eliminar el cuestionario?")
                .setPositiveButton("ACEPTAR"
                ) { _, which ->
                    presenter.deleteQuestionnarie(questionaire)
                }
                .setNegativeButton("CANCELAR",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {}
                        })
        return builder.create()
    }

    override fun showMessagge(message: Any) {
        BaseActivitys.showToastMessage(this, message, Toast.LENGTH_SHORT)
    }

    override fun showProgressDialog(message: Any) {
        BaseActivitys.showProgressDialog(this, message)
    }

    override fun hideProgressDialog() {
        BaseActivitys.hideProgressDialog()
    }

    override fun setQuestions(list: List<QuestionBd>) {
        questions.clear()
        questions.addAll(list)
        mSectionsPagerAdapter!!.notifyDataSetChanged()
        onPageSelected(1)

    }

    override fun setAnswer(answerList: List<Answer>) {
        questions.forEach {
            /*
             if (it.idCloud.equals(answerList[0].idQuestion)) {
                 var aux = it.answers as ArrayList<Answer>
                 aux.clear()
                 aux.addAll(answerList)
                 //Log.e("local", "respuestas " + it.answers.size)
             }
             mSectionsPagerAdapter!!.notifyDataSetChanged()
             */
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager, var questions: ArrayList<QuestionBd>) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(questions.get(position))
        }

        override fun getCount(): Int {
            return questions.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return (position + 1).toString()
        }


    }

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_questions_view, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val question = arguments!!.getParcelable(PARAM_QUESTION) as QuestionBd
            setupRecyclerView(question)
            setData(question)
        }

        private fun setData(question: QuestionBd) {
            tv_statament.text = question.statement
            if (question.photoUrl.isNullOrBlank()) {
                iv_photo_question.visibility = View.GONE

            } else {
                iv_photo_question.visibility = View.VISIBLE
                GlideApp.with(this)
                        .load(question.photoUrl)
                        .centerCrop()
                        .into(iv_photo_question)
            }
        }

        private fun setupRecyclerView(question: QuestionBd) {
            val answerAdapter = AnswerAdapter(question.answers as ArrayList<AnswerBd>)
            rv_answer.layoutManager = LinearLayoutManager(context)
            val mDividerItemDecoration = DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL)
            rv_answer.addItemDecoration(mDividerItemDecoration)
            rv_answer.adapter = answerAdapter
        }


        companion object {
            val PARAM_QUESTION = "question"
            fun newInstance(question: QuestionBd): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putParcelable(PARAM_QUESTION, question)
                fragment.arguments = args
                return fragment
            }
        }
    }

    override fun none_results(show: Boolean) {

    }

    override fun closeActivity() {
        finish()
    }
}
