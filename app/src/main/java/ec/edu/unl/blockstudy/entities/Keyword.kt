package ec.edu.unl.blockstudy.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Created by victor on 20/2/18.
 */
@Entity
class Keyword {
    @Id
    var id: Long = 0
    var description: String? = null

}