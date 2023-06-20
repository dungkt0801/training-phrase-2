package com.students;

import com.students.eventbus.EventBusSender;
import com.students.eventbus.impl.EventBusSenderImpl;
import com.students.handler.StudentHandler;
import com.students.handler.impl.StudentHandlerImpl;
import com.students.repository.StudentRepository;
import com.students.repository.impl.StudentRepositoryImpl;
import com.students.router.StudentRouter;
import com.students.service.StudentService;
import com.students.service.impl.StudentServiceImpl;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class StudentVerticle extends AbstractVerticle {

  private ServiceDiscovery discovery;

  private Record record;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions clusterOptions = new VertxOptions().setClusterManager(mgr);

    Vertx.clusteredVertx(clusterOptions, res -> {
      if (res.succeeded()) {
        this.vertx = res.result();
        ConfigStoreOptions store = new ConfigStoreOptions().setType("env");
        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(store);
        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        retriever.getConfig(ar -> {
          if (ar.succeeded()) {
            JsonObject configurations = ar.result();
            createServicesAndStartServer(configurations);
            startFuture.complete();
          } else {
            startFuture.fail(ar.cause());
          }
        });
      } else {
        startFuture.fail(res.cause());
      }
    });
  }

  private void createServicesAndStartServer(JsonObject configurations) {
    MongoClient mongoClient = createMongoClient(vertx, configurations);

    StudentRepository studentRepository = new StudentRepositoryImpl(mongoClient);
    EventBusSender eventBusSender = new EventBusSenderImpl(vertx.eventBus());
    StudentService studentService = new StudentServiceImpl(eventBusSender, studentRepository);
    StudentHandler studentHandler = new StudentHandlerImpl(studentService);
    StudentRouter studentRouter = new StudentRouter(vertx, studentHandler);

    vertx.createHttpServer()
      .requestHandler(studentRouter.getRouter())
      .listen(configurations.getInteger("HTTP_PORT", 8080), result -> {
        if (result.succeeded()) {

          // Create a record for this service
          this.record = HttpEndpoint.createRecord("students", "localhost", 8080, "/");

          // Use the Service Discovery to publish the record
          this.discovery = ServiceDiscovery.create(vertx);
          discovery.publish(record, ar -> {
            if (ar.succeeded()) {
              System.out.println("Service published");
            } else {
              System.err.println("Service could not be published");
            }
          });


          System.out.println("HTTP Server listening on port " + result.result().actualPort());
        } else {
          System.err.println("Could not start HTTP server: " + result.cause());
        }
      });
  }

  @Override
  public void stop() throws Exception {
    discovery.unpublish(record.getRegistration(), ar -> {
      if (ar.succeeded()) {
        System.out.println("Service unpublished");
        discovery.close();
      } else {
        System.err.println("Service could not be unpublished");
      }
    });
  }

  private MongoClient createMongoClient(Vertx vertx, JsonObject configurations) {
    JsonObject config = new JsonObject()
      .put("host", "localhost")
      .put("db_name", "demo");

    return MongoClient.createShared(vertx, config);
  }
}
