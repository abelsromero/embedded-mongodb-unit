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

        @EmbeddedMongoDbConfiguration
        void defaultConfiguration() {
        }

        @EmbeddedMongoDbConfiguration(skip = true)
        void skip() {
        }

        @EmbeddedMongoDbImport(files = "single_document_data.json")
        void importSingleDocument() {
        }

        @EmbeddedMongoDbImport(files = {"single_document_data.json", "multiple_document_data.json"})
        void importMultipleJsons() {
        }

        @EmbeddedMongoDbImport(files = "multiple_document_data.json")
        void importMultipleDocuments() {
        }

        @EmbeddedMongoDbImport(files = "multiple_document_data_in_array.json")
        void importMultipleDocumentsInArray() {
        }

        @EmbeddedMongoDbImport(files = "multiple_document_data_in_array.json", jsonArray = true)
        void importMultipleDocumentsInArrayTrue() {
        }

        @EmbeddedMongoDbImport(files = "non_existent_file.json")
        void nonExistentFile() {
        }

        @EmbeddedMongoDbConfiguration(port = 10259)
        void nonDefaultPort() {
        }

        @EmbeddedMongoDbConfiguration(version = "V3_4_5")
        void nonDefaultVersion() {
        }
    }

    @SneakyThrows
    public EmbeddedMongoDbConfiguration defaultEmbeddedConfiguration() {
        return getConfigurationAnnotationFrom("defaultConfiguration");
    }

    @SneakyThrows
    public EmbeddedMongoDbConfiguration skipAnnotation() {
        return getConfigurationAnnotationFrom("skip");
    }

    public EmbeddedMongoDbConfiguration nonDefaultPortAnnotation() {
        return getConfigurationAnnotationFrom("nonDefaultPort");
    }

    public EmbeddedMongoDbConfiguration nonDefaultVersionAnnotation() {
        return getConfigurationAnnotationFrom("nonDefaultVersion");
    }

    public EmbeddedMongoDbImport importSingleJsonAnnotation() {
        return getImportAnnotationFrom("importSingleDocument");
    }

    public EmbeddedMongoDbImport importMultipleJsonAnnotation() {
        return getImportAnnotationFrom("importMultipleDocuments");
    }

    public EmbeddedMongoDbImport importMultipleJsonsAnnotations() {
        return getImportAnnotationFrom("importMultipleJsons");
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
    private EmbeddedMongoDbConfiguration getConfigurationAnnotationFrom(String methodName) {
        return FakeTestClass.class.getDeclaredMethod(methodName).getAnnotation(EmbeddedMongoDbConfiguration.class);
    }

    @SneakyThrows
    private EmbeddedMongoDbImport getImportAnnotationFrom(String methodName) {
        return FakeTestClass.class.getDeclaredMethod(methodName).getAnnotation(EmbeddedMongoDbImport.class);
    }

}
