package org.sriki.gpeek.ast.transformer

import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.control.SourceUnit
import org.sriki.gpeek.intercept.VariableInterceptor

class VariableInterceptResultTransformer extends AbstractVariableInterceptTransformer {
    private String methodName

    VariableInterceptResultTransformer(String methodName, SourceUnit sourceUnit) {
        super(sourceUnit)
        this.methodName = methodName
    }


    protected Expression handleVarExp(Expression exp) {
        if (!(exp instanceof VariableExpression)
                || exp.isThisExpression()) return exp

        def name = exp.name
        def value = VariableInterceptor.valueFor(methodName, name)
        if (value != null) {
            return new GStringExpression("$name=$value")
        } else exp
    }

    @Override
    protected Expression handlePropertyExp(Expression exp) {
        if (!(exp instanceof PropertyExpression)) return exp
        PropertyExpression propExp = exp
        if (propExp.property instanceof ConstantExpression) {
            def var = exp.property as ConstantExpression
            def value = VariableInterceptor.valueFor(methodName, "_p_${var.value}")
            return value == null ? exp : new GStringExpression("obj.${var.value}=" + "${value}")
        }
        return exp
    }

    @Override
    protected Expression handleMethodCallExp(Expression exp) {
        if (!(exp instanceof MethodCallExpression) &&
                !(exp instanceof StaticMethodCallExpression)) return exp
        def method = exp.methodAsString
        ArgumentListExpression arguments = super.transform(exp.arguments) as ArgumentListExpression
        def argString = arguments.find {
            it instanceof GStringExpression
        }.collect { (it as GStringExpression).text }.join(',')
        def value = VariableInterceptor.valueFor(methodName, "_m_${method}")
        return value == null ? exp : new GStringExpression("${exp.receiver.text}.${method}($argString)=" + "${value}")
    }
}