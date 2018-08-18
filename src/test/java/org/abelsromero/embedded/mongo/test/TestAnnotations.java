package org.abelsromero.embedded.mongo.test;

import lombok.SneakyThrows;
import org.abelsromero.embedded.mongo.EmbeddedMongoDbConfiguration;
import org.abelsromero.embedded.mongo.EmbeddedMongoDbImport;
import org.junit.Test;

import java.lang.annotation.Annotation;

public class TestAnnotations {

    public static final Test test = new Test() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return this.getClass();
        }

        @Override
        public Class<? extends Throwable> expected() {
            return null;
        }

        @Override
        public long timeout() {
            return 0;
        }
    };

    @SuppressWarnings("unused")
    class FakeTestClass {

        @EmbeddedMongoDbConfiguration(skip = true)
        void skip() {
        }

        @EmbeddedMongoDbImport(file = "single_document_data.json")
        void importSingleDocument() {
        }

        @EmbeddedMongoDbImport(file = "multiple_document_data.json")
        void importMultipleDocuments() {
        }

        @EmbeddedMongoDbImport(file = "multiple_document_data_in_array.json")
        void importMultipleDocumentsInArray() {
        }

        @EmbeddedMongoDbImport(file = "multiple_document_data_in_array.json", jsonArray = true)
        void importMultipleDocumentsInArrayTrue() {
        }

        @EmbeddedMongoDbImport(file = "non_existent_file.json")
        void nonExistentFile() {
        }
    }

    @SneakyThrows
    public EmbeddedMongoDbConfiguration skinAnnotation() {
        return FakeTestClass.class.getDeclaredMethod("skip").getAnnotation(EmbeddedMongoDbConfiguration.class);
    }


    public EmbeddedMongoDbImport importSingleJsonAnnotation() {
        return getImportAnnotationFrom("importSingleDocument");
    }


    public EmbeddedMongoDbImport importMultipleJsonAnnotation() {
        return getImportAnnotationFrom("importMultipleDocuments");
    }


    public EmbeddedMongoDbImport importMultipleJsonInArrayAnnotation() {
        return getImportAnnotationFrom("importMultipleDocumentsInArray");
    }


    public EmbeddedMongoDbImport importMultipleJsonInArrayTrueAnnotation() {
        return getImportAnnotationFrom("importMultipleDocumentsInArrayTrue");
    }


    public EmbeddedMongoDbImport nonExistentFileAnnotation() {
        return getImportAnnotationFrom("nonExistentFile");
    }

    @SneakyThrows
    private EmbeddedMongoDbImport getImportAnnotationFrom(String methodName) {
        return FakeTestClass.class.getDeclaredMethod(methodName).getAnnotation(EmbeddedMongoDbImport.class);
    }
}
