package com.gateway;

import com.gateway.handler.ClassGatewayHandler;
import com.gateway.handler.StudentGatewayHandler;
import com.gateway.handler.impl.ClassGateWayHandlerImpl;
import com.gateway.handler.impl.StudentGatewayHandlerImpl;
import com.gateway.router.ApiGatewayRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClient;

public class ApiGatewayVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    WebClient webClient = WebClient.create(vertx);
    StudentGatewayHandler studentGatewayHandler = new StudentGatewayHandlerImpl(webClient);
    ClassGatewayHandler classGatewayHandler = new ClassGateWayHandlerImpl(webClient);
    ApiGatewayRouter apiGatewayRouter = new ApiGatewayRouter(vertx, studentGatewayHandler, classGatewayHandler);

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
  }

}
