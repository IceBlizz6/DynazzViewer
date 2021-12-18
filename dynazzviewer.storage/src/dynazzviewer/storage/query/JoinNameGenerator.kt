package dynazzviewer.storage.query

class JoinNameGenerator {
    private var counter = 1

    fun generate(): String {
        return "q${counter++}"
    }
}
