package com.ericsson.gic.tms.tvs.infrastructure;

import com.google.common.collect.Iterables;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.google.common.io.Resources.*;
import static java.nio.charset.StandardCharsets.*;

@Component
public class MongoFixtures {

    private final Logger logger = LoggerFactory.getLogger(MongoFixtures.class);

    @Autowired
    private Mongo mongo;

    @Autowired
    private Jongo jongo;

    @Value("${spring.data.mongodb.database}")
    protected String dbName;

    public DB getDatabase() {
        return mongo.getDB(dbName);
    }

    public synchronized void dropDatabase() {
        jongo.getDatabase().dropDatabase();
    }

    public void dropCollection(String collection) {
        jongo.getCollection(collection).drop();
    }

    private void createCollections() throws Exception {
        // Create mock data
        DB db = getDatabase();
        createCollection(db, "testSuiteResult", "mongo/data/test/testSuiteResult.json");
        createCollection(db, "testSession", "mongo/data/test/testSession.json");
        createCollection(db, "testCaseResult", "mongo/data/test/testCaseResult.json");
        createCollection(db, "job", "mongo/data/test/job.json");
        createCollection(db, "collectionMetadata", "mongo/data/test/collectionMetadata.json");
        createCollection(db, "projectRequirement", "mongo/data/test/projectRequirement.json");
    }

    public synchronized void resetCollections() throws Exception {
        logger.info("Resetting Mongo collections from classpath fixtures");
        dropDatabase();
        createCollections();
    }

    private DBCollection createCollection(DB db, String collectionName, String resourceJson)
            throws IOException, URISyntaxException {

        DBObject[] testware = getDbObjects(resourceJson);
        db.getCollection(collectionName).insert(testware);
        return db.getCollection(collectionName);
    }

    private static DBObject[] getDbObjects(String resourceName) throws IOException, URISyntaxException {
        String file = String.join("\n", readLines(getResource(resourceName), UTF_8));

        @SuppressWarnings("unchecked")
        List<DBObject> parsed = (List<DBObject>) JSON.parse(file);
        return Iterables.toArray(parsed, DBObject.class);
    }

}
