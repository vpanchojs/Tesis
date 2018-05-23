package ec.edu.unl.blockstudy.newQuestion.ui

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ec.edu.unl.blockstudy.R
import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.newQuestion.adapters.AnswerAdapter
import ec.edu.unl.blockstudy.newQuestion.adapters.onAnswerAdapterListener
import ec.edu.unl.blockstudy.util.BaseActivitys
import kotlinx.android.synthetic.main.fragment_anwers.view.*

class AnwersFragment : Fragment(), onAnswerAdapterListener, View.OnClickListener {

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
