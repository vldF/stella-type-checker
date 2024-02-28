import org.junit.jupiter.api.Test

// DO NOT MODIFY THIS FILE MANUALLY
// Edit TestsGenerator.kt instead

@Suppress("ClassName")
class OK_TESTS {
    @Test
    fun semicolon_test() {
        StellaTestsRunner.runOkTest("semicolon")
    }
    @Test
    fun record_in_record_test() {
        StellaTestsRunner.runOkTest("record_in_record")
    }
    @Test
    fun simple_tuple_test() {
        StellaTestsRunner.runOkTest("simple_tuple")
    }
    @Test
    fun simple_records_test() {
        StellaTestsRunner.runOkTest("simple_records")
    }
    @Test
    fun simple_sum_test() {
        StellaTestsRunner.runOkTest("simple_sum")
    }
    @Test
    fun let_let_test() {
        StellaTestsRunner.runOkTest("let_let")
    }
    @Test
    fun sum_arg_test() {
        StellaTestsRunner.runOkTest("sum_arg")
    }
    @Test
    fun let_rec_test() {
        StellaTestsRunner.runOkTest("let_rec")
    }
    @Test
    fun record_in_abstraction_test() {
        StellaTestsRunner.runOkTest("record_in_abstraction")
    }
    @Test
    fun twice_bool_not_test() {
        StellaTestsRunner.runOkTest("twice_bool_not")
    }
    @Test
    fun parenthesis_test() {
        StellaTestsRunner.runOkTest("parenthesis")
    }
    @Test
    fun let_bool_test() {
        StellaTestsRunner.runOkTest("let_bool")
    }
    @Test
    fun let_isempty_test() {
        StellaTestsRunner.runOkTest("let_isempty")
    }
    @Test
    fun int_literal_test() {
        StellaTestsRunner.runOkTest("int_literal")
    }
    @Test
    fun let_square_test() {
        StellaTestsRunner.runOkTest("let_square")
    }
    @Test
    fun list_operations_test() {
        StellaTestsRunner.runOkTest("list_operations")
    }
    @Test
    fun let_unit_test() {
        StellaTestsRunner.runOkTest("let_unit")
    }
    @Test
    fun record_apply_to_function_test() {
        StellaTestsRunner.runOkTest("record_apply_to_function")
    }
    @Test
    fun increment_twice_test() {
        StellaTestsRunner.runOkTest("increment_twice")
    }
    @Test
    fun fixpoint_test() {
        StellaTestsRunner.runOkTest("fixpoint")
    }
    @Test
    fun simple_pair_test() {
        StellaTestsRunner.runOkTest("simple_pair")
    }
    @Test
    fun list_ascription_test() {
        StellaTestsRunner.runOkTest("list_ascription")
    }
    @Test
    fun exhaustive_sum_arg_test() {
        StellaTestsRunner.runOkTest("exhaustive_sum_arg")
    }
    @Test
    fun let_if_test() {
        StellaTestsRunner.runOkTest("let_if")
    }
    @Test
    fun variant_attempt_test() {
        StellaTestsRunner.runOkTest("variant_attempt")
    }
    @Test
    fun simple_ascription_test() {
        StellaTestsRunner.runOkTest("simple_ascription")
    }
    @Test
    fun let_fun_test() {
        StellaTestsRunner.runOkTest("let_fun")
    }
    @Test
    fun simple_unit_test() {
        StellaTestsRunner.runOkTest("simple_unit")
    }
    
}

@Suppress("ClassName")
class ERROR_MISSING_MAIN_TESTS {
    @Test
    fun no_main_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_MISSING_MAIN, "no_main")
    }
    
}

@Suppress("ClassName")
class ERROR_NOT_A_LIST_TESTS {
    @Test
    fun head_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_LIST, "head")
    }
    @Test
    fun is_empty_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_LIST, "is_empty")
    }
    @Test
    fun tail_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_LIST, "tail")
    }
    
}

@Suppress("ClassName")
class ERROR_NONEXHAUSTIVE_MATCH_PATTERNS_TESTS {
    @Test
    fun nonexhaustive_match_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NONEXHAUSTIVE_MATCH_PATTERNS, "nonexhaustive_match")
    }
    @Test
    fun nonexhaustive_variant_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NONEXHAUSTIVE_MATCH_PATTERNS, "nonexhaustive_variant")
    }
    
}

@Suppress("ClassName")
class ERROR_ILLEGAL_EMPTY_MATCHING_TESTS {
    @Test
    fun empty_match_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_ILLEGAL_EMPTY_MATCHING, "empty_match")
    }
    
}

@Suppress("ClassName")
class ERROR_MISSING_RECORD_FIELDS_TESTS {
    @Test
    fun simple_missing_fields_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_MISSING_RECORD_FIELDS, "simple_missing_fields")
    }
    @Test
    fun record_in_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_MISSING_RECORD_FIELDS, "record_in_record")
    }
    @Test
    fun record_in_abstraction_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_MISSING_RECORD_FIELDS, "record_in_abstraction")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_TUPLE_TESTS {
    @Test
    fun succ_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TUPLE, "succ_record")
    }
    @Test
    fun application_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TUPLE, "application_record")
    }
    @Test
    fun return_tuple_from_function_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TUPLE, "return_tuple_from_function")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_RECORD_TESTS {
    @Test
    fun succ_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_RECORD, "succ_record")
    }
    @Test
    fun simple_unexpected_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_RECORD, "simple_unexpected_record")
    }
    @Test
    fun application_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_RECORD, "application_record")
    }
    
}

@Suppress("ClassName")
class ERROR_NOT_A_RECORD_TESTS {
    @Test
    fun nat_is_not_a_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_RECORD, "nat_is_not_a_record")
    }
    @Test
    fun if_is_not_a_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_RECORD, "if_is_not_a_record")
    }
    @Test
    fun unit_is_not_a_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_RECORD, "unit_is_not_a_record")
    }
    @Test
    fun func_is_not_a_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_RECORD, "func_is_not_a_record")
    }
    @Test
    fun bool_is_not_a_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_RECORD, "bool_is_not_a_record")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_TUPLE_LENGTH_TESTS {
    @Test
    fun return_tuple_literal_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TUPLE_LENGTH, "return_tuple_literal")
    }
    @Test
    fun functional_type_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TUPLE_LENGTH, "functional_type")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_VARIANT_LABEL_TESTS {
    @Test
    fun simple_unexpected_label_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_VARIANT_LABEL, "simple_unexpected_label")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_RECORD_FIELDS_TESTS {
    @Test
    fun return_record_with_missing_fields_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_RECORD_FIELDS, "return_record_with_missing_fields")
    }
    @Test
    fun call_function_with_missing_fields_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_RECORD_FIELDS, "call_function_with_missing_fields")
    }
    
}

@Suppress("ClassName")
class ERROR_UNDEFINED_VARIABLE_TESTS {
    @Test
    fun undefined_var_in_other_fun_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNDEFINED_VARIABLE, "undefined_var_in_other_fun")
    }
    @Test
    fun simple_undefined_var_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNDEFINED_VARIABLE, "simple_undefined_var")
    }
    @Test
    fun in_let_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNDEFINED_VARIABLE, "in_let")
    }
    
}

@Suppress("ClassName")
class ERROR_NOT_A_TUPLE_TESTS {
    @Test
    fun simple_not_a_tuple_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_TUPLE, "simple_not_a_tuple")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_INJECTION_TESTS {
    @Test
    fun simple_unexpected_injection_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_INJECTION, "simple_unexpected_injection")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_LIST_TESTS {
    @Test
    fun simple_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_LIST, "simple")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_TYPE_FOR_PARAMETER_TESTS {
    @Test
    fun recursion_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_PARAMETER, "recursion")
    }
    @Test
    fun return_lambda_with_wrong_second_argument_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_PARAMETER, "return_lambda_with_wrong_second_argument")
    }
    @Test
    fun return_lambda_with_wrong_argument_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_PARAMETER, "return_lambda_with_wrong_argument")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION_TESTS {
    @Test
    fun let_list_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "let_list")
    }
    @Test
    fun unexpected_iszero_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "unexpected_iszero")
    }
    @Test
    fun simple_let_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "simple_let")
    }
    @Test
    fun unexpected_isempty_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "unexpected_isempty")
    }
    @Test
    fun unexpected_tail_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "unexpected_tail")
    }
    @Test
    fun record_dot_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "record_dot")
    }
    @Test
    fun unexpected_label_type_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "unexpected_label_type")
    }
    @Test
    fun int_literal_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "int_literal")
    }
    @Test
    fun tuple_dot_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "tuple_dot")
    }
    @Test
    fun false_return_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "false_return")
    }
    @Test
    fun unexpected_unit_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "unexpected_unit")
    }
    @Test
    fun unexpected_application_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "unexpected_application")
    }
    @Test
    fun is_zero_bool_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "is_zero_bool")
    }
    @Test
    fun simple_ascription2_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "simple_ascription2")
    }
    @Test
    fun succ_true_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "succ_true")
    }
    @Test
    fun fixpoint_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "fixpoint")
    }
    @Test
    fun different_branches_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "different_branches")
    }
    @Test
    fun no_nat_rec_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "no_nat_rec")
    }
    @Test
    fun return_lambda_with_wrong_return_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "return_lambda_with_wrong_return")
    }
    @Test
    fun unexpected_s_rec_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "unexpected_s_rec")
    }
    @Test
    fun function_return_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "function_return")
    }
    @Test
    fun simple_ascription_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "simple_ascription")
    }
    @Test
    fun if_nat_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "if_nat")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_PATTERN_FOR_TYPE_TESTS {
    @Test
    fun variant_unexpected_pattern_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE, "variant_unexpected_pattern")
    }
    @Test
    fun unexpected_pattern_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE, "unexpected_pattern")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_FIELD_ACCESS_TESTS {
    @Test
    fun simple_field_access_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_FIELD_ACCESS, "simple_field_access")
    }
    
}

@Suppress("ClassName")
class ERROR_NOT_A_FUNCTION_TESTS {
    @Test
    fun not_a_f_fix_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_FUNCTION, "not_a_f_fix")
    }
    @Test
    fun before_arg_type_check_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_FUNCTION, "before_arg_type_check")
    }
    @Test
    fun simple_no_function_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_FUNCTION, "simple_no_function")
    }
    @Test
    fun apply_tuple_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_FUNCTION, "apply_tuple")
    }
    @Test
    fun apply_record_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_NOT_A_FUNCTION, "apply_record")
    }
    
}

@Suppress("ClassName")
class ERROR_AMBIGUOUS_LIST_TESTS {
    @Test
    fun let_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_AMBIGUOUS_LIST, "let")
    }
    @Test
    fun head_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_AMBIGUOUS_LIST, "head")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_VARIANT_TESTS {
    @Test
    fun simple_unexpected_variant_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_VARIANT, "simple_unexpected_variant")
    }
    
}

@Suppress("ClassName")
class ERROR_UNEXPECTED_LAMBDA_TESTS {
    @Test
    fun simple_unexpected_lambda_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_UNEXPECTED_LAMBDA, "simple_unexpected_lambda")
    }
    
}

@Suppress("ClassName")
class ERROR_TUPLE_INDEX_OUT_OF_BOUNDS_TESTS {
    @Test
    fun tuple_from_function_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_TUPLE_INDEX_OUT_OF_BOUNDS, "tuple_from_function")
    }
    @Test
    fun tuple_literal_test() {
        StellaTestsRunner.runBadTest(checkers.errors.StellaErrorType.ERROR_TUPLE_INDEX_OUT_OF_BOUNDS, "tuple_literal")
    }
    
}

