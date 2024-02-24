package types.inference

import checkers.errors.ErrorManager
import checkers.errors.StellaErrorType
import paramName
import stellaParser
import stellaParserBaseVisitor
import types.*

internal class TypeInferrer(
    private val errorManager: ErrorManager,
    parentContext: TypeContext? = null
) : stellaParserBaseVisitor<IType?>() {
    private val context = TypeContext(parentContext)

    override fun visitConstTrue(ctx: stellaParser.ConstTrueContext): BoolType {
        return BoolType
    }

    override fun visitConstFalse(ctx: stellaParser.ConstFalseContext): BoolType {
        return BoolType
    }

    override fun visitTypeNat(ctx: stellaParser.TypeNatContext?): NatType {
        return NatType
    }

    override fun visitTypeBool(ctx: stellaParser.TypeBoolContext?): BoolType {
        return BoolType
    }

    override fun visitIf(ctx: stellaParser.IfContext): IType? {
        val condType = ctx.condition.accept(this) ?: return null

        if (condType != BoolType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, ctx)
            return null
        }

        val thenType = ctx.thenExpr.accept(this) ?: return null
        val elseType = ctx.elseExpr.accept(this) ?: return null

        if (thenType != elseType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, ctx)
            return null
        }

        return thenType
    }

    override fun visitConstInt(ctx: stellaParser.ConstIntContext): NatType {
        return NatType
    }

    override fun visitSucc(ctx: stellaParser.SuccContext): NatType? {
        val innerType = ctx.n.accept(this)

        if (innerType != NatType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, ctx)
            return null
        }

        return NatType
    }

    override fun visitIsZero(ctx: stellaParser.IsZeroContext): BoolType? {
        val innerType = ctx.n.accept(this)

        if (innerType != NatType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, ctx)
            return null
        }

        return BoolType
    }

    override fun visitNatRec(ctx: stellaParser.NatRecContext): IType? {
        val itersCountType = ctx.n.accept(this)
        if (itersCountType != NatType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, ctx)
            return null
        }

        val initialValueType = ctx.initial.accept(this)
        val stepFunctionType = ctx.step.accept(this)

        if (stepFunctionType !is FunctionalType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, ctx.step)
            return null
        }

        if (stepFunctionType.from != NatType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_PARAMETER, ctx.step)
            return null
        }

        if (stepFunctionType.to !is FunctionalType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, ctx.step)
            return null
        }

        if (stepFunctionType.to.from != stepFunctionType.to.to) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_PARAMETER, ctx.step)
            return null
        }

        if (stepFunctionType.to.from != initialValueType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, ctx.step)
            return null
        }

        return initialValueType
    }

    override fun visitTypeFun(ctx: stellaParser.TypeFunContext): IType? {
        val paramType = ctx.paramTypes.first().accept(this) ?: return null
        val returnType = ctx.returnType.accept(this) ?: return null

        return FunctionalType(paramType, returnType)
    }

    override fun visitAbstraction(ctx: stellaParser.AbstractionContext): FunctionalType? {
        val newInferrer = TypeInferrer(errorManager, context)

        val arg = ctx.paramDecl
        val argType = arg.accept(newInferrer) ?: return null

        val innerContext = TypeContext(context)
        innerContext.saveVariableType(arg.paramName, argType)
        val innerVisitor = TypeInferrer(errorManager, innerContext)

        val returnExpr = ctx.returnExpr
        val returnType = returnExpr.accept(innerVisitor) ?: return null

        return FunctionalType(argType, returnType)
    }

    override fun visitParamDecl(ctx: stellaParser.ParamDeclContext): IType? {
        val name = ctx.name.text
        val paramType = ctx.paramType.accept(this) ?: return null

        context.saveVariableType(name, paramType)

        return paramType
    }

    override fun visitApplication(ctx: stellaParser.ApplicationContext): IType? {
        val func = ctx.`fun`
        val funType = func.accept(this) ?: return null
        if (funType !is FunctionalType) {
            errorManager.registerError(StellaErrorType.ERROR_NOT_A_FUNCTION, func)
            return null
        }

        val arg = ctx.args.first()
        val argType = arg.accept(this) ?: return null
        if (funType.from != argType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, arg)
        }

        return funType.to
    }

    override fun visitVar(ctx: stellaParser.VarContext): IType? {
        val name = ctx.name.text
        val type = context.resolveVariableType(name) ?: context.resolveFunctionType(name)

        if (type == null) {
            errorManager.registerError(StellaErrorType.ERROR_UNDEFINED_VARIABLE, ctx)
            return null
        }

        return type
    }

    override fun visitConstUnit(ctx: stellaParser.ConstUnitContext?): UnitType {
        return UnitType
    }

    override fun visitTypeUnit(ctx: stellaParser.TypeUnitContext?): IType? {
        return UnitType
    }

    override fun visitTuple(ctx: stellaParser.TupleContext?): IType? {
        // todo
        return null
    }

    override fun visitTypeTuple(ctx: stellaParser.TypeTupleContext?): IType? {
        // todo
        return null
    }

    override fun visitRecord(ctx: stellaParser.RecordContext?): IType? {
        // todo
        return null
    }

    override fun visitTypeRecord(ctx: stellaParser.TypeRecordContext?): IType? {
        // todo
        return null
    }

    override fun aggregateResult(aggregate: IType?, nextResult: IType?): IType? {
        return when {
            aggregate == null -> nextResult
            aggregate == nextResult -> nextResult
            nextResult == null -> aggregate
            else -> error("can't aggregate types: $aggregate, $nextResult")
        }
    }
}
