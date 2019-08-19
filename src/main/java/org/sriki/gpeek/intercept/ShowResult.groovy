package org.sriki.gpeek.intercept

import org.codehaus.groovy.transform.GroovyASTTransformationClass
import org.sriki.gpeek.ast.transformation.ShowResultAstTransformation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass(classes = [ShowResultAstTransformation.class])
@interface ShowResult {
}