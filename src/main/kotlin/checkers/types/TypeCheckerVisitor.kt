package checkers.types

import checkers.errors.ErrorManager
import checkers.errors.StellaErrorType
import utils.functionName
import org.antlr.v4.runtime.ParserRuleContext
import utils.paramName
import stellaParser
import stellaParserBaseVisitor
import types.*
import types.inference.DumbTypeInferrer
import types.inference.TypeInferrer

class TypeCheckerVisitor(
    private val errorManager: ErrorManager,
    parentTypeContext: TypeContext? = null
) : stellaParserBaseVisitor<Unit>() {
    private val typeContext = TypeContext(parentTypeContext)

    override fun visitProgram(ctx: stellaParser.ProgramContext) {
        val topLevelInfoCollector = TopLevelInfoCollector(typeContext)
        topLevelInfoCollector.visitProgram(ctx)

        super.visitProgram(ctx)
    }

    override fun visitDeclFun(ctx: stellaParser.DeclFunContext) {
        val functionName = ctx.functionName
        val functionType = typeContext.resolveFunctionType(functionName) ?: return
        val expectedFunctionRetType = functionType.to

        val functionContext = TypeContext(typeContext)
        functionContext.saveVariableType(ctx.paramDecl.paramName, functionType.from)

        val topLevelInfoCollector = TopLevelInfoCollector(functionContext)
        ctx.children.forEach { c -> topLevelInfoCollector.visit(c) }

        val innerTypeCheckerVisitor = TypeCheckerVisitor(errorManager, functionContext)
        ctx.children.forEach { c -> innerTypeCheckerVisitor.visit(c) }

        val returnExpr = ctx.returnExpr
        val typeInferrer = TypeInferrer(errorManager, functionContext)

//        if (!dumpCheckFunctionReturnType(expectedFunctionRetType, returnExpr, functionContext)) {
//            return
//        }

        typeInferrer.visitExpression(returnExpr, expectedFunctionRetType)
    }

    private fun dumpCheckFunctionReturnType(
        expected: IType,
        retExpression: stellaParser.ExprContext,
        functionTypeContext: TypeContext
    ): Boolean {
        val dumbTypeInferrer = DumbTypeInferrer()

        val retExpressionType = dumbTypeInferrer.getType(retExpression)
        val absArgSemanticType by lazy {
            val typeInferrer = TypeInferrer(errorManager = null, functionTypeContext)
            typeInferrer.visitExpression(retExpression, expected) ?: UnknownType
        }

        when (retExpressionType) {
            is UnknownType -> {
                return true // continue type checking
            }

            is FunctionalType -> {
                if (expected !is FunctionalType) {
                    errorManager.registerError(
                        StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                        expected,
                        absArgSemanticType,
                        retExpression
                    )
                    return false
                }

                if (retExpressionType.from != expected.from) {
                    val errorNode = (retExpression as? stellaParser.AbstractionContext)?.paramDecl ?: retExpression
                    errorManager.registerError(
                        StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_PARAMETER,
                        expected.from,
                        (absArgSemanticType as FunctionalType).from,
                        errorNode
                    )
                    return false
                }
                    return true
            }

            is TupleType -> {
                if (expected !is TupleType) {
                    errorManager.registerError(
                        StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION,
                        expected,
                        absArgSemanticType,
                        retExpression
                    )
                    return false
                }

                return true
            }

            is RecordType -> {
                if (expected !is RecordType) {
                    errorManager.registerError(
                        StellaErrorType.ERROR_UNEXPECTED_RECORD,
                        expected,
                        absArgSemanticType
                    )
                    return false
                }

                return true
            }

            // todo: ERROR_UNEXPECTED_LIST
            // todo: ERROR_UNEXPECTED_INJECTION
            else -> {}
        }

        return true
    }
}
