package types

object SyntaxTypeProcessor {
    fun getType(ctx: stellaParser.StellatypeContext): IType {
        return when(ctx) {
            is stellaParser.TypeBoolContext -> BoolType
            is stellaParser.TypeUnitContext -> UnitType
            is stellaParser.TypeNatContext -> NatType
            is stellaParser.TypeTupleContext -> visitTypeTuple(ctx)
            is stellaParser.TypeRecordContext -> visitTypeRecord(ctx)
            is stellaParser.TypeFunContext -> visitTypeFun(ctx)
            is stellaParser.TypeParensContext -> getType(ctx.type_)
            is stellaParser.TypeSumContext -> visitSumType(ctx)
            else -> UnknownType
        }
    }

    private fun visitTypeTuple(ctx: stellaParser.TypeTupleContext): TupleType {
        val types = ctx.types.map { getType(it) }

        return TupleType(types.toTypedArray())
    }

    private fun visitTypeRecord(ctx: stellaParser.TypeRecordContext): RecordType {
        val fieldContexts = ctx.fieldTypes
        val labels = fieldContexts.map { field -> field.label.text }
        val types = fieldContexts.map { field -> getType(field.type_) }

        return RecordType(labels.toTypedArray(), types.toTypedArray())
    }

    private fun visitTypeFun(ctx: stellaParser.TypeFunContext): FunctionalType {
        val paramType = getType(ctx.paramTypes.first())
        val returnType = getType(ctx.returnType)

        return FunctionalType(paramType, returnType)
    }

    private fun visitSumType(ctx: stellaParser.TypeSumContext): SumType {
        val left = getType(ctx.left)
        val right = getType(ctx.right)

        return SumType(left, right)
    }
}
