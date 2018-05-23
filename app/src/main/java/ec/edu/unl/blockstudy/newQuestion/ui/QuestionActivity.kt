package ec.edu.unl.blockstudy.newQuestion.ui;

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import ec.edu.unl.blockstudy.MyApplication
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.entities.Question
import ec.edu.unl.blockstudy.newQuestion.QuestionPresenter
import ec.edu.unl.blockstudy.newQuestion.adapters.SlidePagerAdapter
import ec.edu.unl.blockstudy.util.BaseActivitys
import kotlinx.android.synthetic.main.activity_new_question.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class QuestionActivity : AppCompatActivity(), View.OnClickListener, QuestionView {

    private val TAG = "Question"

    lateinit var adapter: SlidePagerAdapter
    lateinit var application: MyApplication
    @Inject
    lateinit var presenter: QuestionPresenter

    lateinit var question: Question
    private var statement = HashMap<String, String>()
    private val MY_PERMISSIONS_REQUEST_CODE = 1

    companion object {
        val ID_QUESTIONNAIRE_PARAM = "id_questionnaire"
        val QUESTION_PARAM = "id_question"
        val POSITION_PARAM = "position"
    }

    lateinit var idQuestionnaire: String
    lateinit var idQuestion: String
    var position: Int = -1

    var answerList: ArrayList<Answer>? = ArrayList<Answer>()

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_question -> {
                Log.e(TAG, "pregunta" + statement.get("statament"))
                if (ValidateData() && !statement.get("statament")!!.isBlank()) {
                    if (position < 0) {
                        presenter.onCreateQuestion(statement.get("photo")!!, answerList!!, statement.get("statament")!!, idQuestionnaire)
                    } else {
                        presenter.updateQuestion(question.idCloud, statement.get("statament")!!, statement.get("photo")!!, answerList, idQuestionnaire)
                    }

                } else {
                    showMessagge("Existen Campos Vacios")
                }

            }

        }
    }

    private fun setupInjection() {
        application = getApplication() as MyApplication
        application.getQuestionComponent(this).inject(this)
    }


    fun ValidateData(): Boolean {
        var cont = 0
        answerList!!.forEach {
            if (it.correct == true) cont++ else cont--
        }

        if (Math.abs(cont) < answerList!!.size) {
            return true
        } else {
            showMessagge(R.string.exist_correct_response)
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_question)
        btn_add_question.setOnClickListener(this)
        setupInjection()
        presenter.onResume()
        statement.put("statament", "")
        statement.put("photo", " ")
        getDataAndEditOrNewQuestion()
        vp_content_question.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        //pg_indicator.progress = 1
                    }
                    1 -> {
                        //pg_indicator.progress = 2
                    }
                }
            }
        })

    }

    fun getDataAndEditOrNewQuestion() {
        if (intent.extras != null) {
            idQuestionnaire = intent.extras.getString(QuestionActivity.ID_QUESTIONNAIRE_PARAM, "")
            position = intent.extras.getInt(QuestionActivity.POSITION_PARAM, -1)
            if (position < 0) {
                setupToolbar("Nueva Pregunta")
                btn_add_question.text = "Crear"
                adapter = SlidePagerAdapter(statement!!, answerList!!, supportFragmentManager)
                vp_content_question.adapter = adapter
                tabs.setupWithViewPager(vp_content_question)
                tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(vp_content_question))
            } else {
                setupToolbar("Editar Pregunta")
                btn_add_question.text = "Editar"
                question = intent.extras.getParcelable(QuestionActivity.QUESTION_PARAM)
                //position = intent.extras.getInt(QuestionActivity.POSITION_PARAM)
                //presenter.onGetDataQuestion(question.idCloud, idQuestionnaire)
                setDataQuestion(question)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    private fun setupToolbar(title: String) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onPause()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_question, menu)
        if (position < 0) {
            menu!!.findItem(R.id.action_delete).setVisible(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                createSimpleDialog().show()
                return true
            }
            R.id.action_delete -> {
                createConfirmDeleteDialog().show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onBackPressed() {
        createSimpleDialog().show()
    }


    fun createSimpleDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Mensaje de confirmación")
                .setMessage("Desea salir?")
                .setPositiveButton("ACEPTAR"
                ) { _, _ ->
                    val resultData = Intent()
                    setResult(0, resultData)
                    finish()
                }
                .setNegativeButton("CANCELAR",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {}
                        })
        return builder.create()
    }

    fun createConfirmDeleteDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Mensaje de confirmación")
                .setMessage("Desea Eliminar la pregunta?")
                .setPositiveButton("ACEPTAR"
                ) { _, which ->
                    presenter.onDeteleQuestion(question.idCloud, idQuestionnaire)
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

    override fun setDataQuestion(ques: Question) {
        answerList!!.addAll(question.answers)
        statement.put("statament", question.statement)
        statement.put("photo", question.photoUrl)
        adapter = SlidePagerAdapter(statement, answerList!!, supportFragmentManager)
        vp_content_question.adapter = adapter
        tabs.setupWithViewPager(vp_content_question)
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(vp_content_question))
    }

    override fun setDataAnswers(anserws: List<Answer>) {
        answerList!!.addAll(anserws)
        statement.put("statament", question.statement)
        statement.put("photo", question.photoUrl)
        adapter = SlidePagerAdapter(statement, answerList!!, supportFragmentManager)
        vp_content_question.adapter = adapter
        tabs.setupWithViewPager(vp_content_question)
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(vp_content_question))
    }

    override fun setNavigationQuestionnnaire() {
        finish()
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CODE)
            return false
        } else {
            return true
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CODE -> if (grantResults.size > 0) {
                when (permissions[0]) {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        else
                            Toast.makeText(this, "Permisos necesarios para funcionamiento mostrar el mapa ", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
