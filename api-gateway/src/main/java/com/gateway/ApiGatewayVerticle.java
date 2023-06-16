package com.gateway;

import com.gateway.handler.GatewayHandler;
import com.gateway.handler.impl.GatewayHandlerImpl;
import com.gateway.loadbalancer.LoadBalancer;
import com.gateway.router.ApiGatewayRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import java.util.HashMap;
import java.util.Map;

public class ApiGatewayVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr);

    Vertx.clusteredVertx(options, vertxAsyncResult -> {
      if (vertxAsyncResult.succeeded()) {
        Vertx vertx = vertxAsyncResult.result();

        ServiceDiscovery discovery = ServiceDiscovery.create(vertx);

        // create load balancer for each service
        Map<String, LoadBalancer> loadBalancers = new HashMap<>();
        loadBalancers.put("students-service", new LoadBalancer(discovery, vertx, "students-service"));
        loadBalancers.put("classes-service", new LoadBalancer(discovery, vertx, "classes-service"));

        GatewayHandler gatewayHandler = new GatewayHandlerImpl(loadBalancers);
        ApiGatewayRouter apiGatewayRouter = new ApiGatewayRouter(vertx, gatewayHandler);

        vertx.createHttpServer()
          .requestHandler(apiGatewayRouter.getRouter())
          .listen(3000, result -> {
            if (result.succeeded()) {
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
