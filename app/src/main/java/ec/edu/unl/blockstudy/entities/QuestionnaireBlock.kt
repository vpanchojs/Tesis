package ec.edu.unl.blockstudy.entities

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
class QuestionnaireBlock {
    @Id
    var id: Long = 0
    var idQuestionnaire: Long = 0
    lateinit var idCloud: String
    var offline: Boolean = false
    lateinit var block: ToOne<Block>
    @Backlink
    lateinit var questionsPath: ToMany<QuestionPath>

}