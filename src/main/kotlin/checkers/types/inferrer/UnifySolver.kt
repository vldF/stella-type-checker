package checkers.types.inferrer

import org.antlr.v4.runtime.ParserRuleContext
import types.*

sealed class UnificationResult

data object UnificationOk : UnificationResult()

data class UnificationFailed(
    val expectedType: IType,
    val actualType: IType,
    val expression: ParserRuleContext
) : UnificationResult()

data class UnificationFailedInfiniteType(
    val expectedType: IType,
    val actualType: IType,
    val expression: ParserRuleContext
) : UnificationResult()

class UnifySolver {
    private val storedConstraints = mutableListOf<Constraint>()

    fun addConstraint(
        left: IType,
        right: IType,
        ruleContext: ParserRuleContext
    ) {
        storedConstraints.add(Constraint(left, right, ruleContext))
    }

    fun solve(): UnificationResult {
        return try {
            solveInternal(storedConstraints)
            UnificationOk
        } catch (e: UnificationFailedException) {
             UnificationFailed(e.expectedType, e.actualType, e.expression)
        } catch (e: InfiniteTypeException) {
            UnificationFailedInfiniteType(e.expectedType, e.actualType, e.expression)
        }

    }

    private class UnificationFailedException(
        val expectedType: IType,
        val actualType: IType,
        val expression: ParserRuleContext
    ) : Exception()

    private class InfiniteTypeException(
        val expectedType: IType,
        val actualType: IType,
        val expression: ParserRuleContext
    ) : Exception()

    private tailrec fun solveInternal(constraints: List<Constraint>) {
        if (constraints.isEmpty()) {
            return
        }

        val constraint = constraints.first()
        val remainingConstraints = constraints.drop(1)

        val left = constraint.left
        val right = constraint.right
        val ruleContext = constraint.ruleContext

        return when {
            left is TypeVar && right is TypeVar && left == right -> {
                solveInternal(remainingConstraints)
            }
            left is TypeVar && !left.containsIn(right, ruleContext) -> {
                solveInternal(remainingConstraints.replace(left, right))
            }
            right is TypeVar && !right.containsIn(left, ruleContext) -> {
                solveInternal(remainingConstraints.replace(right, left))
            }
            left is FunctionalType && right is FunctionalType -> {
                solveInternal(
                    remainingConstraints + listOf(
                        Constraint(left.from, right.from, ruleContext),
                        Constraint(left.to, right.to, ruleContext),
                    )
                )
            }
            left is SumType && right is SumType -> {
                solveInternal(
                    remainingConstraints + listOf(
                        Constraint(left.left, right.left, ruleContext),
                        Constraint(left.right, right.right, ruleContext)
                    )
                )
            }
            left is ListType && right is ListType -> {
                solveInternal(
                    remainingConstraints + listOf(
                        Constraint(left.type, right.type, ruleContext),
                    )
                )
            }
            left is TupleType && right is TupleType -> {
                solveInternal(remainingConstraints +
                left.types.zip(right.types).map { (t1, t2) -> Constraint(t1, t2, ruleContext) })
            }
            left !is TypeVar && right !is TypeVar && left == right -> {
                solveInternal(remainingConstraints)
            }
            else -> throw UnificationFailedException(left, right, ruleContext)
        }
    }

    private fun TypeVar.containsIn(type: IType, expression: ParserRuleContext): Boolean {
        val result = when (type) {
            is FunctionalType -> containsIn(type.from, expression) || containsIn(type.to, expression)
            is ListType -> containsIn(type.type, expression)
            is SumType -> containsIn(type.left, expression) || containsIn(type.right, expression)
            is TupleType -> type.types.any { containsIn(it, expression) }
            is TypeVar -> this == type
            else -> false
        }

        if (result) {
            throw InfiniteTypeException(this, type, expression)
        }

        return false
    }

    private fun List<Constraint>.replace(what: TypeVar, to: IType): List<Constraint> {
        return this.map { it.replace(what, to) }
    }

    private fun Constraint.replace(what: TypeVar, to: IType): Constraint {
        return Constraint(this.left.replace(what, to), this.right.replace(what, to), this.ruleContext)
    }

    private fun IType.replace(what: TypeVar, to: IType): IType {
        return when (this) {
            is FunctionalType -> FunctionalType(this.from.replace(what, to), this.to.replace(what, to))
            is ListType -> ListType(this.type.replace(what, to))
            is SumType -> SumType(this.left.replace(what, to), this.right.replace(what, to))
            is TupleType -> TupleType(this.types.map { it.replace(what, to) })
            is TypeVar -> if (this == what) to else this
            else -> this
        }
    }
}
