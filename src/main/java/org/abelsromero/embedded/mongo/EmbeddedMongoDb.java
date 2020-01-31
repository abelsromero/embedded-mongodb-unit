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
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

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

        final EmbeddedMongoDbImport mongoImport =
            description.getAnnotation(EmbeddedMongoDbImport.class);

        try {
            if (mongoImport != null && mongoImport.file().length() > 0) {
                startMongoImport(BIND_IP, port, version,
                    databaseName, collectionName,
                    mongoImport.file(),
                    mongoImport == null ? getDefaultBooleanValue(EmbeddedMongoDbImport.class, "jsonArray") : mongoImport.jsonArray()
                );
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @SneakyThrows
    private MongoImportProcess startMongoImport(String bindIp, int port,
                                                String version,
                                                String dbName, String collection,
                                                String jsonFile, boolean jsonArray) {

        final IMongoImportConfig mongoImportConfig = new MongoImportConfigBuilder()
            .version(Version.valueOf(version))
            .net(new Net(bindIp, port, Network.localhostIsIPv6()))
            .db(dbName)
            .collection(collection)
            .upsert(false)
            .dropCollection(true)
            .jsonArray(jsonArray)
            .importFile(absolutePath(jsonFile))
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
