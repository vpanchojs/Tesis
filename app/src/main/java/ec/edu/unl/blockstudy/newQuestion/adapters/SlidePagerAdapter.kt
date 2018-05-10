package ec.edu.unl.blockstudy.newQuestion.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ec.edu.unl.blockstudy.entities.Answer
import ec.edu.unl.blockstudy.newQuestion.ui.AnwersFragment
import ec.edu.unl.blockstudy.newQuestion.ui.StatementQuestionFragment

/**
 * Created by victor on 1/3/18.
 */
class SlidePagerAdapter(var statement: HashMap<String, String>, var answers: ArrayList<Answer>, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    var stateQuestionFragment: StatementQuestionFragment? = null
    var answerFragment: AnwersFragment? = null

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                stateQuestionFragment = StatementQuestionFragment.newInstance(statement)
                return stateQuestionFragment!!
            }
            1 -> {
                answerFragment = AnwersFragment.newInstance(answers)
                return answerFragment!!
            }
            else -> {
                return StatementQuestionFragment.newInstance(statement)
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return "Enunciado"
            }
            1 -> {
                return "Respuestas"
            }
            else -> {
                return ""
            }
        }
    }
}