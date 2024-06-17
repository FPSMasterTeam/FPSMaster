package top.fpsmaster.modules.music

class Line {
    var words = ArrayList<Word>()
    var time: Long = 0
    var duration: Long = 0
    var timeTick: String? = null
    var type = 0
    var animation = 0f
    var alpha = 0f
    fun addWord(word: Word) {
        words.add(word)
    }

    val content: String
        get() {
            val stringBuilder = StringBuilder()
            for (word in words) {
                stringBuilder.append(word.content)
            }
            return stringBuilder.toString()
        }
}
