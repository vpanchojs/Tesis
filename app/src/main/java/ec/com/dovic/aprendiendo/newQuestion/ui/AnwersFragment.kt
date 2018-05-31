package ec.com.dovic.aprendiendo.newQuestion.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ec.com.dovic.aprendiendo.R
import ec.com.dovic.aprendiendo.entities.Answer
import ec.com.dovic.aprendiendo.newQuestion.adapters.AnswerAdapter
import ec.com.dovic.aprendiendo.newQuestion.adapters.onAnswerAdapterListener
import ec.com.dovic.aprendiendo.util.BaseActivitys
import kotlinx.android.synthetic.main.fragment_anwers.view.*

class AnwersFragment : Fragment(), onAnswerAdapterListener, View.OnClickListener {

    private val REQ_CODE_SPEECH_INPUT = 0
    lateinit var tie_answer: TextInputEditText

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.fab_add_answer -> {
                generateAnswer(1)
                adapter.notifyItemInserted(adapter.getItemCount() - 1)
            }
        }
    }

    override fun onRemoveAnswer(position: Answer) {
        if (mAnswers!!.size > 2) {
            adapter.removeItem(position)
        } else {
            BaseActivitys.showToastMessage(context!!, "Deben existir respuestas", Toast.LENGTH_SHORT)
        }
    }

    fun generateAnswer(num: Int) {
        for (a in 1..num) {
            mAnswers!!.add(Answer())
        }
    }

    private var mAnswers: ArrayList<Answer>? = null
    lateinit var adapter: AnswerAdapter
    lateinit var rv_answer: RecyclerView
    lateinit var fab_add_answer: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mAnswers = arguments!!.getParcelableArrayList(ARG_ANSWERS)
            if (mAnswers!!.size == 0) {
                generateAnswer(2)
            }
            adapter = AnswerAdapter(mAnswers!!, this)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_anwers, container, false)
        rv_answer = view.rv_answer
        fab_add_answer = view.fab_add_answer
        fab_add_answer.setOnClickListener(this)
        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        rv_answer.layoutManager = LinearLayoutManager(context)
        rv_answer.adapter = adapter
    }

    fun getAnswers(): ArrayList<Answer> {
        return mAnswers!!
    }

    override fun onRequestStatament(tie_answer: TextInputEditText) {
        this.tie_answer = tie_answer
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dicte el enunciado de la respuesta")

        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    tie_answer.setText(result[0].toString())
                }
            }
        }
    }

    companion object {

        private val ARG_ANSWERS = "answers"
        fun newInstance(answers: ArrayList<Answer>): AnwersFragment {
            val fragment = AnwersFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_ANSWERS, answers)
            fragment.arguments = args
            return fragment
        }
    }
}
