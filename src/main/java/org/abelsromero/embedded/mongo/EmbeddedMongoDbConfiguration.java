package org.abelsromero.embedded.mongo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface EmbeddedMongoDbConfiguration {

    /**
     * Should not start an embedded MongoDB process.
     */
    boolean skip() default false;

    String database() default "local";

    String collection() default "embedded-test-collection";

    int port() default 27017;

}
