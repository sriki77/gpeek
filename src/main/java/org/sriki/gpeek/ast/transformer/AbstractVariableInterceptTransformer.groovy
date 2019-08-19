package org.sriki.gpeek.ast.transformer

import org.codehaus.groovy.ast.ClassCodeExpressionTransformer
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.control.SourceUnit
import org.sriki.gpeek.Utils

abstract class AbstractVariableInterceptTransformer extends ClassCodeExpressionTransformer {
    private Stack<Boolean> leftExpression = new Stack<>()
    private SourceUnit sourceUnit

    AbstractVariableInterceptTransformer(SourceUnit sourceUnit) {
        this.sourceUnit = sourceUnit
    }

    @Override
    Expression transform(Expression exp) {
        def isLeftExpression = Utils.hasPropertyNamed(exp, 'isLeft')
        if (isLeftExpression) {
            leftExpression.push(Boolean.TRUE)
        }
        def expression = handleExp(exp)
        if (expression instanceof BinaryExpression) {
            BinaryExpression be = expression as BinaryExpression
            if (be.operation.text == '=') {
                expression.getLeftExpression().metaClass.isLeft = true
            }
        }
        def transform = super.transform(expression)
        if (isLeftExpression) {
            leftExpression.pop()
        }
        return transform
    }

    private Expression handleExp(Expression exp) {
        leftExpression.isEmpty() ? handleMethodCallExp(handlePropertyExp(handleVarExp(exp))) : exp
    }

    protected SourceUnit getSourceUnit() { sourceUnit }

    protected abstract Expression handleVarExp(Expression exp)

    protected abstract Expression handlePropertyExp(Expression exp)

    protected abstract Expression handleMethodCallExp(Expression exp)

}