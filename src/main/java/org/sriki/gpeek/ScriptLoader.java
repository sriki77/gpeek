package org.sriki.gpeek;

import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.sriki.gpeek.intercept.ShowResult;
import org.sriki.gpeek.intercept.Intercept;

@Slf4j
class ScriptLoader {

    private GroovyClassLoader classLoader;
    private MutableImportCustomizer mutableImportCustomizer
            = new MutableImportCustomizer();


    ScriptLoader() {
        ImportCustomizer importCustomizer = buildImportCustomizer();
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.setTargetBytecode(CompilerConfiguration.JDK8);
        compilerConfiguration.addCompilationCustomizers(importCustomizer, mutableImportCustomizer);

        classLoader = new GroovyClassLoader(
                this.getClass().getClassLoader(), compilerConfiguration);

    }

    private ImportCustomizer buildImportCustomizer() {
        ImportCustomizer customizer = new ImportCustomizer();
        customizer.addImports(Intercept.class.getName());
        customizer.addImports(ShowResult.class.getName());
        customizer.addStarImports("java.time");
        customizer.addStarImports("java.time.temporal");
        customizer.addStarImports("groovy.time");
        customizer.addStarImports("groovy.transform");
        return customizer;
    }

    void reset() {
        mutableImportCustomizer.reset();
        classLoader.clearCache();
    }

    Class parseClass(String script, String fileName) {
//        LOGGER.info("Parsing File: {} Class: \n{}", fileName, script);
        final Class parsedClass = classLoader.parseClass(script, fileName);
        mutableImportCustomizer.addImports(parsedClass.getName());
        return parsedClass;
    }

}
