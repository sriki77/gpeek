package org.sriki.gpeek

final class InterceptorContext implements Closeable {

    private static final ME = new InterceptorContext()
    private final ThreadLocal<List<String>> contextThreadLocal = new ThreadLocal<>()

    private InterceptorContext() {
    }

    static InterceptorContext instance() { ME }

    boolean hasContext() { contextThreadLocal.get() != null }

    void addToContext(String data) {
        if (!hasContext()) {
            contextThreadLocal.set(Collections.synchronizedList(new LinkedList<String>()))
        }
        contextThreadLocal.get().add(data)
    }

    List<String> context() { contextThreadLocal.get() }

    private void clearContext() { contextThreadLocal.remove() }

    @Override
    void close() { clearContext() }
}
