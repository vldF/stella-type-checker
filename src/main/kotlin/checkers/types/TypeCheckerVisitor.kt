package checkers.types

import checkers.errors.ErrorManager
import stellaParser
import stellaParserBaseVisitor
import types.TypeContext
import types.inference.TypeInferrer
import utils.functionName
import utils.paramName

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

        typeInferrer.visitExpression(returnExpr, expectedFunctionRetType)
    }
}
