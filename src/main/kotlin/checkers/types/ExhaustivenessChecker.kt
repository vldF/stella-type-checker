package checkers.types

import checkers.errors.ErrorManager
import checkers.errors.StellaErrorType
import stellaParser
import types.*
import kotlin.reflect.KClass

class ExhaustivenessChecker {
    fun checkForPatternsTypeMissmatch(
        patterns: List<stellaParser.PatternContext>,
        expectedType: IType,
        errorManager: ErrorManager,
    ): Boolean {
        return patterns.all { checkForPatternTypeMissmatch(it, expectedType, errorManager) }
    }

    private fun checkForPatternTypeMissmatch(
        pattern: stellaParser.PatternContext,
        expectedType: IType,
        errorManager: ErrorManager,
    ): Boolean {
        val patternType = when (pattern) {
            is stellaParser.PatternVariantContext -> VariantType::class
            is stellaParser.PatternInlContext -> SumType::class
            is stellaParser.PatternInrContext -> SumType::class
            is stellaParser.PatternTupleContext -> TupleType::class
            is stellaParser.PatternRecordContext -> RecordType::class
            is stellaParser.PatternListContext -> ListType::class
            is stellaParser.PatternConsContext -> ListType::class
            is stellaParser.PatternFalseContext -> BoolType::class
            is stellaParser.PatternTrueContext -> BoolType::class
            is stellaParser.PatternUnitContext -> UnitType::class
            is stellaParser.PatternIntContext -> NatType::class
            is stellaParser.PatternSuccContext -> NatType::class
            is stellaParser.PatternVarContext -> return true
            is stellaParser.PatternAscContext -> {
                val ascType = SyntaxTypeProcessor.getType(pattern.type_)
                if (ascType != expectedType) {
                    errorManager.registerError(
                        StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE,
                        pattern,
                        expectedType
                    )

                    return false
                }

                return checkForPatternTypeMissmatch(pattern.pattern_, ascType, errorManager)
            }
            is stellaParser.ParenthesisedPatternContext -> {
                return checkForPatternTypeMissmatch(pattern.pattern_, expectedType, errorManager)
            }
            else -> error("unsupported pattern")
        }

        return validatePatternType(expectedType, patternType, pattern, errorManager)
    }

    private fun validatePatternType(
        expectedType: IType,
        actualPatternType: KClass<out IType>,
        context: stellaParser.PatternContext,
        errorManager: ErrorManager,
    ): Boolean {
        if (actualPatternType == expectedType::class) {
            return true
        }

        errorManager.registerError(
            StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE,
            expectedType,
            context
        )

        return false
    }

    fun arePatternsExhaustive(patterns: List<stellaParser.PatternContext>, type: IType): Boolean {
        return hasAny(patterns) || when (type) {
            BoolType -> areBoolPatternsExhaustive(patterns)
            NatType -> areNatPatternsExhaustive(patterns)
            is SumType -> areSumPatternsExhaustive(patterns)
            UnitType -> areUnitPatternsExhaustive(patterns)
            is TupleType -> TODO()
            is RecordType -> TODO()
            is VariantType -> areVariantPatternsExhaustive(patterns, type)
            is FunctionalType -> false // only var can match with a functional type
            is ListType -> false       // only var can match with a list type
            is ReferenceType -> true
        }
    }

    private fun areBoolPatternsExhaustive(patterns: List<stellaParser.PatternContext>): Boolean {
        return patterns.any { it is stellaParser.PatternTrueContext }
                && patterns.any { it is stellaParser.PatternFalseContext }
    }

    private fun areNatPatternsExhaustive(patterns: List<stellaParser.PatternContext>): Boolean {
        return patterns.any { it is stellaParser.PatternIntContext }
                && patterns.any { it is stellaParser.PatternSuccContext && it.pattern_ is stellaParser.PatternVarContext }
    }

    private fun areSumPatternsExhaustive(patterns: List<stellaParser.PatternContext>): Boolean {
        return patterns.any { it is stellaParser.PatternInlContext } && patterns.any { it is stellaParser.PatternInrContext }
    }

    private fun areUnitPatternsExhaustive(patterns: List<stellaParser.PatternContext>): Boolean {
        return patterns.any { it is stellaParser.PatternUnitContext }
    }

    private fun areVariantPatternsExhaustive(patterns: List<stellaParser.PatternContext>, type: VariantType): Boolean {
        val labelsInPattern = patterns.filterIsInstance<stellaParser.PatternVariantContext>().map { it.label.text }
        val labelsInType = type.labels.toSet()

        return labelsInPattern.toSet().containsAll(labelsInType)
    }

    private fun hasAny(patterns: List<stellaParser.PatternContext>): Boolean {
        return patterns.any { it is stellaParser.PatternVarContext }
    }

    fun findWrongPattern(patterns: List<stellaParser.PatternContext>, type: IType): stellaParser.PatternContext? {
        return when (type) {
            BoolType -> findWrongBoolPatter(patterns)
            NatType -> findWrongNatPatter(patterns)
            is SumType -> findWrongSumPatter(patterns)
            UnitType -> findWrongUnitPatter(patterns)
            is VariantType -> findWrongVariantPatter(patterns, type)
            is TupleType -> TODO()
            is RecordType -> TODO()
            is FunctionalType -> findNotVarPatter(patterns)
            is ListType -> findNotVarPatter(patterns)
            is ReferenceType -> null
        }
    }

    private fun findWrongBoolPatter(patterns: List<stellaParser.PatternContext>): stellaParser.PatternContext? {
        return patterns.firstOrNull {
            it !is stellaParser.PatternTrueContext &&
                    it !is stellaParser.PatternFalseContext &&
                    it !is stellaParser.PatternVarContext
        }
    }

    private fun findWrongNatPatter(patterns: List<stellaParser.PatternContext>): stellaParser.PatternContext? {
        return patterns.firstOrNull {
            it !is stellaParser.PatternIntContext &&
                    !(it is stellaParser.PatternSuccContext && it.pattern_ is stellaParser.PatternVarContext) &&
                    it !is stellaParser.PatternVarContext
        }
    }

    private fun findWrongSumPatter(patterns: List<stellaParser.PatternContext>): stellaParser.PatternContext? {
        return patterns.firstOrNull {
            it !is stellaParser.PatternInlContext &&
                    it !is stellaParser.PatternInrContext &&
                    it !is stellaParser.PatternVarContext
        }
    }

    private fun findWrongUnitPatter(patterns: List<stellaParser.PatternContext>): stellaParser.PatternContext? {
        return patterns.firstOrNull {
            it !is stellaParser.PatternUnitContext &&
                    it !is stellaParser.PatternVarContext
        }
    }

    private fun findNotVarPatter(patterns: List<stellaParser.PatternContext>): stellaParser.PatternContext? {
        return patterns.firstOrNull { it !is stellaParser.PatternVarContext }
    }

    private fun findWrongVariantPatter(patterns: List<stellaParser.PatternContext>, type: VariantType): stellaParser.PatternContext? {
        val labelsInType = type.labels.toSet()
        return patterns.firstOrNull {
            it !is stellaParser.PatternVarContext &&
                    !(it is stellaParser.PatternVariantContext && it.label.text in labelsInType)
        }
    }
}
