package com.gateway.router;

import static com.gateway.constants.Constants.CLASSES_SERVICE_ROUTE_PATTERN;
import static com.gateway.constants.Constants.STUDENTS_SERVICE_ROUTE_PATTERN;

import com.gateway.handler.ClassGatewayHandler;
import com.gateway.handler.StudentGatewayHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiGatewayRouter {

  private final Vertx vertx;

  private final StudentGatewayHandler studentGatewayHandler;

  private final ClassGatewayHandler classGatewayHandler;

  public Router getRouter() {

    Router router = Router.router(vertx);

    router.route(STUDENTS_SERVICE_ROUTE_PATTERN).method(HttpMethod.GET).handler(studentGatewayHandler::handleStudents);
    router.route(STUDENTS_SERVICE_ROUTE_PATTERN).method(HttpMethod.POST).handler(studentGatewayHandler::handleStudentsPost);
    router.route(STUDENTS_SERVICE_ROUTE_PATTERN).method(HttpMethod.PUT).handler(studentGatewayHandler::handleStudentsPut);
    router.route(STUDENTS_SERVICE_ROUTE_PATTERN).method(HttpMethod.DELETE).handler(studentGatewayHandler::handleStudentsDelete);

    router.route(CLASSES_SERVICE_ROUTE_PATTERN).method(HttpMethod.GET).handler(classGatewayHandler::handleClasses);
    router.route(CLASSES_SERVICE_ROUTE_PATTERN).method(HttpMethod.POST).handler(classGatewayHandler::handleClassesPost);
    router.route(CLASSES_SERVICE_ROUTE_PATTERN).method(HttpMethod.PUT).handler(classGatewayHandler::handleClassesPut);
    router.route(CLASSES_SERVICE_ROUTE_PATTERN).method(HttpMethod.DELETE).handler(classGatewayHandler::handleClassesDelete);

    return router;
  }

}
