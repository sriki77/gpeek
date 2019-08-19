package org.sriki.gpeek.ast.transformation

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.sriki.gpeek.ast.transformer.VariableInterceptResultTransformer

import static org.sriki.gpeek.InterceptorContext.instance
import static org.sriki.gpeek.intercept.VariableInterceptor.resultFor

@CompileStatic
@GroovyASTTransformation
@Slf4j
class ShowResultAstTransformation implements ASTTransformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptAstTransformation.class)

    @Override
    void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        MethodNode method = (MethodNode) nodes[1]
        def existingStatements = ((BlockStatement) method.code).statements
        def visitor = new VariableInterceptResultTransformer(
                method.name, sourceUnit)
        existingStatements.each { s -> s.visit(visitor) }

        def result = resultFor(method.name)
        if (result != null) {
            existingStatements.add(new ExpressionStatement(
                    new GStringExpression("result=$result")))
        }

        StringWriter writer = new StringWriter()
        AstNodeToScriptVisitor scriptVisitor = new AstNodeToScriptVisitor(writer)
        scriptVisitor.visitMethod(method)
        def methodString = writer.toString()
        LOGGER.info("Show Result Method: {}", methodString)
        instance().addToContext(methodString)
    }
}

