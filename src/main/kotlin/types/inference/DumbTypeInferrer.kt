package types.inference

import org.antlr.v4.runtime.ParserRuleContext
import stellaParser
import types.*

/**
 * Dump type inferrer infers type based on syntax not on semantics
 *
 * {v1, v2, v3} : UnknownTupleType
 * {l1 : v1, l2 : v2} : UnknownRecordType
 * fn(...) {} : UnknownFunctionalType
 * TODO: Add inference for lists and injections
 */
class DumbTypeInferrer {
    fun getType(ctx: ParserRuleContext): IType? {
        return when(ctx) {
            is stellaParser.ExprContext -> getExpressionType(ctx)
            is stellaParser.StellatypeContext -> getTypeType(ctx)
            is stellaParser.ParamDeclContext -> visitParameterDecl(ctx)
            else -> null
        }
    }

    private fun getExpressionType(ctx: stellaParser.ExprContext): IType? {
        return when (ctx) {
            is stellaParser.TupleContext -> TupleType(isKnownType = false)
            is stellaParser.RecordContext -> RecordType(isKnownType = false)
            is stellaParser.AbstractionContext -> visitAbstraction(ctx)
            is stellaParser.ConstUnitContext -> UnitType
            else -> return null
        }
    }

    private fun getTypeType(ctx: stellaParser.StellatypeContext): IType? {
        return when(ctx) {
            is stellaParser.TypeTupleContext -> TupleType(isKnownType = false)
            is stellaParser.TypeRecordContext -> RecordType(isKnownType = false)
            is stellaParser.TypeNatContext -> NatType
            is stellaParser.TypeBoolContext -> BoolType
            is stellaParser.TypeFunContext -> visitTypeFun(ctx)
            is stellaParser.TypeUnitContext -> UnitType
            else -> null
        }
    }

    private fun visitParameterDecl(ctx: stellaParser.ParamDeclContext): IType? {
        return getTypeType(ctx.paramType)
    }

    private fun visitAbstraction(ctx: stellaParser.AbstractionContext): FunctionalType {
        val arg = ctx.paramDecl
        val argType = getTypeType(arg.paramType) ?: UnknownType

        val returnExpr = ctx.returnExpr
        val returnType = getExpressionType(returnExpr) ?: UnknownType

        return FunctionalType(from = argType, to = returnType)
    }

    private fun visitTypeFun(ctx: stellaParser.TypeFunContext): FunctionalType {
        val arg = ctx.paramTypes.first()
        val argType = getTypeType(arg) ?: UnknownType

        val `return` = ctx.returnType
        val returnType = getTypeType(`return`) ?: UnknownType

        return FunctionalType(argType, returnType, isKnownType = false)
    }
}
