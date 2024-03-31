package checkers.types

import checkers.errors.ErrorManager
import checkers.errors.StellaErrorType
import org.antlr.v4.runtime.ParserRuleContext
import utils.paramName
import stellaParser
import types.*
import utils.functionName

internal class TypeChecker(
    private val errorManager: ErrorManager,
    parentContext: TypeContext? = null
) {
    private val context = TypeContext(parentContext)

    fun checkProgram(ctx: stellaParser.ProgramContext) {
        val topLevelInfoCollector = TopLevelInfoCollector(context)
        topLevelInfoCollector.visitProgram(ctx)

        ctx.decls.forEach(::visitDecl)
    }
    
    private fun visitDecl(ctx: stellaParser.DeclContext) {
        when (ctx) {
            is stellaParser.DeclFunContext -> visitDeclFun(ctx)
        }
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

        val innerTypeCheckerVisitor = TypeChecker(errorManager, functionContext)
        ctx.localDecls.forEach { c -> innerTypeCheckerVisitor.visitDecl(c) }

        val returnExpr = ctx.returnExpr
        val typeInferrer = TypeChecker(errorManager, functionContext)

        typeInferrer.visitExpression(returnExpr, expectedFunctionRetType)
    }

    private fun visitIf(ctx: stellaParser.IfContext, expectedType: IType?): IType? {
        val condition = ctx.condition
        visitExpression(condition, BoolType) ?: return null

        val thenType = visitExpression(ctx.thenExpr, expectedType) ?: return null
        val elseType = visitExpression(ctx.elseExpr, expectedType) ?: return null

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
        val stepFunctionType = visitExpression(ctx.step, null) ?: return null

        if (stepFunctionType !is FunctionalType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                FunctionalType.unknownFunctionalTypeName,
                stepFunctionType,
                ctx.step
            )
            return null
        }

        if (stepFunctionType.from != NatType) {
            val errorNode = (ctx.step as? stellaParser.AbstractionContext)?.paramDecl ?: ctx.step

            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_PARAMETER,
                NatType,
                stepFunctionType.from,
                errorNode
            )
            return null
        }

        if (stepFunctionType.to !is FunctionalType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                FunctionalType.unknownFunctionalTypeName,
                stepFunctionType.to,
                ctx.step
            )
            return null
        }

        if (stepFunctionType.to.from != stepFunctionType.to.to) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                stepFunctionType.to.to,
                stepFunctionType.to.from,
                ctx.step
            )
            return null
        }

        if (stepFunctionType.to.from != initialValueType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                initialValueType,
                stepFunctionType.to.from,
                ctx.step
            )
            return null
        }

        return initialValueType
    }

    private fun visitAbstraction(ctx: stellaParser.AbstractionContext, expectedType: IType?): FunctionalType? {
        if (expectedType != null && expectedType !is FunctionalType) {
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
        val innerInferrer = TypeChecker(errorManager, innerContext)

        val returnExpr = ctx.returnExpr
        val returnType = innerInferrer.visitExpression(returnExpr, null) ?: return null

        val result = FunctionalType(argType, returnType)
        return validateTypes(result, expectedType, ctx) as FunctionalType?
    }

    private fun visitApplication(ctx: stellaParser.ApplicationContext, expectedType: IType?): IType? {
        val func = ctx.`fun`

        val funType = visitExpression(func, null) ?: return null

        if (funType !is FunctionalType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_FUNCTION,
                funType,
                func
            )
            return null
        }

        val arg = ctx.args.first()
        visitExpression(arg, funType.from) ?: return null

        val resultType = funType.to
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

        if (expectedType != null && type != expectedType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                expectedType,
                type,
                ctx
            )
        }

        return type
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
        if (expressionType !is TupleType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_TUPLE,
                expressionType,
                ctx
            )
            return null
        }

        val indexContext = ctx.index
        val indexValue = indexContext.text.toInt()
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
    private fun visitRecord(ctx: stellaParser.RecordContext, expectedType: IType?): RecordType? {
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
        if (expectedType != null) {
            if (!canRecordDefMatchExpectedType(expectedType, labels, types, ctx)) {
                return null
            }

            return expectedType
        }

        return RecordType(labels, types)
    }

    private fun canRecordDefMatchExpectedType(
        expectedRecord: RecordType,
        actualLabels: List<String>,
        actualTypes: List<IType>,
        ctx: stellaParser.RecordContext
    ): Boolean {
        val missingFields = expectedRecord.labels.toSet() - actualLabels.toSet()
        val extraFields = actualLabels.toSet() - expectedRecord.labels.toSet()

        if (extraFields.isNotEmpty()) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_RECORD_FIELDS,
                extraFields.first(),
                expectedRecord
            )

            return false
        }

        if (missingFields.isNotEmpty()) {
            errorManager.registerError(
                StellaErrorType.ERROR_MISSING_RECORD_FIELDS,
                missingFields.first(),
                expectedRecord
            )

            return false
        }

        for ((label, type) in actualLabels.zip(actualTypes)) {
            val expectedTypeForLabelIdx = expectedRecord.labels.indexOf(label)
            val expectedTypeForLabel = expectedRecord.types[expectedTypeForLabelIdx]

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

                if (!canRecordDefMatchExpectedType(expectedTypeForLabel, type.labels, type.types, ctx)) {
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

        val letTypeInferrer = TypeChecker(errorManager, letContext)
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

        if (actualType is FunctionalType && expectedType !is FunctionalType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_LAMBDA,
                expectedType,
                actualType,
                expression
            )

            return null
        }

        if (actualType is TupleType && expectedType !is TupleType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TUPLE,
                expectedType,
                actualType
            )

            return null
        }

        if (actualType is RecordType && expectedType !is RecordType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_RECORD,
                expectedType,
                actualType
            )

            return null
        }

        if (actualType !is FunctionalType && expectedType is FunctionalType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_FUNCTION,
                actualType,
                expression
            )

            return null
        }

        if (actualType !is TupleType && expectedType is TupleType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_TUPLE,
                actualType,
                expression
            )

            return null
        }

        if (actualType !is RecordType && expectedType is RecordType) {
            errorManager.registerError(
                StellaErrorType.ERROR_NOT_A_RECORD,
                actualType,
                expression
            )

            return null
        }

        if (actualType is TupleType && expectedType is TupleType) {
            if (actualType.arity != expectedType.arity) {
                errorManager.registerError(
                    StellaErrorType.ERROR_UNEXPECTED_TUPLE_LENGTH,
                    expectedType.arity,
                    actualType.arity,
                    expression
                )
            }
        }

        if (actualType != expectedType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                expectedType,
                actualType,
                expression
            )

            return null
        }

        return actualType
    }

    private fun visitFix(ctx: stellaParser.FixContext, expectedType: IType?): IType? {
        val expression = ctx.expr_

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
        val expression = ctx.expr_
        val expressionType = visitExpression(expression, null) ?: return null

        val cases = ctx.cases

        if (cases.isEmpty()) {
            errorManager.registerError(
                StellaErrorType.ERROR_ILLEGAL_EMPTY_MATCHING,
                ctx
            )

            return null
        }

        val patterns = cases.map { it.pattern_ }
        val exhaustivenessChecker = ExhaustivenessChecker()
        if (!exhaustivenessChecker.checkForPatternsTypeMissmatch(patterns, expressionType, errorManager)) {
            return null
        }

        val wrongPattern = exhaustivenessChecker.findWrongPattern(patterns, expressionType)
        if (wrongPattern != null) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_PATTERN_FOR_TYPE,
                wrongPattern,
                ctx
            )

            return null
        }

        val arePatternsExhaustive = exhaustivenessChecker.arePatternsExhaustive(patterns, expressionType)
        if (!arePatternsExhaustive) {
            errorManager.registerError(
                StellaErrorType.ERROR_NONEXHAUSTIVE_MATCH_PATTERNS,
                expressionType
            )

            return null
        }

        var resultType: IType? = null

        for (case in cases) {
            val caseContext = TypeContext(context)
            val caseInferrer = TypeChecker(errorManager, caseContext)

            val pattern = case.pattern_
            val bodyExpr = case.expr_

            @Suppress("DuplicatedCode")
            val caseType = when (pattern) {
                is stellaParser.PatternInlContext -> {
                    expressionType as SumType
                    val variableName = (pattern.pattern_ as? stellaParser.PatternVarContext)?.name?.text ?: return null
                    val type = expressionType.left
                    caseContext.saveVariableType(variableName, type)

                    caseInferrer.visitExpression(bodyExpr, expectedType)
                }

                is stellaParser.PatternInrContext -> {
                    expressionType as SumType
                    val variableName = (pattern.pattern_ as? stellaParser.PatternVarContext)?.name?.text ?: return null
                    val type = expressionType.right
                    caseContext.saveVariableType(variableName, type)

                    caseInferrer.visitExpression(bodyExpr, expectedType)
                }

                is stellaParser.PatternVariantContext -> {
                    expressionType as VariantType
                    val labelName = pattern.label.text
                    val labelIndex = expressionType.labels.indexOf(labelName)
                    if (labelIndex == -1) {
                        errorManager.registerError(
                            StellaErrorType.ERROR_UNEXPECTED_VARIANT_LABEL,
                            labelName,
                            pattern,
                            expectedType ?: return null
                        )

                        return null
                    }

                    val labelType = expressionType.types[labelIndex]

                    val variableName = (pattern.pattern_ as? stellaParser.PatternVarContext)?.name?.text ?: return null

                    caseContext.saveVariableType(variableName, labelType)

                    caseInferrer.visitExpression(bodyExpr, expectedType)
                }

                is stellaParser.PatternVarContext -> {
                    val variableName = pattern.name.text
                    caseContext.saveVariableType(variableName, expressionType)

                    caseInferrer.visitExpression(bodyExpr, expectedType)
                }

                else -> { // todo
                    caseInferrer.visitExpression(bodyExpr, expectedType)
                }
            }

            caseType ?: return null

            if (resultType != null && resultType != caseType) {
                errorManager.registerError(
                    StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                    resultType,
                    caseType,
                    bodyExpr
                )
            }

            resultType = caseType
        }

        return resultType
    }

    @Suppress("DuplicatedCode")
    private fun visitInl(ctx: stellaParser.InlContext, expectedType: IType?): IType? {
        if (expectedType == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_AMBIGUOUS_SUM_TYPE,
                ctx
            )

            return null
        }

        if (expectedType !is SumType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_INJECTION,
                expectedType
            )

            return null
        }

        visitExpression(ctx.expr_, expectedType.left) ?: return null

        return expectedType
    }

    @Suppress("DuplicatedCode")
    private fun visitInr(ctx: stellaParser.InrContext, expectedType: IType?): IType? {
        if (expectedType == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_AMBIGUOUS_SUM_TYPE,
                ctx
            )

            return null
        }

        if (expectedType !is SumType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_INJECTION,
                expectedType
            )

            return null
        }

        visitExpression(ctx.expr_, expectedType.right) ?: return null

        return expectedType
    }

    private fun visitVariant(ctx: stellaParser.VariantContext, expectedType: IType?): VariantType? {
        if (expectedType == null) {
            errorManager.registerError(
                StellaErrorType.ERROR_AMBIGUOUS_VARIANT_TYPE,
                ctx
            )

            return null
        }

        if (expectedType !is VariantType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_VARIANT,
                expectedType
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

    private fun visitListContext(ctx: stellaParser.ListContext, expectedType: IType?): ListType? {
        if (expectedType != null && expectedType !is ListType) {
            val listType = visitListContext(ctx, null) ?: return null
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_LIST,
                expectedType,
                listType
            )

            return null
        }

        val expressions = ctx.exprs
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

        val listType = (expectedType as ListType?)?.type ?: expressionTypes.firstOrNull { it != null } ?: return null
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

    private fun visitConsList(ctx: stellaParser.ConsListContext, expectedType: IType?): ListType? {
        if (expectedType != null && expectedType !is ListType) {
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

        if (expectedType != null && (expectedType as ListType).type != headType) {
            errorManager.registerError(
                StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                expectedType.type,
                headType,
                head
            )

            return null
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
}
