import java.util.*

class AppContext {
    val strings by lazy {
        val lang = "_sv"
        StringsWrapper(
            properties = Properties().also {
                it.load(javaClass.classLoader.getResourceAsStream("labels$lang.properties"))
            }
        )
    }
}

class StringsWrapper(private val properties: Properties) {
    operator fun get(key: String): String {
        return properties[key] as? String
            ?: throw IllegalStateException("label not found: $key")
    }
}