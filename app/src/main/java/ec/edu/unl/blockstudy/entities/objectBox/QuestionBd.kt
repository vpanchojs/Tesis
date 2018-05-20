package ec.edu.unl.blockstudy.entities.objectBox

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
class QuestionBd {
    @Id
    var id: Long = 0
    var statement: String = ""
    var photoUrl: String = ""
    lateinit var questionnaireBd: ToOne<QuestionnaireBd>

    @Backlink
    lateinit var answers: ToMany<AnswerBd>
}