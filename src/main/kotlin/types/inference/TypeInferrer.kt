package types.inference

import checkers.errors.ErrorManager
import checkers.errors.StellaErrorType
import utils.paramName
import stellaParser
import stellaParserBaseVisitor
import types.*

internal class TypeInferrer(
    private val errorManager: ErrorManager?,
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
        val condition = ctx.condition
        val condType = condition.accept(this) ?: return null

        if (condType != BoolType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                BoolType,
                condType,
                condition
            )
            return null
        }

        val thenType = ctx.thenExpr.accept(this) ?: return null
        val elseType = ctx.elseExpr.accept(this) ?: return null

        if (elseType != thenType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                thenType,
                elseType,
                ctx.elseExpr
            )
            return null
        }

        return thenType
    }

    override fun visitConstInt(ctx: stellaParser.ConstIntContext): NatType {
        return NatType
    }

    override fun visitSucc(ctx: stellaParser.SuccContext): NatType? {
        val innerType = ctx.n.accept(this) ?: return null

        if (innerType != NatType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                NatType,
                innerType,
                ctx.n
            )
            return null
        }

        return NatType
    }

    override fun visitIsZero(ctx: stellaParser.IsZeroContext): BoolType? {
        val innerType = ctx.n.accept(this)

        if (innerType != NatType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                NatType,
                innerType ?: UnknownType,
                ctx
            )
            return null
        }

        return BoolType
    }

    override fun visitNatRec(ctx: stellaParser.NatRecContext): IType? {
        val itersCountType = ctx.n.accept(this)
        if (itersCountType != NatType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                NatType,
                itersCountType ?: UnknownType,
                ctx
            )
            return null
        }

        val initialValueType = ctx.initial.accept(this) ?: return null
        val stepFunctionType = ctx.step.accept(this)

        if (stepFunctionType !is FunctionalType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                FunctionalType.unknownFunctionalTypeName,
                stepFunctionType ?: UnknownType,
                ctx.step
            )
            return null
        }

        if (stepFunctionType.from != NatType) {
            val errorNode = (ctx.step as? stellaParser.AbstractionContext)?.paramDecl ?: ctx.step

            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_PARAMETER,
                NatType,
                stepFunctionType.from,
                errorNode
            )
            return null
        }

        if (stepFunctionType.to !is FunctionalType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                FunctionalType.unknownFunctionalTypeName,
                stepFunctionType.to,
                ctx.step
            )
            return null
        }

        if (stepFunctionType.to.from != stepFunctionType.to.to) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                stepFunctionType.to.to,
                stepFunctionType.to.from,
                ctx.step
            )
            return null
        }

        if (stepFunctionType.to.from != initialValueType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                initialValueType,
                stepFunctionType.to.from,
                ctx.step
            )
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
            errorManager?.registerError(
                StellaErrorType.ERROR_NOT_A_FUNCTION,
                funType,
                func
            )
            return null
        }

        val arg = ctx.args.first()
        val argType = arg.accept(this) ?: return null
        if (funType.from != argType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                funType.from,
                argType,
                arg
            )
        }

        return funType.to
    }

    override fun visitVar(ctx: stellaParser.VarContext): IType? {
        val name = ctx.name.text
        val type = context.resolveVariableType(name) ?: context.resolveFunctionType(name)

        if (type == null) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNDEFINED_VARIABLE,
                name,
                ctx
            )
            return null
        }

        return type
    }

    override fun visitConstUnit(ctx: stellaParser.ConstUnitContext?): UnitType {
        return UnitType
    }

    override fun visitTypeUnit(ctx: stellaParser.TypeUnitContext?): IType {
        return UnitType
    }

    override fun visitTuple(ctx: stellaParser.TupleContext): TupleType? {
        val content = ctx.expr()
        val contentTypes = content.map {
            it.accept(this)
        }

        if (contentTypes.any { it == null }) {
            return null
        }

        return TupleType(contentTypes.filterNotNull().toTypedArray())
    }

    override fun visitTypeTuple(ctx: stellaParser.TypeTupleContext): TupleType? {
        val types = ctx.types.map { visit(it) }
        if (types.any { it == null }) {
            return null
        }

        return TupleType(types.filterNotNull().toTypedArray())
    }

    override fun visitDotTuple(ctx: stellaParser.DotTupleContext): IType? {
        val expr = ctx.expr_
        val expressionType = expr.accept(this) ?: return null
        if (expressionType !is TupleType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_NOT_A_TUPLE,
                expressionType,
                ctx
            )
            return null
        }

        val indexContext = ctx.index
        val indexValue = indexContext.text.toInt()
        if (indexValue > expressionType.arity || indexValue == 0) {
            errorManager?.registerError(
                StellaErrorType.ERROR_TUPLE_INDEX_OUT_OF_BOUNDS,
                indexValue,
                expressionType.arity
            )
            return null
        }

        return expressionType.types[indexValue - 1]
    }

    @Suppress("DuplicatedCode")
    override fun visitRecord(ctx: stellaParser.RecordContext): RecordType? {
        val bindingsContext = ctx.bindings
        val labels = bindingsContext.map { bind -> bind.name.text }
        val types = bindingsContext.mapNotNull { bind -> bind.rhs.accept(this) }

        if (labels.size != types.size) {
            return null
        }

        return RecordType(labels.toTypedArray(), types.toTypedArray())
    }

    @Suppress("DuplicatedCode")
    override fun visitTypeRecord(ctx: stellaParser.TypeRecordContext): RecordType? {
        val fieldContexts = ctx.fieldTypes
        val labels = fieldContexts.map { field -> field.label.text }
        val types = fieldContexts.mapNotNull { field -> field.type_.accept(this) }

        if (labels.size != types.size) {
            return null
        }

        return RecordType(labels.toTypedArray(), types.toTypedArray())
    }

    override fun visitDotRecord(ctx: stellaParser.DotRecordContext): IType? {
        val expression = ctx.expr_
        val label = ctx.label.text

        if (expression is stellaParser.RecordContext) {
            val declaredLabels = expression.bindings.map { it.name.text }

            if (label !in declaredLabels) {
                errorManager?.registerError(
                    StellaErrorType.ERROR_UNEXPECTED_FIELD_ACCESS,
                    label,
                    ctx
                )
                return null
            }
        }

        val expressionType = expression.accept(this)

        if (expressionType !is RecordType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_NOT_A_RECORD,
                expressionType ?: UnknownType,
                ctx
            )
            return null
        }

        val labelIndex = expressionType.labels.indexOf(label)
        if (labelIndex == -1) {
            errorManager?.registerError(
                StellaErrorType.ERROR_MISSING_RECORD_FIELDS,
                label,
                ctx
            )
            return null
        }

        return expressionType.types[labelIndex]
    }

    override fun visitLet(ctx: stellaParser.LetContext): IType? {
        val patternBinding = ctx.patternBinding(0)
        val name = patternBinding.pat.text
        val expression = patternBinding.rhs

        val expressionType = visit(expression) ?: return null

        val letContext = TypeContext(context)
        letContext.saveVariableType(name, expressionType)

        val letTypeInferrer = TypeInferrer(errorManager, letContext)
        return letTypeInferrer.visit(ctx.body)
    }

    override fun visitTypeAsc(ctx: stellaParser.TypeAscContext): IType? {
        val expression = ctx.expr_
        val expressionType = expression.accept(this) ?: return null

        val targetType = ctx.type_.accept(this) ?: return null

        if (expressionType != targetType) {
            errorManager?.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                targetType,
                expressionType,
                expression
            )
            return null
        }

        return targetType
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
