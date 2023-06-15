package com.gateway.router;

import static com.gateway.constants.Constants.CLASSES_SERVICE_ROUTE_PATTERN;
import static com.gateway.constants.Constants.STUDENTS_SERVICE_ROUTE_PATTERN;

import com.gateway.handler.GatewayHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiGatewayRouter {

  private final Vertx vertx;

  private final GatewayHandler gatewayHandler;

  public Router getRouter() {
    Router router = Router.router(vertx);

    registerHandler(router, gatewayHandler, STUDENTS_SERVICE_ROUTE_PATTERN);
    registerHandler(router, gatewayHandler, CLASSES_SERVICE_ROUTE_PATTERN);

    return router;
  }

  private void registerHandler(Router router, GatewayHandler gatewayHandler, String routePattern) {
    router.route(routePattern).handler(BodyHandler.create());
    router.get(routePattern).handler(gatewayHandler::handleGet);
    router.post(routePattern).handler(gatewayHandler::handlePost);
    router.put(routePattern).handler(gatewayHandler::handlePut);
    router.delete(routePattern).handler(gatewayHandler::handleDelete);
  }

}
