package org.abelsromero.embedded.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.*;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import lombok.SneakyThrows;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static org.abelsromero.embedded.mongo.AnnotationHelper.*;

public class EmbeddedMongoDb extends TestWatcher {

    private static final String BIND_IP = "127.0.0.1";
    private static final Version.Main VERSION = Version.Main.V3_5;

    private MongodExecutable executable;

    private MongoDatabase db;
    private String collectionName;
    private String databaseName;

    @SneakyThrows
    @Override
    protected void starting(Description description) {

        final EmbeddedMongoDbConfiguration mongoConfiguration =
            description.getAnnotation(EmbeddedMongoDbConfiguration.class);

        if (mongoConfiguration != null && mongoConfiguration.skip()) {
            return;
        }
        // Start MongoDB
        int port = port(mongoConfiguration);
        executable = MongodStarter
            .getDefaultInstance()
            .prepare(buildMongoConfig(port));
        executable.start();

        databaseName = databaseName(mongoConfiguration);
        collectionName = collectionName(mongoConfiguration);

        if (mongoConfiguration != null) {
            db = new MongoClient("localhost", port)
                .getDatabase(mongoConfiguration.database());
            db.createCollection(collectionName);
        }

        final EmbeddedMongoDbImport mongoImport =
            description.getAnnotation(EmbeddedMongoDbImport.class);

        if (mongoImport != null && mongoImport.file().length() > 0) {
            startMongoImport(BIND_IP, port, databaseName, collectionName,
                mongoImport.file(),
                mongoImport == null ? getDefaultBooleanValue(EmbeddedMongoDbImport.class, "jsonArray") : mongoImport.jsonArray()
            );
        }
    }

    @SneakyThrows
    private MongoImportProcess startMongoImport(String bindIp, int port,
                                                String dbName, String collection,
                                                String jsonFile, boolean jsonArray) {

        final IMongoImportConfig mongoImportConfig = new MongoImportConfigBuilder()
            .version(VERSION)
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
            mongoConfiguration.collection() :
            getDefaultStringValue(EmbeddedMongoDbConfiguration.class, "database");
    }

    @SneakyThrows
    private int port(EmbeddedMongoDbConfiguration mongoConfiguration) {
        return mongoConfiguration != null ?
            mongoConfiguration.port() :
            getDefaultIntValue(EmbeddedMongoDbConfiguration.class, "port").intValue();
    }

    @SneakyThrows
    private String absolutePath(final String jsonFile) {
        final URL resource = this.getClass().getClassLoader().getResource(jsonFile);
        if (resource == null)
            throw new FileNotFoundException("Could not find file: " + jsonFile);
        else
            return new File(resource.toURI()).getAbsolutePath();
    }

    private IMongodConfig buildMongoConfig(int port) throws IOException {
        return new MongodConfigBuilder()
            .version(VERSION)
            .net(new Net(BIND_IP, port, Network.localhostIsIPv6()))
            .build();
    }

}
