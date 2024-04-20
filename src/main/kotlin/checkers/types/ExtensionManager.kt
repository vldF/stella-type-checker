package checkers.types

import StellaExtension

class ExtensionManager {
    private val enabledExtensions = mutableSetOf<StellaExtension>()

    val ambiguousTypeAsBottom: Boolean
        get() = StellaExtension.AmbiguousTypeAsBottom in enabledExtensions

    val structuralSubtyping: Boolean
        get() = StellaExtension.StructuralSubtyping in enabledExtensions

    fun enableExtensions(extensions: List<StellaExtension>) {
        enabledExtensions.addAll(extensions)
    }
}
