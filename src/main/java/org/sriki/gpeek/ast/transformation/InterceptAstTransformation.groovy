package org.sriki.gpeek.ast.transformation

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.classgen.VariableScopeVisitor
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.sriki.gpeek.ast.transformer.VariableInterceptTransformer

import static org.sriki.gpeek.intercept.VariableInterceptor.buildInterceptCall

@CompileStatic
@GroovyASTTransformation
class InterceptAstTransformation implements ASTTransformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptAstTransformation.class)

    @Override
    void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        MethodNode method = (MethodNode) nodes[1]
        def statement = (BlockStatement) method.code
        def existingStatements = statement.statements
        def methodName = method.name
        def visitor = new VariableInterceptTransformer(methodName, sourceUnit)
        existingStatements.each { s -> s.visit(visitor) }

        StringWriter writer = new StringWriter()
        AstNodeToScriptVisitor scriptVisitor = new AstNodeToScriptVisitor(writer)
        scriptVisitor.visitMethod(method)

        def interceptCall = buildInterceptCall(statement, methodName, "${methodName}_result")
        def body = new BlockStatement()
        body.addStatement(new ExpressionStatement(interceptCall))
        method.code = body

        VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(sourceUnit)
        sourceUnit.AST.classes.each {
            scopeVisitor.visitClass(it)
        }
        LOGGER.info("Mangled Method: {}", writer.toString())
    }
}


