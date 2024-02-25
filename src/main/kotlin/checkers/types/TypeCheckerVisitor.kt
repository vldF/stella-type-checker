package checkers.types

import checkers.errors.ErrorManager
import checkers.errors.StellaErrorType
import functionName
import org.antlr.v4.runtime.ParserRuleContext
import paramName
import stellaParser
import stellaParserBaseVisitor
import types.*
import types.inference.DumbTypeInferrer
import types.inference.TypeInferrer

class TypeCheckerVisitor(
    private val errorManager: ErrorManager,
) : stellaParserBaseVisitor<Unit>() {
    private val typeContext = TypeContext()

    override fun visitProgram(ctx: stellaParser.ProgramContext) {
        val topLevelInfoCollector = TopLevelInfoCollector(errorManager, typeContext)
        topLevelInfoCollector.visitProgram(ctx)

        super.visitProgram(ctx)
    }

    override fun visitDeclFun(ctx: stellaParser.DeclFunContext) {
        val functionName = ctx.functionName
        val functionType = typeContext.resolveFunctionType(functionName) ?: return
        val expectedFunctionType = functionType.to

        val functionContext = TypeContext(typeContext)
        functionContext.saveVariableType(ctx.paramDecl.paramName, functionType.from)

        val topLevelInfoCollector = TopLevelInfoCollector(errorManager, functionContext)
        topLevelInfoCollector.visit(ctx)

        val returnExpr = ctx.returnExpr
        val typeInferrer = TypeInferrer(errorManager, functionContext)

        if (!dumpCheckFunctionReturnType(expectedFunctionType, returnExpr)) {
            return
        }

        val returnExpressionType = returnExpr.accept(typeInferrer) ?: return

        if (returnExpressionType is FunctionalType && expectedFunctionType !is FunctionalType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_LAMBDA, returnExpr)
        }

        if (expectedFunctionType != returnExpressionType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, returnExpr)
        }
    }

    private fun dumpCheckFunctionReturnType(
        expected: IType,
        retExpression: ParserRuleContext,
    ): Boolean {
        val dumbTypeInferrer = DumbTypeInferrer()

        val absArgType = dumbTypeInferrer.getType(retExpression)

        when (absArgType) {
            is UnknownType -> {
                return true // continue type checking
            }

            is FunctionalType -> {
                if (expected !is FunctionalType) {
                    errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, retExpression)
                    return false
                }

                if (absArgType != expected) {
                    errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_PARAMETER, retExpression)
                    return false
                }
                    return true
            }

            is TupleType -> {
                if (expected !is TupleType) {
                    errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TUPLE, retExpression)
                    return false
                }

                return true
            }

            is RecordType -> {
                if (expected !is RecordType) {
                    errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_RECORD, retExpression)
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
