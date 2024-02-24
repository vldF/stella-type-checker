package checkers.types

import checkers.errors.ErrorManager
import functionName
import org.antlr.v4.runtime.tree.RuleNode
import stellaParser
import stellaParserBaseVisitor
import types.FunctionalType
import types.TypeContext
import types.inference.TypeInferrer

class TopLevelInfoCollector(
    private val errorManager: ErrorManager,
    private val typeContext: TypeContext,
) : stellaParserBaseVisitor<Unit>() {
    override fun visitDeclFun(ctx: stellaParser.DeclFunContext) {
        val typeInferrer = TypeInferrer(errorManager, typeContext)

        val name = ctx.functionName

        val argType = ctx.paramDecl.accept(typeInferrer) ?: return
        val returnType = ctx.returnType.accept(typeInferrer) ?: return
        val functionType = FunctionalType(argType, returnType)

        typeContext.saveFunctionType(name, functionType)
    }

    override fun visitChildren(node: RuleNode) {
        val n = node.childCount
        for (i in 0 until n) {
            val c = node.getChild(i)
            if (c is stellaParser.DeclContext) {
                c.accept(this)
            }
        }
    }
}
