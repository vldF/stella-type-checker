package checkers.types

import StellaExtension

class ExtensionManager {
    private val enabledExtensions = mutableSetOf<StellaExtension>()

    val typeReconstruction: Boolean
        get() = StellaExtension.TypeReconstruction in enabledExtensions

    fun enableExtensions(extensions: List<StellaExtension>) {
        enabledExtensions.addAll(extensions)
    }
}
