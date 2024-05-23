package checkers.types

import stellaParser
import types.*

class ExhaustivenessChecker {
    fun check(
        patterns: List<stellaParser.PatternContext>,
        expectedType: IType
    ) : Boolean {
        val preparedPatterns = patterns.map { pattern ->
            when(pattern) {
                is stellaParser.ParenthesisedPatternContext -> pattern.pattern_
                is stellaParser.PatternAscContext -> pattern.pattern_
                else -> pattern
            }
        }

        if (preparedPatterns.isAnyVar()) {
            return true
        }

        return when (expectedType) {
            BoolType -> processBoolPattern(patterns)
            NatType -> processNatPattern(patterns)
            UnitType -> processUnitPattern(patterns)
            is SumType -> processSumPattern(patterns)
            is TypeVar -> processVariantsWithTypeVar(patterns)
            is VariantType -> processVariantPattern(patterns, expectedType)
            else -> false
        }
    }

    private fun List<stellaParser.PatternContext>.isAnyVar() = this.any { it is stellaParser.PatternVarContext }

    private fun processBoolPattern(patterns: List<stellaParser.PatternContext>): Boolean {
        return patterns.any { it is stellaParser.PatternTrueContext }
                && patterns.any { it is stellaParser.PatternFalseContext }
    }

    private fun processNatPattern(patterns: List<stellaParser.PatternContext>): Boolean {
        return patterns.any { it is stellaParser.PatternIntContext }
                && patterns.any { it is stellaParser.PatternSuccContext && it.pattern_ is stellaParser.PatternVarContext }
    }

    private fun processUnitPattern(patterns: List<stellaParser.PatternContext>): Boolean {
        return patterns.any { it is stellaParser.PatternUnitContext }
    }

    private fun processSumPattern(patterns: List<stellaParser.PatternContext>): Boolean {
        return patterns.any { it is stellaParser.PatternInlContext } && patterns.any { it is stellaParser.PatternInrContext }
    }

    private fun processVariantPattern(patterns: List<stellaParser.PatternContext>, type: VariantType): Boolean {
        val labelsInPattern = patterns.filterIsInstance<stellaParser.PatternVariantContext>().map { it.label.text }
        val labelsInType = type.labels.toSet()

        return labelsInPattern.toSet().containsAll(labelsInType)
    }

    private fun processVariantsWithTypeVar(patterns: List<stellaParser.PatternContext>): Boolean {
        var result = true

        if (patterns.any { it is stellaParser.PatternInlContext || it is stellaParser.PatternInrContext }) {
            result = processSumPattern(patterns)
        }

        if (patterns.any { it is stellaParser.PatternTrueContext || it is stellaParser.PatternFalseContext }) {
            result = processBoolPattern(patterns)
        }

        return result
    }
}
