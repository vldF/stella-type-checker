package checkers.types

import StellaExtension
import checkers.errors.ErrorManager
import checkers.errors.StellaErrorType
import checkers.types.inferrer.UnificationFailed
import checkers.types.inferrer.UnificationFailedInfiniteType
import checkers.types.inferrer.UnificationOk
import checkers.types.inferrer.UnifySolver
import org.antlr.v4.runtime.ParserRuleContext
import utils.paramName
import stellaParser
import types.*
import utils.functionName

internal class TypeChecker(
    private val errorManager: ErrorManager,
    parentContext: TypeContext? = null,
    private val extensionManager: ExtensionManager = ExtensionManager(),
    private val unifySolver: UnifySolver = UnifySolver(),
) {
    private val context = TypeContext(parentContext)

    fun checkProgram(ctx: stellaParser.ProgramContext) {
        addExtensions(ctx.extensions)

        val topLevelInfoCollector = TopLevelInfoCollector(context)
        topLevelInfoCollector.visitProgram(ctx)

        ctx.decls.forEach(::visitDecl)

        when (val unificationResult = unifySolver.solve()) {
            is UnificationFailed -> {
                errorManager.registerError(
                    StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                    unificationResult.expectedType,
                    unificationResult.actualType,
                    unificationResult.expression
                )
            }
            is UnificationFailedInfiniteType -> {
                errorManager.registerError(
                    StellaErrorType.ERROR_OCCURS_CHECK_INFINITE_TYPE,
                    unificationResult.expression
                )
            }
            UnificationOk -> {}
        }
    }

    private fun addExtensions(ctxs: List<stellaParser.ExtensionContext>) {
        val extensions = ctxs
            .filterIsInstance<stellaParser.AnExtensionContext>()
            .flatMap { it.extensionNames }
            .map { it.text.removePrefix("#") }
            .mapNotNull { StellaExtension.fromString(it) }

        extensionManager.enableExtensions(extensions)
    }
    
    private fun visitDecl(ctx: stellaParser.DeclContext) {
        when (ctx) {
            is stellaParser.DeclFunContext -> visitDeclFun(ctx)
            is stellaParser.DeclExceptionTypeContext -> visitDeclExceptionType(ctx)
        }
    }

    private fun visitDeclExceptionType(ctx: stellaParser.DeclExceptionTypeContext) {
        context.exceptionType = SyntaxTypeProcessor.getType(ctx.exceptionType)
    }

    private fun visitExpression(ctx: stellaParser.ExprContext, expectedType: IType?): IType? {
        val type = when (ctx) {
            is stellaParser.ConstTrueContext -> BoolType
            is stellaParser.ConstFalseContext -> BoolType
            is stellaParser.ConstIntContext -> NatType
            is stellaParser.ConstUnitContext -> UnitType
            is stellaParser.IsZeroContext -> visitIsZero(ctx)
            is stellaParser.SuccContext -> visitSucc(ctx)
            is stellaParser.PredContext -> visitPred(ctx)
            is stellaParser.VarContext -> visitVar(ctx, expectedType)
            is stellaParser.DotRecordContext -> visitDotRecord(ctx, expectedType)
            is stellaParser.AbstractionContext -> visitAbstraction(ctx, expectedType)
            is stellaParser.ApplicationContext -> visitApplication(ctx, expectedType)
            is stellaParser.ParenthesisedExprContext -> visitExpression(ctx.expr_, expectedType)
            is stellaParser.RecordContext -> visitRecord(ctx, expectedType)
            is stellaParser.LetContext -> visitLet(ctx, expectedType)
            is stellaParser.TypeAscContext -> visitTypeAsc(ctx, expectedType)
            is stellaParser.NatRecContext -> visitNatRec(ctx, expectedType)
            is stellaParser.DotTupleContext -> visitDotTuple(ctx, expectedType)
            is stellaParser.IfContext -> visitIf(ctx, expectedType)
            is stellaParser.TupleContext -> visitTuple(ctx, expectedType)
            is stellaParser.TerminatingSemicolonContext -> visitExpression(ctx.expr_, expectedType)
            is stellaParser.FixContext -> visitFix(ctx, expectedType)
            is stellaParser.MatchContext -> visitMatch(ctx, expectedType)
            is stellaParser.InlContext -> visitInl(ctx, expectedType)
            is stellaParser.InrContext -> visitInr(ctx, expectedType)
            is stellaParser.VariantContext -> visitVariant(ctx, expectedType)
            is stellaParser.ListContext -> visitListContext(ctx, expectedType)
            is stellaParser.ConsListContext -> visitConsList(ctx, expectedType)
            is stellaParser.HeadContext -> visitHead(ctx, expectedType)
            is stellaParser.TailContext -> visitTail(ctx, expectedType)
            is stellaParser.IsEmptyContext -> visitIsEmpty(ctx, expectedType)
            is stellaParser.SequenceContext -> visitSequence(ctx, expectedType)
            is stellaParser.RefContext -> visitRef(ctx, expectedType)
            is stellaParser.ConstMemoryContext -> visitConstMemory(ctx, expectedType)
            is stellaParser.DerefContext -> visitDeref(ctx, expectedType)
            is stellaParser.AssignContext -> visitAssign(ctx, expectedType)
            is stellaParser.PanicContext -> visitPanic(ctx, expectedType)
            is stellaParser.ThrowContext -> visitThrow(ctx, expectedType)
            is stellaParser.TryWithContext -> visitTryWith(ctx, expectedType)
            is stellaParser.TryCatchContext -> visitTryCatch(ctx, expectedType)
            is stellaParser.TypeCastContext -> visitTypeCast(ctx, expectedType)
            else -> {
                println("unsupported syntax for ${ctx::class.java}")
                null
            }
        } ?: return null

        return validateTypes(type, expectedType, ctx)
    }

    private fun visitDeclFun(ctx: stellaParser.DeclFunContext) {
        val functionName = ctx.functionName
        val functionType = context.resolveFunctionType(functionName) ?: return
        val expectedFunctionRetType = functionType.to

        val functionContext = TypeContext(context)
        functionContext.saveVariableType(ctx.paramDecl.paramName, functionType.from)

        val topLevelInfoCollector = TopLevelInfoCollector(functionContext)
        ctx.children.forEach { c -> topLevelInfoCollector.visit(c) }

        val innerTypeCheckerVisitor = TypeChecker(errorManager, functionContext, extensionManager, unifySolver)
        ctx.localDecls.forEach { c -> innerTypeCheckerVisitor.visitDecl(c) }

        val returnExpr = ctx.returnExpr
        val typeInferrer = TypeChecker(errorManager, functionContext, extensionManager, unifySolver)

        typeInferrer.visitExpression(returnExpr, expectedFunctionRetType)
    }

    private fun visitIf(ctx: stellaParser.IfContext, expectedType: IType?): IType? {
        val condition = ctx.condition
        visitExpression(condition, BoolType) ?: return null

        val thenType = visitExpression(ctx.thenExpr, expectedType) ?: return null
        val elseType = visitExpression(ctx.elseExpr, thenType) ?: return null

        if (elseType != thenType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                thenType,
                elseType,
                ctx.elseExpr
            )
            return null
        }

        return thenType
    }

    private fun visitSucc(ctx: stellaParser.SuccContext): NatType? {
        visitExpression(ctx.n, NatType) ?: return null
        return NatType
    }

    private fun visitPred(ctx: stellaParser.PredContext): NatType? {
        visitExpression(ctx.n, NatType) ?: return null
        return NatType
    }

    private fun visitIsZero(ctx: stellaParser.IsZeroContext): BoolType? {
        visitExpression(ctx.n, NatType) ?: return null
        return BoolType
    }

    private fun visitNatRec(ctx: stellaParser.NatRecContext, expectedType: IType?): IType? {
       visitExpression(ctx.n, NatType)

        val initialValueType = visitExpression(ctx.initial, expectedType) ?: return null
        val expectedStepFunctionType = FunctionalType(NatType, FunctionalType(initialValueType, initialValueType))
        visitExpression(ctx.step, expectedStepFunctionType) ?: return null

        return initialValueType
    }

    private fun visitAbstraction(ctx: stellaParser.AbstractionContext, expectedType: IType?): IType? {
        if (expectedType != null && expectedType !is FunctionalType && expectedType !is TypeVar) {
            val actualType = visitExpression(ctx, null) ?: return null
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_LAMBDA,
                expectedType,
                actualType,
                ctx
            )

            return null
        }

        val arg = ctx.paramDecl
        val argType = SyntaxTypeProcessor.getType(arg.paramType)

        val innerContext = TypeContext(context)
        innerContext.saveVariableType(arg.paramName, argType)
        val innerInferrer = TypeChecker(errorManager, innerContext, extensionManager, unifySolver)

        val returnExpr = ctx.returnExpr

        val returnType = if (expectedType is TypeVar) {
            val retTypeVar = TypeVar.new()

            val expectedFuncType = FunctionalType(argType, retTypeVar)
            unifySolver.addConstraint(expectedType, expectedFuncType, ctx)

            innerInferrer.visitExpression(returnExpr, retTypeVar)
        } else {
            innerInferrer.visitExpression(returnExpr, (expectedType as FunctionalType?)?.to) ?: return null
        } ?: return null

        val result = FunctionalType(argType, returnType)
        return validateTypes(result, expectedType, ctx)
    }

    private fun visitApplication(ctx: stellaParser.ApplicationContext, expectedType: IType?): IType? {
        val func = ctx.`fun`

        val funType = visitExpression(func, null) ?: return null

        if (extensionManager.typeReconstruction) {
            val arg = ctx.args.first()
            val argType = visitExpression(arg, null) ?: return null

            val expectedTypeOrVarType = expectedType ?: TypeVar.new()

            unifySolver.addConstraint(funType, FunctionalType(argType, expectedTypeOrVarType), ctx)

            return expectedTypeOrVarType
        }

        if (funType !is FunctionalType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_FUNCTION,
                funType,
                func
            )
            return null
        }

        val resultType = funType.to

        val arg = ctx.args.first()
        visitExpression(arg, funType.from) ?: return null

        return validateTypes(resultType, expectedType, ctx)
    }

    private fun visitVar(ctx: stellaParser.VarContext, expectedType: IType?): IType? {
        val name = ctx.name.text
        val type = context.resolveVariableType(name) ?: context.resolveFunctionType(name)

        if (type == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNDEFINED_VARIABLE,
                name,
                ctx
            )
            return null
        }

        return validateTypes(type, expectedType, ctx)
    }

    private fun visitTuple(ctx: stellaParser.TupleContext, expectedType: IType?): TupleType? {
        if (expectedType !is TupleType?) {
            val tupleType = visitTuple(ctx, null) ?: return null

            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TUPLE,
                expectedType!!,
                tupleType
            )
        }

        val content = ctx.expr()
        val contentTypes = content.map { visitExpression(it, null) }

        if (contentTypes.any { it == null }) {
            return null
        }

        return TupleType(contentTypes.filterNotNull())
    }

    private fun visitDotTuple(ctx: stellaParser.DotTupleContext, expectedType: IType?): IType? {
        val expr = ctx.expr_
        val expressionType = visitExpression(expr, null) ?: return null
        val indexContext = ctx.index
        val indexValue = indexContext.text.toInt()

        if (extensionManager.typeReconstruction) {
            val pairType1 = TypeVar.new()
            val pairType2 = TypeVar.new()

            val pairType = TupleType(listOf(pairType1, pairType2))

            unifySolver.addConstraint(expressionType, pairType, ctx)

            return validateTypes(pairType.types[indexValue - 1], expectedType, ctx)
        }

        if (expressionType !is TupleType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_TUPLE,
                expressionType,
                ctx
            )
            return null
        }

        if (indexValue > expressionType.arity || indexValue == 0) {
            errorManager.registerError(
                StellaErrorType.ERROR_TUPLE_INDEX_OUT_OF_BOUNDS,
                indexValue,
                expressionType.arity
            )
            return null
        }

        val type = expressionType.types[indexValue - 1]
        return validateTypes(type, expectedType, ctx)
    }

    @Suppress("DuplicatedCode")
    private fun visitRecord(ctx: stellaParser.RecordContext, expectedType: IType?): IType? {
        if (expectedType != null && expectedType !is RecordType) {
            val actualType = visitRecord(ctx, null) ?: return null

            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_RECORD,
                expectedType,
                actualType
            )

            return null
        }

        expectedType as RecordType?

        val bindingsContext = ctx.bindings
        val labels = bindingsContext.map { bind -> bind.name.text }
        val types = bindingsContext.mapNotNull { bind -> visitExpression(bind.rhs, null) }

        if (labels.size != types.size) {
            return null
        }

        if (expectedType == null) {
            return RecordType(labels, types)
        }

        if (expectedType.labels.zip(expectedType.types).toSet() == labels.zip(types).toSet()) {
            return expectedType
        }

        val actualType = RecordType(labels, types)

        return validateTypes(actualType, expectedType, ctx)
    }

    private fun visitDotRecord(ctx: stellaParser.DotRecordContext, expectedType: IType?): IType? {
        val expression = ctx.expr_
        val label = ctx.label.text

        val expressionType = visitExpression(expression, null) ?: return null
        if (expressionType !is RecordType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_RECORD,
                expressionType,
                ctx
            )
            return null
        }


        val declaredLabels = expressionType.labels
        if (label !in declaredLabels) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_FIELD_ACCESS,
                label,
                ctx
            )
            return null
        }

        val labelIndex = declaredLabels.indexOf(label)
        val type = expressionType.types[labelIndex]
        return validateTypes(type, expectedType, ctx)
    }

    private fun visitLet(ctx: stellaParser.LetContext, expectedType: IType?): IType? {
        val patternBinding = ctx.patternBinding(0)
        val name = patternBinding.pat.text
        val expression = patternBinding.rhs

        val expressionType = visitExpression(expression, null) ?: return null

        val letContext = TypeContext(context)
        letContext.saveVariableType(name, expressionType)

        val letTypeInferrer = TypeChecker(errorManager, letContext, extensionManager, unifySolver)
        return letTypeInferrer.visitExpression(ctx.body, expectedType)
    }

    private fun visitTypeAsc(ctx: stellaParser.TypeAscContext, expectedType: IType?): IType? {
        val expression = ctx.expr_
        val targetType = SyntaxTypeProcessor.getType(ctx.type_)

        val expressionType = visitExpression(expression, expectedType ?: targetType) ?: return null

        if (expressionType != targetType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                targetType,
                expressionType,
                expression
            )
            return null
        }

        return validateTypes(expressionType, expectedType, ctx)
    }

    private fun validateTypes(actualType: IType, expectedType: IType?, expression: ParserRuleContext): IType? {
        if (expectedType == null) {
            return actualType
        }

        if (extensionManager.typeReconstruction) {
            unifySolver.addConstraint(actualType, expectedType, expression)
            return expectedType
        }

        if (actualType == expectedType) {
            return expectedType
        }

        when {
            actualType is RecordType && expectedType is RecordType -> {
                val result = validateRecords(expectedType, actualType, expression)

                if (result) {
                    return expectedType
                }

                return null
            }

            actualType is TupleType && expectedType is TupleType -> {
                if (actualType.arity != expectedType.arity) {
                    errorManager.registerError(
                        StellaErrorType.ERROR_UNEXPECTED_TUPLE_LENGTH,
                        expectedType.arity,
                        actualType.arity,
                        expression
                    )

                    return null
                }
            }

            actualType is VariantType && expectedType is VariantType -> {
                val expectedLabels = expectedType.labels
                val actualLabels = actualType.labels

                if (!expectedLabels.containsAll(actualLabels)) {
                    errorManager.registerError(
                        StellaErrorType.ERROR_UNEXPECTED_VARIANT_LABEL,
                        (expectedLabels - actualLabels.toSet()).first(),
                        expression,
                        actualType
                    )

                    return null
                }
            }
        }

        errorManager.registerError(
            StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
            expectedType,
            actualType,
            expression
        )

        return null
    }

    private fun validatePattern(expectedType: IType, actualType: IType, context: stellaParser.PatternContext): IType? {
        if (expectedType == actualType) {
            return expectedType
        }

        errorManager.registerError(
            StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE,
            context,
            expectedType
        )

        return null
    }

    private fun validateRecords(
        expectedRecord: RecordType,
        actualRecord: RecordType,
        ctx: ParserRuleContext
    ): Boolean {
        if (expectedRecord != actualRecord) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                expectedRecord,
                actualRecord,
                ctx
            )

            return false
        }

        val missingFields = expectedRecord.labels.zip(expectedRecord.types) -
                actualRecord.labels.zip(actualRecord.types).toSet()

        val extraFields = actualRecord.labels.zip(actualRecord.types).toSet() -
                expectedRecord.labels.zip(expectedRecord.types).toSet()

        if (missingFields.isNotEmpty()) {
            errorManager.registerError(
                StellaErrorType.ERROR_MISSING_RECORD_FIELDS,
                missingFields.first().first,
                expectedRecord
            )

            return false
        }

        if (extraFields.isNotEmpty()) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_RECORD_FIELDS,
                extraFields.first().first,
                expectedRecord
            )

            return false
        }

        for ((label, type) in actualRecord.labels.zip(actualRecord.types)) {
            val expectedTypeForLabelIdx = expectedRecord.labels.indexOf(label)
            val expectedTypeForLabel = expectedRecord.types.getOrNull(expectedTypeForLabelIdx) ?: continue

            if (type is RecordType) {
                if (expectedTypeForLabel !is RecordType) {
                    errorManager.registerError(
                        StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                        expectedTypeForLabel,
                        type,
                        ctx
                    )
                    return false
                }

                if (!validateRecords(expectedTypeForLabel, type, ctx)) {
                    return false
                }
            } else {
                if (type != expectedTypeForLabel) {
                    if (expectedTypeForLabel !is RecordType) {
                        errorManager.registerError(
                            StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                            expectedTypeForLabel,
                            type,
                            ctx
                        )
                        return false
                    }
                }
            }
        }

        return true
    }

    private fun visitFix(ctx: stellaParser.FixContext, expectedType: IType?): IType? {
        val expression = ctx.expr_

        if (extensionManager.typeReconstruction) {
            val expectedTypeOrVar = expectedType ?: TypeVar.new()
            visitExpression(expression, FunctionalType(expectedTypeOrVar, expectedTypeOrVar))

            return expectedTypeOrVar
        }

        val expressionType = if (expectedType != null) {
            visitExpression(expression, FunctionalType(expectedType, expectedType))
        } else {
            visitExpression(expression, null)
        } ?: return null

        if (expressionType !is FunctionalType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_FUNCTION,
                expressionType,
                expression
            )

            return null
        }

        if (expressionType.from != expressionType.to) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                FunctionalType(expressionType.from, expressionType.from),
                expressionType,
                expression
            )

            return null
        }

        return expressionType.to
    }

    private fun visitMatch(ctx: stellaParser.MatchContext, expectedType: IType?): IType? {
        val matchExprType = visitExpression(ctx.expr_, expectedType = null) ?: return null

        val cases = ctx.cases

        if (cases.isEmpty()) {
            errorManager.registerError(StellaErrorType.ERROR_ILLEGAL_EMPTY_MATCHING, ctx)
            return null
        }

        val branchExpressions = cases.map { case ->
            val newContext = TypeContext(context)
            val newChecker = TypeChecker(errorManager, newContext, extensionManager, unifySolver)


            newChecker.visitPatternContext(case.pattern_, matchExprType) ?: return null
            newChecker.visitExpression(case.expr_, expectedType) ?: return null
        }

        if (branchExpressions.size != cases.size) {
            return null
        }

        val patterns = cases.map { it.pattern_ }
        if (!ExhaustivenessChecker().check(patterns, matchExprType)) {
            errorManager.registerError(
                StellaErrorType.ERROR_NONEXHAUSTIVE_MATCH_PATTERNS,
                matchExprType
            )

            return null
        }

        val matchType = branchExpressions.first()
        val firstWrongType = branchExpressions.firstOrNull { validateTypes(matchType, it, ctx) == null }
        if (firstWrongType != null) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                matchType,
                firstWrongType,
                ctx
            )
            return null
        }

        return matchType
    }

    private fun visitPatternContext(ctx: stellaParser.PatternContext, expectedType: IType): IType? {
        val resultType = when (ctx) {
            is stellaParser.PatternConsContext -> visitPatternConsContext(ctx, expectedType)
            is stellaParser.PatternTrueContext -> visitTruePatternContext(ctx, expectedType)
            is stellaParser.PatternFalseContext -> visitFalsePatternContext(ctx, expectedType)
            is stellaParser.PatternUnitContext -> visitUnitPatternContext(ctx, expectedType)
            is stellaParser.PatternVarContext -> visitVarPatternContext(ctx, expectedType)
            is stellaParser.PatternAscContext -> visitAscPatternContext(ctx, expectedType)
            is stellaParser.ParenthesisedPatternContext -> visitPatternContext(ctx.pattern_, expectedType)
            is stellaParser.PatternInlContext -> visitInlPatternContext(ctx, expectedType)
            is stellaParser.PatternInrContext -> visitInrPatternContext(ctx, expectedType)
            is stellaParser.PatternVariantContext -> visitVariantPatternContext(ctx, expectedType)
            else -> null
        } ?: return null

        return validatePattern(resultType, expectedType, ctx)
    }

    private fun visitPatternConsContext(ctx: stellaParser.PatternConsContext, expectedType: IType): IType? {
        if (expectedType !is ListType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE,
                ctx,
                expectedType
            )

            return null
        }

        visitPatternContext(ctx.head, expectedType.type)
        visitPatternContext(ctx.tail, expectedType)

        return expectedType
    }

    private fun visitTruePatternContext(ctx: stellaParser.PatternTrueContext, expectedType: IType): IType? {
        return validatePattern(BoolType, expectedType, ctx)
    }

    private fun visitFalsePatternContext(ctx: stellaParser.PatternFalseContext, expectedType: IType): IType? {
        return validatePattern(BoolType, expectedType, ctx)
    }

    private fun visitUnitPatternContext(ctx: stellaParser.PatternUnitContext, expectedType: IType): IType? {
        return validatePattern(UnitType, expectedType, ctx)
    }

    private fun visitVarPatternContext(ctx: stellaParser.PatternVarContext, expectedType: IType): IType {
        context.saveVariableType(ctx.name.text, expectedType)

        return expectedType
    }

    private fun visitAscPatternContext(ctx: stellaParser.PatternAscContext, expectedType: IType): IType? {
        val type = SyntaxTypeProcessor.getType(ctx.type_)
        val trueType = validatePattern(type, expectedType, ctx) // todo?

        if (trueType == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_AMBIGUOUS_PATTERN_TYPE,
                type,
                ctx
            )

            return null
        }

        return visitPatternContext(ctx.pattern_, trueType)
    }

    private fun visitInlPatternContext(ctx: stellaParser.PatternInlContext, expectedType: IType): IType? {
        if (expectedType !is SumType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE,
                ctx,
                expectedType
            )

            return null
        }

        visitPatternContext(ctx.pattern_, expectedType.left) ?: return null

        return expectedType
    }

    private fun visitInrPatternContext(ctx: stellaParser.PatternInrContext, expectedType: IType): IType? {
        if (expectedType !is SumType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE,
                ctx,
                expectedType
            )

            return null
        }

       visitPatternContext(ctx.pattern_, expectedType.right) ?: return null
        return expectedType
    }

    private fun visitVariantPatternContext(ctx: stellaParser.PatternVariantContext, expectedType: IType): IType? {
        if (expectedType !is VariantType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE,
                ctx,
                expectedType
            )

            return null
        }

        val tagName = ctx.label.text
        val varTypeIdx = expectedType.labels.indexOf(tagName)
        if (varTypeIdx == -1) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE,
                ctx,
                expectedType
            )

            return null
        }

        val varType = expectedType.types[varTypeIdx]

        visitPatternContext(ctx.pattern_, varType) ?: return null

        return expectedType
    }

    @Suppress("DuplicatedCode")
    private fun visitInl(ctx: stellaParser.InlContext, expectedType: IType?): IType? {
        return if (expectedType == null && !extensionManager.typeReconstruction) {
            errorManager.registerError(
                StellaErrorType.ERROR_AMBIGUOUS_SUM_TYPE,
                ctx
            )

            null
        } else if (extensionManager.typeReconstruction) {
            val leftType = visitExpression(ctx.expr_, null) ?: return null
            SumType(leftType, TypeVar.new())
        } else if (expectedType !is SumType && expectedType !is BotType && expectedType != null) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_INJECTION,
                expectedType
            )

            null
        } else if (expectedType != null && expectedType is SumType) {
            visitExpression(ctx.expr_, expectedType.left) ?: return null

            expectedType
        } else {
            val leftType = visitExpression(ctx.expr_, null) ?: return null
            SumType(leftType, BotType)
        }
    }

    @Suppress("DuplicatedCode")
    private fun visitInr(ctx: stellaParser.InrContext, expectedType: IType?): IType? {
        return if (expectedType == null && !extensionManager.typeReconstruction) {
            errorManager.registerError(
                StellaErrorType.ERROR_AMBIGUOUS_SUM_TYPE,
                ctx
            )

            null
        } else if (extensionManager.typeReconstruction) {
            val rightType = visitExpression(ctx.expr_, null) ?: return null
            SumType(TypeVar.new(), rightType)
        } else if (expectedType !is SumType && expectedType !is BotType && expectedType != null) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_INJECTION,
                expectedType
            )

            null
        } else if (expectedType != null && expectedType is SumType) {
            visitExpression(ctx.expr_, expectedType.right) ?: return null

            expectedType
        } else {
            val rightType = visitExpression(ctx.expr_, null) ?: return null
            SumType(BotType, rightType)
        }
    }

    private fun visitVariant(ctx: stellaParser.VariantContext, expectedType: IType?): VariantType? {
        if (expectedType != null && expectedType !is VariantType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_VARIANT,
                expectedType
            )

            return null
        }

        expectedType as VariantType?

        if (expectedType == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_AMBIGUOUS_VARIANT_TYPE,
                ctx
            )

            return null
        }

        val label = ctx.label.text
        val labelIndex = expectedType.labels.indexOf(label)
        if (labelIndex == -1) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_VARIANT_LABEL,
                label,
                ctx,
                expectedType
            )

            return null
        }

        val expectedExpressionType = expectedType.types[labelIndex]

        val expression = ctx.rhs
        visitExpression(expression, expectedExpressionType)

        return expectedType
    }

    private fun visitListContext(ctx: stellaParser.ListContext, expectedType: IType?): IType? {
        val expressions = ctx.exprs
        if (extensionManager.typeReconstruction) {
            val elementType = TypeVar.new()
            expressions.forEach { visitExpression(it, elementType) }

            return validateTypes(ListType(elementType), expectedType, ctx)
        }

        if (expectedType != null && expectedType !is ListType) {
            val listType = visitListContext(ctx, null) ?: return null
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_LIST,
                expectedType,
                listType
            )

            return null
        }

        if (expectedType == null && expressions.isEmpty()) {
            errorManager.registerError(
                StellaErrorType.ERROR_AMBIGUOUS_LIST_TYPE,
                ctx
            )

            return null
        }

        val expressionTypes = expressions.map { visitExpression(it, null) }
        if (expressionTypes.any { it == null }) {
            return null
        }

        val listType = (expectedType as? ListType?)?.type ?: expressionTypes.firstOrNull { it != null } ?: BotType

        if (extensionManager.typeReconstruction) {
            expressionTypes.filterNotNull().forEach { expType ->
                unifySolver.addConstraint(listType, expType, ctx)
            }

            return ListType(listType)
        }

        val firstWrongTypedExpressionIndex = expressionTypes.indexOfFirst { it != listType }

        if (firstWrongTypedExpressionIndex != -1) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                listType,
                expressionTypes[firstWrongTypedExpressionIndex]!!,
                expressions[firstWrongTypedExpressionIndex]
            )

            return null
        }

        return ListType(listType)
    }

    private fun visitConsList(ctx: stellaParser.ConsListContext, expectedType: IType?): IType? {
        if (extensionManager.typeReconstruction) {
            val elementType = TypeVar.new()
            val listType = ListType(elementType)

            val head = ctx.head
            visitExpression(head, elementType) ?: return null

            val tail = ctx.tail
            visitExpression(tail, listType) ?: return null

            return validateTypes(listType, expectedType, ctx)
        }

        if (expectedType != null && expectedType !is ListType && expectedType !is TopType) {
            val listType = visitConsList(ctx, null) ?: return null
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_LIST,
                expectedType,
                listType
            )

            return null
        }

        val head = ctx.head
        val headType = visitExpression(head, null) ?: return null

        if (expectedType != null && expectedType is ListType) {
            validateTypes(headType, expectedType.type, ctx) ?: return null
        }

        val resultType = ListType(headType)
        val tail = ctx.tail
        visitExpression(tail, resultType) ?: return null

        return resultType
    }

    @Suppress("DuplicatedCode")
    private fun visitHead(ctx: stellaParser.HeadContext, expectedType: IType?): IType? {
        val listType = if (expectedType != null) {
            ListType(expectedType)
        } else {
            null
        }

        val list = ctx.list
        val expressionType = visitExpression(list, listType) ?: return null
        if (expressionType !is ListType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_LIST,
                expressionType,
                list
            )

            return null
        }

        val actualType = expressionType.type
        return validateTypes(actualType, expectedType, ctx)
    }

    @Suppress("DuplicatedCode")
    private fun visitTail(ctx: stellaParser.TailContext, expectedType: IType?): IType? {
        if (expectedType != null && expectedType !is ListType) {
            val actualType = visitTail(ctx, null) ?: return null
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                expectedType,
                actualType,
                ctx
            )

            return null
        }

        val list = ctx.list

        val expressionType = visitExpression(list, expectedType) ?: return null
        if (expressionType !is ListType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_LIST,
                expressionType,
                list
            )

            return null
        }

        return validateTypes(expressionType, expectedType, ctx)
    }

    private fun visitIsEmpty(ctx: stellaParser.IsEmptyContext, expectedType: IType?): BoolType? {
        if (expectedType != null && expectedType !is BoolType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                expectedType,
                BoolType,
                ctx
            )

            return null
        }

        val expression = ctx.expr()
        val expressionType = visitExpression(expression, null) ?: return null
        if (expressionType !is ListType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_LIST,
                expressionType,
                expression
            )

            return null
        }

        return BoolType
    }

    private fun visitSequence(ctx: stellaParser.SequenceContext, expectedType: IType?): IType? {
        visitExpression(ctx.expr1, UnitType) ?: return null

        return visitExpression(ctx.expr2, expectedType)
    }

    private fun visitRef(ctx: stellaParser.RefContext, expectedType: IType?): IType? {
        if (expectedType != null && expectedType !is ReferenceType && expectedType !is TopType) {
            val ref = visitRef(ctx, null) ?: return null
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_REFERENCE,
                ref,
                expectedType
            )

            return null
        }

        val innerExpectedType = if (expectedType != null && expectedType is ReferenceType) {
            expectedType.innerType
        } else null

        val innerType = visitExpression(ctx.expr_, innerExpectedType) ?: return null

        return validateTypes(ReferenceType(innerType), expectedType, ctx)
    }

    private fun visitConstMemory(ctx: stellaParser.ConstMemoryContext, expectedType: IType?): IType? {
        if (expectedType == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_AMBIGUOUS_REFERENCE_TYPE,
                ctx
            )

            return null
        }

        if (expectedType !is ReferenceType && expectedType !is TopType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_MEMORY_ADDRESS,
                ctx,
                expectedType
            )

            return null
        }

        return expectedType
    }

    private fun visitDeref(ctx: stellaParser.DerefContext, expectedType: IType?): IType? {
        val expectedRefType = expectedType?.let { ReferenceType(it) }
        val refType = visitExpression(ctx.expr_, expectedRefType) ?: return null

        if (refType !is ReferenceType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_REFERENCE,
                refType
            )

            return null
        }

        return validateTypes(refType.innerType, expectedType, ctx)
    }

    private fun visitAssign(ctx: stellaParser.AssignContext, expectedType: IType?): UnitType? {
        if (expectedType != null && expectedType !is UnitType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                UnitType,
                expectedType,
                ctx
            )

            return null
        }

        val lhsType = visitExpression(ctx.lhs, null) ?: return null
        if (lhsType !is ReferenceType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_REFERENCE,
                lhsType
            )

            return null
        }

        val rhsType = visitExpression(ctx.rhs, null) ?: return null

        if (lhsType.innerType != rhsType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                lhsType.innerType,
                rhsType,
                ctx
            )

            return null
        }

        return UnitType
    }

    private fun visitPanic(ctx: stellaParser.PanicContext, expectedType: IType?): IType? {
        if (expectedType == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_AMBIGUOUS_PANIC_TYPE,
                ctx
            )

            return null
        }

        return expectedType
    }

    private fun visitThrow(ctx: stellaParser.ThrowContext, expectedType: IType?): IType? {
        if (expectedType == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_AMBIGUOUS_THROW_TYPE,
                ctx
            )

            return null
        }

        val exceptionType = context.exceptionType
        if (exceptionType == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_EXCEPTION_TYPE_NOT_DECLARED
            )

            return null
        }

        visitExpression(ctx.expr_, exceptionType) ?: return null

        return expectedType
    }

    @Suppress("DuplicatedCode")
    private fun visitTryWith(ctx: stellaParser.TryWithContext, expectedType: IType?): IType? {
        val exceptionType = context.exceptionType
        if (exceptionType == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_EXCEPTION_TYPE_NOT_DECLARED
            )

            return null
        }

        val mainType = visitExpression(ctx.tryExpr, expectedType) ?: return null
        visitExpression(ctx.fallbackExpr, mainType) ?: return null

        return mainType
    }

    @Suppress("DuplicatedCode")
    private fun visitTryCatch(ctx: stellaParser.TryCatchContext, expectedType: IType?): IType? {
        val exceptionType = context.exceptionType
        if (exceptionType == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_EXCEPTION_TYPE_NOT_DECLARED
            )

            return null
        }

        val mainType = visitExpression(ctx.tryExpr, expectedType) ?: return null

        val patternInCatch = ctx.pat
        if (patternInCatch !is stellaParser.PatternVarContext) {
            // only variables are supported here
            return expectedType
        }

        val varName = patternInCatch.name.text

        val newContext = TypeContext(context)
        newContext.saveVariableType(varName, exceptionType)
        val newTypeChecker = TypeChecker(errorManager, newContext, extensionManager, unifySolver)

        newTypeChecker.visitExpression(ctx.fallbackExpr, expectedType) ?: return null

        return mainType
    }

    private fun visitTypeCast(ctx: stellaParser.TypeCastContext, expectedType: IType?): IType? {
        visitExpression(ctx.expr_, null) ?: return null
        val actualType = SyntaxTypeProcessor.getType(ctx.type_)

        return validateTypes(actualType, expectedType, ctx)
    }
}
