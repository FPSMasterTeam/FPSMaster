package top.fpsmaster.interfaces.client

interface IConstantsProvider {
    fun getVersion(): String
    fun getEdition(): String
}