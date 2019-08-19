package org.sriki.gpeek;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sriki.gpeek.intercept.VariableInterceptor;
import org.sriki.gpeek.testData.PaymentCalculator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.sriki.gpeek.intercept.VariableInterceptor.resultFor;
import static org.sriki.gpeek.intercept.VariableInterceptor.valueFor;

class ScriptLoaderTest extends AbstractBaseTestcase {

    private ScriptLoader compiler;

    @BeforeEach
    void setUp() {
        compiler = new ScriptLoader();
    }

    @Test
    void shouldDebugCompileGroovyScript() throws Exception {
        String fileName = "IndividualPaymentCalculator.groovy";
        Class clazz = compiler.parseClass(getFile(fileName), fileName);
        final PaymentCalculator calculator = (PaymentCalculator) clazz.getDeclaredConstructor().newInstance();
        assertThat(calculator.payment(), is(new BigDecimal("3000.0")));
        clazz.getDeclaredMethod("paymentSet").invoke(calculator);
        clazz.getDeclaredMethod("paymentMarch").invoke(calculator);

        System.err.println(VariableInterceptor.getMethodDataMap());
        assertThat(valueFor("paymentMarch", "_p_schoolFees"), is(new BigDecimal("150000.0")));
        assertThat(valueFor("paymentMarch", "_p_propertyTax"), is(new BigDecimal("30000.0")));
        assertThat(valueFor("paymentMarch", "_m_isMarch"), is(false));
        assertThat(valueFor("paymentSet", "march"), is(false));
        assertThat(resultFor("payment"), is(new BigDecimal("3000.0")));
        assertThat(resultFor("paymentSet"), is(new BigDecimal("3000.0")));

        final InterceptorContext interceptorContext = InterceptorContext.instance();
        try (interceptorContext) {
            fileName = "IndividualPaymentCalculator.groovy";
            compiler.parseClass(getFileWithResult(fileName), fileName);
            FileUtils.writeLines(new File(tempDir,
                    "IndividualPaymentCalculator_Debug.groovy"), interceptorContext.context());
        }
    }

    private String getFile(String fileName) throws IOException {
        return IOUtils.resourceToString("/" + fileName, Charset.defaultCharset());
    }

    private String getFileWithResult(String fileName) throws IOException {
        final String fileData = IOUtils.resourceToString("/" + fileName, Charset.defaultCharset());
        return fileData.replaceAll("@Intercept", "@ShowResult");
    }

}