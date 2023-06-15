package com.gateway.router;

import static com.gateway.constants.Constants.CLASSES_SERVICE_ROUTE_PATTERN;
import static com.gateway.constants.Constants.STUDENTS_SERVICE_ROUTE_PATTERN;

import com.gateway.handler.ClassGatewayHandler;
import com.gateway.handler.StudentGatewayHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiGatewayRouter {

  private final Vertx vertx;

  private final StudentGatewayHandler studentGatewayHandler;

  private final ClassGatewayHandler classGatewayHandler;

  public Router getRouter() {

    Router router = Router.router(vertx);

    router.route(STUDENTS_SERVICE_ROUTE_PATTERN).handler(BodyHandler.create());
    router.get(STUDENTS_SERVICE_ROUTE_PATTERN).handler(studentGatewayHandler::handleStudents);
    router.post(STUDENTS_SERVICE_ROUTE_PATTERN).handler(studentGatewayHandler::handleStudentsPost);
    router.put(STUDENTS_SERVICE_ROUTE_PATTERN).handler(studentGatewayHandler::handleStudentsPut);
    router.delete(STUDENTS_SERVICE_ROUTE_PATTERN).handler(studentGatewayHandler::handleStudentsDelete);

    router.route(CLASSES_SERVICE_ROUTE_PATTERN).handler(BodyHandler.create());
    router.get(CLASSES_SERVICE_ROUTE_PATTERN).handler(classGatewayHandler::handleClasses);
    router.post(CLASSES_SERVICE_ROUTE_PATTERN).handler(classGatewayHandler::handleClassesPost);
    router.put(CLASSES_SERVICE_ROUTE_PATTERN).handler(classGatewayHandler::handleClassesPut);
    router.delete(CLASSES_SERVICE_ROUTE_PATTERN).handler(classGatewayHandler::handleClassesDelete);

    return router;
  }

}
