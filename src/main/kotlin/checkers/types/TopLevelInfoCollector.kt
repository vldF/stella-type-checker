package checkers.types

import org.antlr.v4.runtime.tree.RuleNode
import stellaParser
import stellaParserBaseVisitor
import types.FunctionalType
import types.SyntaxTypeProcessor
import types.TypeContext
import utils.functionName

class TopLevelInfoCollector(
    private val typeContext: TypeContext,
) : stellaParserBaseVisitor<Unit>() {
    override fun visitDeclFun(ctx: stellaParser.DeclFunContext) {
        val name = ctx.functionName

        val argType = SyntaxTypeProcessor.getType(ctx.paramDecl.paramType)
        val returnType = SyntaxTypeProcessor.getType(ctx.returnType)
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
