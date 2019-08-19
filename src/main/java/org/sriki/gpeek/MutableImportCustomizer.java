package org.sriki.gpeek;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MutableImportCustomizer extends CompilationCustomizer {

    private final List<String> imports = Collections.synchronizedList(new ArrayList<>());

    MutableImportCustomizer() {
        super(CompilePhase.CONVERSION);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode)
            throws CompilationFailedException {
        final ModuleNode ast = source.getAST();
        for (String imp : imports) {
            final ClassNode node = ClassHelper.make(imp);
            ast.addImport(node.getNameWithoutPackage(), node);
        }
    }

    MutableImportCustomizer addImports(final String... imports) {
        Collections.addAll(this.imports, imports);
        return this;
    }

    MutableImportCustomizer reset() {
        if (!imports.isEmpty()) {
            imports.clear();
        }
        return this;
    }
}
