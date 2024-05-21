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
    Predecessor("predecessor"),

    Sequencing("sequencing"),
    References("references"),
    Exceptions("exceptions"),
    ExceptionTypeDeclaration("exception-type-declaration"),
    OpenVariantExceptions("open-variant-exceptions"),
    TryCastAs("try-cast-as"),
    TopType("top-type"),
    BottomType("bottom-type"),
    StructuralSubtyping("structural-subtyping"),
    TypeCast("type-cast"),
    Panic("panic"),
    AmbiguousTypeAsBottom("ambiguous-type-as-bottom"),

    // additional extensions
    NaturalLiterals("natural-literals"),
    NestedFunctionDeclarations("nested-function-declarations"),
    NullaryFunctions("nullary-functions"),
    MultiparameterFunctions("multiparameter-functions"),
    StructuralPatterns("structural-patterns"),
    NullaryVariantLabels("nullary-variant-labels"),
    LetrecBindings("letrec-bindings"),
    LetrecManyBindings("letrec-many-bindings"),
    LetPatterns("let-patterns"),
    PatternAscriptions("pattern-ascriptions"),
    ArithmeticOperators("arithmetic-operators"),
    TypeCastPatterns("type-cast-patterns"),
    TypeReconstruction("type-reconstruction"),
    UniversalTypes("universal-types");

    companion object {
        fun fromString(str: String): StellaExtension? {
            return entries.firstOrNull { it.extensionName == str }
        }
    }
}
