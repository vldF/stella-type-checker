package checkers.errors

object ErrorStrings {
    val strings = mapOf(
        StellaErrorType.ERROR_MISSING_MAIN to "main function is missing",
        StellaErrorType.ERROR_UNDEFINED_VARIABLE to "variable %s is undefined",
        StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION to "expected type %s but got %s for expression %s",
        StellaErrorType.ERROR_NOT_A_FUNCTION to "expected a function type but got %s for expression %s",
        StellaErrorType.ERROR_NOT_A_TUPLE to "expected an expression of tuple type but got expression of type %s in %s",
        StellaErrorType.ERROR_NOT_A_RECORD to "expected record but got expression of type %s in %s",
        StellaErrorType.ERROR_NOT_A_LIST to "expected list but got expression of type %s in %s",
        StellaErrorType.ERROR_UNEXPECTED_LAMBDA to "expected an expression of a non-function type %s but got function type %s for expression %s",
        StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_PARAMETER to "expected type %s but got type %s for parameter %s",
        StellaErrorType.ERROR_UNEXPECTED_TUPLE to "expected an expression of type %s but got tuple %s",
        StellaErrorType.ERROR_UNEXPECTED_RECORD to "expected an expression of type %s but got record %s",
        StellaErrorType.ERROR_UNEXPECTED_LIST to "expected an expression of type %s but got list %s",
        StellaErrorType.ERROR_UNEXPECTED_INJECTION to "TODO",
        StellaErrorType.ERROR_MISSING_RECORD_FIELDS to "missing field %s in record %s",
        StellaErrorType.ERROR_UNEXPECTED_RECORD_FIELDS to "unexpected record field %s in record %s",
        StellaErrorType.ERROR_UNEXPECTED_FIELD_ACCESS to "unexpected field access %s in record %s",
        StellaErrorType.ERROR_TUPLE_INDEX_OUT_OF_BOUNDS to "tuple index %s is out of bounds %s",
        StellaErrorType.ERROR_UNEXPECTED_TUPLE_LENGTH to "unexpected tuple length %s of tuple with length %s",
        StellaErrorType.ERROR_AMBIGUOUS_SUM_TYPE to "TODO",
        StellaErrorType.ERROR_AMBIGUOUS_LIST to "TODO",
        StellaErrorType.ERROR_ILLEGAL_EMPTY_MATCHING to "TODO",
        StellaErrorType.ERROR_NONEXHAUSTIVE_MATCH_PATTERNS to "TODO",
        StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE to "TODO",
    )
}