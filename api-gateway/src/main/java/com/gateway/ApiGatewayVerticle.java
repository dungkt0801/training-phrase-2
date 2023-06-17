package com.gateway;

import com.gateway.handler.GatewayHandler;
import com.gateway.handler.impl.GatewayHandlerImpl;
import com.gateway.loadbalancer.LoadBalancer;
import com.gateway.router.ApiGatewayRouter;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import java.util.concurrent.ConcurrentHashMap;

public class ApiGatewayVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    ClusterManager mgr = new HazelcastClusterManager();
    VertxOptions options = new VertxOptions().setClusterManager(mgr);

    Vertx.clusteredVertx(options, vertxAsyncResult -> {
      if (vertxAsyncResult.succeeded()) {
        Vertx vertx = vertxAsyncResult.result();
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
        CircuitBreaker circuitBreaker = CircuitBreaker.create("circuit-breaker", vertx,
          new CircuitBreakerOptions()
            .setMaxFailures(1)// number of failure before opening the circuit
            .setTimeout(50000) // consider a failure if the operation does not succeed in time
            .setFallbackOnFailure(true) // do we call the fallback on failure
            .setResetTimeout(240000) // time spent in open state before attempting to re-try
        );
        LoadBalancer loadBalancer = new LoadBalancer(vertx, discovery, new ConcurrentHashMap<>());
        GatewayHandler gatewayHandler = new GatewayHandlerImpl(loadBalancer);
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
