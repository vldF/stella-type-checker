package checkers.types

import types.*

class ExhaustivenessChecker {
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
            UnknownType -> error("wrong type $type")
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

    fun findWrongPatter(patterns: List<stellaParser.PatternContext>, type: IType): stellaParser.PatternContext? {
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
            UnknownType -> error("wrong type $type")
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
