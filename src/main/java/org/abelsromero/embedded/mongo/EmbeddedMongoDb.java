package org.abelsromero.embedded.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongoImportExecutable;
import de.flapdoodle.embed.mongo.MongoImportProcess;
import de.flapdoodle.embed.mongo.MongoImportStarter;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.config.IMongoImportConfig;
import de.flapdoodle.embed.mongo.config.MongoImportConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.abelsromero.embedded.mongo.AnnotationHelper.*;

public class EmbeddedMongoDb extends TestWatcher {

    private static final String BIND_IP = "127.0.0.1";

    private MongodExecutable executable;

    private MongoDatabase db;
    private String collectionName;
    private String databaseName;

    @SneakyThrows
    @Override
    protected void starting(Description description) {

        final EmbeddedMongoDbConfiguration mongoConfiguration =
            description.getAnnotation(EmbeddedMongoDbConfiguration.class);

        int port = port(mongoConfiguration);
        final String version = version(mongoConfiguration);
        if (mongoConfiguration != null && !mongoConfiguration.skip()) {
            executable = MongoHandler.start(BIND_IP, port, version);
        }

        databaseName = databaseName(mongoConfiguration);
        collectionName = collectionName(mongoConfiguration);

        if (mongoConfiguration != null) {
            db = new MongoClient("localhost", port)
                .getDatabase(mongoConfiguration.database());
            db.createCollection(collectionName);
        }


        try {
            final EmbeddedMongoDbImport mongoImport = getImportAnnotations(description);

            if (mongoImport != null && mongoImport.files().length > 0) {

                final List<String> filesToImport = Arrays.stream(mongoImport.files())
                    .filter(StringUtils::isNotBlank)
                    .map(this::absolutePath)
                    .collect(Collectors.toList());

                if (filesToImport.size() > 0) {
                    final Path tempFile = mergeFilesToImport(filesToImport);

                    startMongoImport(BIND_IP, port, version,
                        databaseName, collectionName,
                        tempFile.toFile().getAbsolutePath(),
                        mongoImport == null ? getDefaultBooleanValue(EmbeddedMongoDbImport.class, "jsonArray") : mongoImport.jsonArray()
                    );
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private Path mergeFilesToImport(List<String> filesToImport) throws IOException {
        final Path tempFile = Files.createTempFile("org.abelsromero.embedded.mongo.", ".temp");
        final OutputStream tempOs = Files.newOutputStream(tempFile);
        for (String file : filesToImport) {
            IOUtils.copy(new FileInputStream(file), tempOs);
        }
        return tempFile;
    }

    private EmbeddedMongoDbImport getImportAnnotations(Description description) {
        return description.getAnnotation(EmbeddedMongoDbImport.class);
    }


    @SneakyThrows
    private MongoImportProcess startMongoImport(String bindIp, int port,
                                                String version,
                                                String dbName, String collection,
                                                String jsonFile, boolean jsonArray) {

        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("-------------------------------------------------------------------------------");
        final IMongoImportConfig mongoImportConfig = new MongoImportConfigBuilder()
            .version(Version.valueOf(version))
            .net(new Net(bindIp, port, Network.localhostIsIPv6()))
            .db(dbName)
            .collection(collection)
            .upsert(false)
            .dropCollection(true)
            .jsonArray(jsonArray)
            .importFile(jsonFile)
            .build();

        final MongoImportExecutable mongoImportExecutable = MongoImportStarter.getDefaultInstance().prepare(mongoImportConfig);
        return mongoImportExecutable.start();
    }

    @Override
    protected void finished(Description description) {
        if (executable != null) {
            executable.stop();
        }
    }

    @SneakyThrows
    private String collectionName(EmbeddedMongoDbConfiguration mongoConfiguration) {
        return mongoConfiguration != null ?
            mongoConfiguration.collection() :
            getDefaultStringValue(EmbeddedMongoDbConfiguration.class, "collection");
    }

    @SneakyThrows
    private String databaseName(EmbeddedMongoDbConfiguration mongoConfiguration) {
        return mongoConfiguration != null ?
            mongoConfiguration.database() :
            getDefaultStringValue(EmbeddedMongoDbConfiguration.class, "database");
    }

    @SneakyThrows
    private int port(EmbeddedMongoDbConfiguration mongoConfiguration) {
        return mongoConfiguration != null ?
            mongoConfiguration.port() :
            getDefaultIntValue(EmbeddedMongoDbConfiguration.class, "port").intValue();
    }

    @SneakyThrows
    private String version(EmbeddedMongoDbConfiguration mongoConfiguration) {
        return mongoConfiguration != null ?
            mongoConfiguration.version() :
            getDefaultStringValue(EmbeddedMongoDbConfiguration.class, "version");
    }

    @SneakyThrows
    private String absolutePath(final String jsonFile) {
        final URL resource = this.getClass().getClassLoader().getResource(jsonFile);
        if (resource == null)
            throw new FileNotFoundException("Could not find file: " + jsonFile);
        else
            return new File(resource.toURI()).getAbsolutePath();
    }

}
