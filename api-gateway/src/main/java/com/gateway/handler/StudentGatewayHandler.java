package com.gateway.handler;

import io.vertx.ext.web.RoutingContext;

public interface StudentGatewayHandler {

  void handleStudents(RoutingContext routingContext);

  void handleStudentsPost(RoutingContext routingContext);

  void handleStudentsPut(RoutingContext routingContext);

  void handleStudentsDelete(RoutingContext routingContext);

}
