package com.gateway;

import com.gateway.handler.GatewayHandler;
import com.gateway.handler.impl.GatewayHandlerImpl;
import com.gateway.router.ApiGatewayRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class ApiGatewayVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr);

    Vertx.clusteredVertx(options, vertxAsyncResult -> {
      if (vertxAsyncResult.succeeded()) {

        Vertx vertx = vertxAsyncResult.result();

        WebClient webClient = WebClient.create(
          vertx,
          new WebClientOptions()
            .setDefaultHost("localhost")
            .setDefaultPort(8084)
        );

        GatewayHandler gatewayHandler = new GatewayHandlerImpl(webClient);
        ApiGatewayRouter apiGatewayRouter = new ApiGatewayRouter(vertx, gatewayHandler);

        vertx.createHttpServer()
          .requestHandler(apiGatewayRouter.getRouter())
          .listen(3000, result -> {
            if (result.succeeded()) {

              ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
              discovery.getRecords(record -> record.getName().equals("student-service"), ar -> {
                if(ar.succeeded()) {
                  System.out.println(ar.result());
                  if(!ar.result().isEmpty()) {
                    System.out.println("thay rui ne");
                    System.out.println("co " + ar.result().size() + " thang ne!");
                  } else {
                    System.out.println("khong thay ne");
                  }
                } else {
                  System.out.println("Loi rui ne");
                }
              });

              System.out.println("HTTP Server listening on port " + result.result().actualPort());
              startFuture.complete();
            } else {
              System.err.println("Could not start HTTP server: " + result.cause());
              startFuture.fail(result.cause());
            }
          });
      } else {
        startFuture.fail(vertxAsyncResult.cause());
      }
    });
  }

}
