enum class StellaExtension(val extensionName: String) {
    UnitType("unit-type"),
    Pairs("pairs"),
    Tuples("tuples"),
    Records("records"),
    LetBindings("let-bindings"),
    TypeAscriptions("type-ascriptions"),
    SumTypes("sum-types"),
    Lists("lists"),
    Variants("variants"),
    FixpointCombinator("fixpoint-combinator"),

    // additional extensions
    NaturalLiterals("natural-literals"),
    NestedFunctionDeclarations("nested-function-declarations"),
    NullaryFunctions("nullary-functions"),
    MultiparameterFunctions("multiparameter-functions"),
    StructuralPatterns("structural-patterns"),
    NullaryVariantLabels("nullary-variant-labels"),
    LetrecBindings("letrec-bindings"),
    LetrecManyBindings("letrec-many-bindings");

    companion object {
        fun fromString(str: String): StellaExtension {
            return entries.firstOrNull { it.extensionName == str } ?: error("can't find extension $str")
        }
    }
}
