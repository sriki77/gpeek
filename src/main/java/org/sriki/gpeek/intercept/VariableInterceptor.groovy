package org.sriki.gpeek.intercept

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.VariableScope
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement

@TupleConstructor(includes = 'methodName')
@ToString(includeNames = true)
class MethodData {
    String methodName
    Object methodResult
    Map<String, Object> variables = new HashMap().asSynchronized()

    def record(String varName, Object value) {
        if (varName == "${methodName}_result") {
            methodResult = value
        } else {
            variables.put(varName, value)
        }
    }
}

class VariableInterceptor {

    static Map<String, MethodData> methodDataMap = new HashMap().asSynchronized()

    static def resultFor(String methodName) {
        methodDataMap[methodName]?.methodResult
    }

    static <T> T intercept(String methodName, String varName, Closure<T> closure) {
        def result = closure.call()
        methodDataMap.putIfAbsent(methodName, new MethodData(methodName))
        methodDataMap.get(methodName).record(varName, result)
        return result
    }

    static def valueFor(String methodName, String variableName) {
        methodDataMap[methodName]?.variables?.get(variableName)
    }

    static StaticMethodCallExpression buildInterceptCall(
            Statement closureBody,
            String methodName,
            String variableName) {
        def statement = new BlockStatement()
        statement.addStatement(closureBody)
        def closureExpression = new ClosureExpression(new Parameter[0], statement)
        closureExpression.setVariableScope(new VariableScope())
        def expression = new StaticMethodCallExpression(new ClassNode(VariableInterceptor.class),
                "intercept", new ArgumentListExpression(
                new ConstantExpression(methodName),
                new ConstantExpression(variableName),
                closureExpression))
        return expression
    }
}