package ec.edu.unl.blockstudy.entities.objectBox

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
class AnswerBd {
    @Id
    var id: Long = 0
    var statement: String? = ""
    var correct: Boolean? = false
    var select: Boolean? = false

    lateinit var question: ToOne<QuestionBd>
}