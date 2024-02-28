package checkers.structure

import checkers.errors.ErrorManager
import checkers.errors.StellaErrorType
import functionName
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
