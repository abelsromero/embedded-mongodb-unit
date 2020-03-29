package org.abelsromero.embedded.mongo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows importing data from a json file.
 * Check https://docs.mongodb.com/manual/reference/program/mongoimport/ for information on the options.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface EmbeddedMongoDbImport {

    String[] files();

    boolean jsonArray() default false;

}
