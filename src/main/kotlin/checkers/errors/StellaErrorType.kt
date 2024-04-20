package checkers.errors

enum class StellaErrorType {
    ERROR_MISSING_MAIN,
    ERROR_UNDEFINED_VARIABLE,
    ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
    ERROR_NOT_A_FUNCTION,
    ERROR_NOT_A_TUPLE,
    ERROR_NOT_A_RECORD,
    ERROR_NOT_A_LIST,
    ERROR_UNEXPECTED_LAMBDA,
    ERROR_UNEXPECTED_TYPE_FOR_PARAMETER,
    ERROR_UNEXPECTED_TUPLE,
    ERROR_UNEXPECTED_RECORD,
    ERROR_UNEXPECTED_LIST,
    ERROR_UNEXPECTED_INJECTION,
    ERROR_MISSING_RECORD_FIELDS,
    ERROR_UNEXPECTED_RECORD_FIELDS,
    ERROR_UNEXPECTED_FIELD_ACCESS,
    ERROR_TUPLE_INDEX_OUT_OF_BOUNDS,
    ERROR_UNEXPECTED_TUPLE_LENGTH,
    ERROR_AMBIGUOUS_SUM_TYPE,
    ERROR_ILLEGAL_EMPTY_MATCHING,
    ERROR_NONEXHAUSTIVE_MATCH_PATTERNS,
    ERROR_NONEXHAUSTIVE_LET_PATTERNS,
    ERROR_DUPLICATE_VARIANT_TYPE_FIELDS,
    ERROR_UNEXPECTED_PATTERN_FOR_TYPE,
    ERROR_UNEXPECTED_VARIANT_LABEL,
    ERROR_UNEXPECTED_VARIANT,
    ERROR_AMBIGUOUS_VARIANT_TYPE,
    ERROR_AMBIGUOUS_LIST_TYPE,
    ERROR_UNEXPECTED_REFERENCE,
    ERROR_NOT_A_REFERENCE,
    ERROR_EXCEPTION_TYPE_NOT_DECLARED,
    ERROR_UNEXPECTED_SUBTYPE,
    ERROR_AMBIGUOUS_PANIC_TYPE,
    ERROR_AMBIGUOUS_THROW_TYPE,
    ERROR_AMBIGUOUS_REFERENCE_TYPE,
    ERROR_UNEXPECTED_MEMORY_ADDRESS,
    ERROR_MISSING_TYPE_FOR_LABEL,
    ERROR_UNEXPECTED_TYPE_FOR_NULLARY_LABEL,

    // additional errors for #nullary-functions and #multiparameter-functions
    ERROR_INCORRECT_ARITY_OF_MAIN,
    ERROR_INCORRECT_NUMBER_OF_ARGUMENTS,
    ERROR_UNEXPECTED_NUMBER_OF_PARAMETERS_IN_LAMBDA,

    //additional errors for #nullary-variant-labels
    ERROR_UNEXPECTED_DATA_FOR_NULLARY_LABEL,
    ERROR_MISSING_DATA_FOR_LABEL,
    ERROR_UNEXPECTED_NON_NULLARY_VARIANT_PATTERN,
    ERROR_UNEXPECTED_NULLARY_VARIANT_PATTERN,

    ERROR_AMBIGUOUS_PATTERN_TYPE,
}
