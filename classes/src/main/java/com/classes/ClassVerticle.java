package com.classes;

import com.classes.eventbus.EventBusConsumer;
import com.classes.handler.ClassHandler;
import com.classes.handler.impl.ClassHandlerImpl;
import com.classes.repository.ClassRepository;
import com.classes.repository.impl.ClassRepositoryImpl;
import com.classes.router.ClassRouter;
import com.classes.service.ClassService;
import com.classes.service.impl.ClassServiceImpl;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class ClassVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions clusterOptions = new VertxOptions().setClusterManager(mgr);

    io.vertx.core.Vertx.clusteredVertx(clusterOptions, res -> {
      if (res.succeeded()) {
        this.vertx = res.result();
        ConfigStoreOptions store = new ConfigStoreOptions().setType("env");
        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(store);
        io.vertx.config.ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
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

    final ClassRepository classRepository = new ClassRepositoryImpl(mongoClient);
    final ClassService classService = new ClassServiceImpl(classRepository);
    final ClassHandler classHandler = new ClassHandlerImpl(classService);
    final ClassRouter classRouter = new ClassRouter(vertx, classHandler);
    final EventBusConsumer consumerRegistrar = new EventBusConsumer(vertx.eventBus(), classService);

    vertx.createHttpServer()
      .requestHandler(classRouter.getRouter())
      .listen(configurations.getInteger("HTTP_PORT", 8081), server -> {
        System.out.println("HTTP Server listening on port " + server.result().actualPort());
        consumerRegistrar.registerConsumers()
          .subscribe(
            () -> System.out.println("Successfully registered consumers"),
            error -> System.out.println("Failed to register consumers: " + error.getMessage())
          );
      });
  }

  private MongoClient createMongoClient(Vertx vertx, JsonObject configurations) {
    JsonObject config = new JsonObject()
      .put("host", "localhost")
      .put("db_name", "demo");

    return MongoClient.createShared(vertx, config);
  }
}
