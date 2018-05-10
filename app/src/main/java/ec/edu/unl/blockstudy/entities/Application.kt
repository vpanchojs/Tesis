package ec.edu.unl.blockstudy.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

/**
 * Created by victor on 3/4/18.
 */
@Entity
class Application {
    @Id
    var id: Long = 0
    lateinit var app: String
    lateinit var block: ToOne<Block>
    lateinit var idUser: String
}

