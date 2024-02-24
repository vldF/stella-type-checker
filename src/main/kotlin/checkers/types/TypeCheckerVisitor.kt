package checkers.types

import checkers.errors.ErrorManager
import checkers.errors.StellaErrorType
import functionName
import paramName
import stellaParser
import stellaParserBaseVisitor
import types.FunctionalType
import types.TypeContext
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

        val typeInferrer = TypeInferrer(errorManager, functionContext)
        val returnExpr = ctx.returnExpr
        val returnExpressionType = returnExpr.accept(typeInferrer)

        if (returnExpressionType is FunctionalType && expectedFunctionType !is FunctionalType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_LAMBDA, returnExpr)
        }

        if (expectedFunctionType != returnExpressionType) {
            errorManager.registerError(StellaErrorType.ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION, returnExpr)
        }
    }
}
