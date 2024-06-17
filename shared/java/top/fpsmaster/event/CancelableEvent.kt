package top.fpsmaster.event

open class CancelableEvent : Event {
    var isCanceled = false
        private set

    fun cancel() {
        isCanceled = true
    }
}
