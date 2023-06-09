package com.students;

import com.students.eventbus.EventBusSender;
import com.students.handler.StudentHandler;
import com.students.handler.impl.StudentHandlerImpl;
import com.students.repository.StudentRepository;
import com.students.repository.impl.StudentRepositoryImpl;
import com.students.router.StudentRouter;
import com.students.service.StudentService;
import com.students.service.impl.StudentServiceImpl;
import io.reactivex.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import io.vertx.reactivex.config.ConfigRetriever;

public class StudentVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions clusterOptions = new VertxOptions().setClusterManager(mgr);

    Single<Vertx> vertxSingle = Vertx.rxClusteredVertx(clusterOptions);

    vertxSingle
      .flatMap(vertx -> {
        this.vertx = vertx;
        ConfigStoreOptions store = new ConfigStoreOptions().setType("env");
        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(store);
        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        return retriever.rxGetConfig();
      })
      .flatMap(this::createServicesAndStartServer)
      .subscribe(
        result -> startFuture.complete(),
        error -> startFuture.fail(error)
      );
  }

  private Single<HttpServer> createServicesAndStartServer(JsonObject configurations) {
    MongoClient mongoClient = createMongoClient(vertx, configurations);

    StudentRepository studentRepository = new StudentRepositoryImpl(mongoClient);
    EventBusSender eventBusSender = new EventBusSender(vertx.eventBus());
    StudentService studentService = new StudentServiceImpl(eventBusSender, studentRepository);
    StudentHandler studentHandler = new StudentHandlerImpl(studentService);
    StudentRouter studentRouter = new StudentRouter(vertx, studentHandler);

    return vertx
      .createHttpServer()
      .requestHandler(studentRouter.getRouter())
      .rxListen(configurations.getInteger("HTTP_PORT", 8080))
      .doOnSuccess(server -> System.out.println("HTTP Server listening on port " + server.actualPort()));
  }

  private MongoClient createMongoClient(Vertx vertx, JsonObject configurations) {
    JsonObject config = new JsonObject()
      .put("host", "localhost")
      .put("db_name", "demo");

    return MongoClient.createShared(vertx, config);
  }
}
