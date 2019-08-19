package org.sriki.gpeek.ast.transformer


import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.SourceUnit

import static org.sriki.gpeek.intercept.VariableInterceptor.buildInterceptCall

class VariableInterceptTransformer extends AbstractVariableInterceptTransformer {
    private String methodName

    VariableInterceptTransformer(String methodName, SourceUnit sourceUnit) {
        super(sourceUnit)
        this.methodName = methodName
    }

    @Override
    protected Expression handleVarExp(Expression exp) {
        if (!(exp instanceof VariableExpression)
                || exp.isThisExpression()) return exp

        VariableExpression varExp = exp
        def expressionStatement = new ExpressionStatement(varExp)
        StaticMethodCallExpression expression = buildInterceptCall(expressionStatement,
                methodName, varExp.name)
        return expression
    }

    @Override
    protected Expression handlePropertyExp(Expression exp) {
        if (!(exp instanceof PropertyExpression)) return exp

        PropertyExpression propExp = exp
        if (exp.property instanceof ConstantExpression) {
            def prop = exp.property as ConstantExpression
            def expressionStatement = new ExpressionStatement(propExp)
            StaticMethodCallExpression expression = buildInterceptCall(expressionStatement,
                    methodName, "_p_${prop.value}")
            return expression
        }
        return exp
    }

    @Override
    protected Expression handleMethodCallExp(Expression exp) {
        if (exp instanceof MethodCallExpression) return methodCallExp(exp)
        if (exp instanceof StaticMethodCallExpression) return staticMethodCallExp(exp)
        return exp
    }

    private StaticMethodCallExpression methodCallExp(MethodCallExpression exp) {
        def method = exp.methodAsString
        if (method == "intercept") return exp
        exp.arguments = super.transform(exp.arguments)
        def expressionStatement = new ExpressionStatement(exp)
        return buildInterceptCall(expressionStatement, methodName, "_m_${method}")
    }

    private StaticMethodCallExpression staticMethodCallExp(StaticMethodCallExpression exp) {
        def method = exp.methodAsString
        if (method == "intercept") return exp
        def arguments = super.transform(exp.arguments)
        def expressionStatement = new ExpressionStatement(new StaticMethodCallExpression(exp.ownerType, exp.method, arguments))
        return buildInterceptCall(expressionStatement, methodName, "_m_${method}")
    }
}