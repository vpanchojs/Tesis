package ec.edu.unl.blockstudy.block

class BlockInteractorImp(var repository: BlockRepository) : BlockInteractor {

    override fun getQuestion(questionPath: String) {
        repository.getQuestion(questionPath)
    }

}