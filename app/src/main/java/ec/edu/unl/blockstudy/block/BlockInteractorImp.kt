package ec.edu.unl.blockstudy.block

class BlockInteractorImp(var repository: BlockRepository) : BlockInteractor {

    override fun getQuestion(ids: ArrayList<Long>) {
        repository.getQuestion(ids)
    }

}