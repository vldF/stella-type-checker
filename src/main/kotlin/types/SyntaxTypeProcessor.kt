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
            is stellaParser.TypeVariantContext -> visitVariantType(ctx)
            is stellaParser.TypeListContext -> visitListType(ctx)
            is stellaParser.TypeRefContext -> visitRefType(ctx)
            else -> error("unknown type")
        }
    }

    private fun visitTypeTuple(ctx: stellaParser.TypeTupleContext): TupleType {
        val types = ctx.types.map { getType(it) }

        return TupleType(types)
    }

    private fun visitTypeRecord(ctx: stellaParser.TypeRecordContext): RecordType {
        val fieldContexts = ctx.fieldTypes
        val labels = fieldContexts.map { field -> field.label.text }
        val types = fieldContexts.map { field -> getType(field.type_) }

        return RecordType(labels, types)
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

    private fun visitVariantType(ctx: stellaParser.TypeVariantContext): VariantType {
        val labels = ctx.fieldTypes.map { it.label.text }
        val types = ctx.fieldTypes.map { getType(it.type_) }

        return VariantType(labels, types)
    }

    private fun visitListType(ctx: stellaParser.TypeListContext): ListType {
        val type = getType(ctx.type_)

        return ListType(type)
    }

    private fun visitRefType(ctx: stellaParser.TypeRefContext): ReferenceType {
        val type = getType(ctx.type_)

        return ReferenceType(type)
    }
}
