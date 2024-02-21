import org.junit.jupiter.api.Test

// DO NOT MODIFY THIS FILE MANUALLY
// Edit TestsGenerator.kt instead

@Suppress("ClassName")
class OK_TESTS {
    @Test
    fun simple_tuple_test() {
        StellaTestsRunner.runOkTest("simple_tuple")
    }
    @Test
    fun simple_records_test() {
        StellaTestsRunner.runOkTest("simple_records")
    }
    @Test
    fun twice_bool_not_test() {
        StellaTestsRunner.runOkTest("twice_bool_not")
    }
    @Test
    fun let_square_test() {
        StellaTestsRunner.runOkTest("let_square")
    }
    @Test
    fun increment_twice_test() {
        StellaTestsRunner.runOkTest("increment_twice")
    }
    @Test
    fun simple_pair_test() {
        StellaTestsRunner.runOkTest("simple_pair")
    }
    @Test
    fun simple_ascription_test() {
        StellaTestsRunner.runOkTest("simple_ascription")
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
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_MISSING_MAIN, "no_main")
    }
    
}
@Suppress("ClassName")
class ERROR_MISSING_RECORD_FIELDS_TESTS {
    @Test
    fun simple_missing_fields_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_MISSING_RECORD_FIELDS, "simple_missing_fields")
    }
    
}
@Suppress("ClassName")
class ERROR_UNEXPECTED_TUPLE_TESTS {
    @Test
    fun simple_unexpected_tuple_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TUPLE, "simple_unexpected_tuple")
    }
    
}
@Suppress("ClassName")
class ERROR_UNEXPECTED_RECORD_TESTS {
    @Test
    fun simple_unexpected_record_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_RECORD, "simple_unexpected_record")
    }
    
}
@Suppress("ClassName")
class ERROR_NOT_A_RECORD_TESTS {
    @Test
    fun simple_not_a_record_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_NOT_A_RECORD, "simple_not_a_record")
    }
    
}
@Suppress("ClassName")
class ERROR_UNEXPECTED_TUPLE_LENGTH_TESTS {
    @Test
    fun simple_unexpected_length_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TUPLE_LENGTH, "simple_unexpected_length")
    }
    
}
@Suppress("ClassName")
class ERROR_UNEXPECTED_RECORD_FIELDS_TESTS {
    @Test
    fun simple_unexpected_fields_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_RECORD_FIELDS, "simple_unexpected_fields")
    }
    
}
@Suppress("ClassName")
class ERROR_UNDEFINED_VARIABLE_TESTS {
    @Test
    fun simple_undefined_var_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNDEFINED_VARIABLE, "simple_undefined_var")
    }
    
}
@Suppress("ClassName")
class ERROR_NOT_A_TUPLE_TESTS {
    @Test
    fun simple_not_a_tuple_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_NOT_A_TUPLE, "simple_not_a_tuple")
    }
    
}
@Suppress("ClassName")
class ERROR_UNEXPECTED_TYPE_FOR_PARAMETER_TESTS {
    @Test
    fun simple_unexpected_parameter_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_PARAMETER, "simple_unexpected_parameter")
    }
    
}
@Suppress("ClassName")
class ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION_TESTS {
    @Test
    fun simple_let_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "simple_let")
    }
    @Test
    fun false_return_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "false_return")
    }
    @Test
    fun unexpected_unit_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "unexpected_unit")
    }
    @Test
    fun unexpected_application_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "unexpected_application")
    }
    @Test
    fun is_zero_bool_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "is_zero_bool")
    }
    @Test
    fun succ_true_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "succ_true")
    }
    @Test
    fun different_branches_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "different_branches")
    }
    @Test
    fun no_nat_rec_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "no_nat_rec")
    }
    @Test
    fun unexpected_s_rec_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "unexpected_s_rec")
    }
    @Test
    fun simple_ascription_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "simple_ascription")
    }
    @Test
    fun if_nat_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, "if_nat")
    }
    
}
@Suppress("ClassName")
class ERROR_UNEXPECTED_FIELD_ACCESS_TESTS {
    @Test
    fun simple_field_access_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_FIELD_ACCESS, "simple_field_access")
    }
    
}
@Suppress("ClassName")
class ERROR_NOT_A_FUNCTION_TESTS {
    @Test
    fun simple_no_function_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_NOT_A_FUNCTION, "simple_no_function")
    }
    
}
@Suppress("ClassName")
class ERROR_UNEXPECTED_LAMBDA_TESTS {
    @Test
    fun simple_unexpected_lambda_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_UNEXPECTED_LAMBDA, "simple_unexpected_lambda")
    }
    
}
@Suppress("ClassName")
class ERROR_TUPLE_INDEX_OUT_OF_BOUNDS_TESTS {
    @Test
    fun simple_index_out_test() {
        StellaTestsRunner.runBadTest(StellaTypeError.ERROR_TUPLE_INDEX_OUT_OF_BOUNDS, "simple_index_out")
    }
    
}
