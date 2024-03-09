package checkers.structure

import checkers.errors.ErrorManager
import checkers.errors.StellaErrorType
import utils.functionName
import org.antlr.v4.runtime.tree.ParseTree
import stellaParser
import stellaParserBaseVisitor

class MainFunctionCheckerVisitor(
    private val errorManager: ErrorManager,
) : stellaParserBaseVisitor<Unit>() {
    private var isMainDiscovered = false
    private val mainFunctionName = "main"

    override fun visitDeclFun(ctx: stellaParser.DeclFunContext?) {
        if (ctx != null && ctx.functionName == mainFunctionName) {
            isMainDiscovered = true
            if (ctx.paramDecls.size != 1) {
                errorManager.registerError(
                    StellaErrorType.ERROR_INCORRECT_ARITY_OF_MAIN,
                    ctx.paramDecls.size
                )
            }
        }

        return super.visitDeclFun(ctx)
    }

    override fun visit(tree: ParseTree?) {
        if (isMainDiscovered) {
            return
        }

        super.visit(tree)
    }

    override fun visitProgram(ctx: stellaParser.ProgramContext) {
        super.visitProgram(ctx)

        if (!isMainDiscovered) {
            errorManager.registerError(
                StellaErrorType.ERROR_MISSING_MAIN
            )
        }
    }
}
