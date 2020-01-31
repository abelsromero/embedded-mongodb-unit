package org.abelsromero.embedded.mongo;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import java.io.IOException;

public class MongoHandler {

    public static MongodExecutable start (String bindIp, int port, String version) throws IOException {
        final MongodExecutable executable = MongodStarter
            .getDefaultInstance()
            .prepare(buildMongoConfig(bindIp, port, version));
        executable.start();
        return executable;
    }


    private static  IMongodConfig buildMongoConfig(String bindIp, int port, String version) throws IOException {
        return new MongodConfigBuilder()
            .version(Version.valueOf(version))
            .net(new Net(bindIp, port, Network.localhostIsIPv6()))
            .build();
    }
}
