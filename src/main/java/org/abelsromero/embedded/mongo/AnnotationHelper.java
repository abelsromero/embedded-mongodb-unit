package org.abelsromero.embedded.mongo;

import lombok.SneakyThrows;

class AnnotationHelper {

    @SneakyThrows
    public static String getDefaultStringValue(final Class<?> annotation, final String methodName) {
        return (String) annotation
            .getDeclaredMethod(methodName)
            .getDefaultValue();
    }

    @SneakyThrows
    public static Boolean getDefaultBooleanValue(final Class<?> annotation, final String methodName) {
        return (Boolean) annotation
            .getDeclaredMethod(methodName)
            .getDefaultValue();
    }

}
