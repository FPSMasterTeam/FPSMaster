package top.fpsmaster.features.command

abstract class Command(var name: String) {
    abstract fun execute(args: Array<String>)
}