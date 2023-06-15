package com.gateway.handler.impl;

import static com.gateway.constants.Constants.STUDENTS_BASE_PATH;
import static com.gateway.constants.Constants.STUDENTS_SERVICE_URL;

import com.common.util.Util;
import com.gateway.handler.StudentGatewayHandler;
import com.gateway.util.ApiGatewayUtil;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StudentGatewayHandlerImpl implements StudentGatewayHandler {

  private final Vertx vertx;

  public void handleStudents(RoutingContext routingContext) {

    String query = routingContext.request().query();
    String url = STUDENTS_SERVICE_URL + STUDENTS_BASE_PATH + routingContext.normalisedPath();
    if(query != null) {
      url += "?" + query;
    }

    WebClient.create(vertx)
      .getAbs(url)
      .send(result -> {
        if(result.succeeded()) {
          ApiGatewayUtil.onClientSuccessResponse(routingContext, 200, result.result().bodyAsString());
        } else {
          Util.onErrorResponse(routingContext, 500, result.cause());
        }
      });
  }

  public void handleStudentsPost(RoutingContext routingContext) {
    // Code to handle POST request for students
  }

  public void handleStudentsPut(RoutingContext routingContext) {
    // Code to handle PUT request for students
  }

  public void handleStudentsDelete(RoutingContext routingContext) {
    // Code to handle DELETE request for students
  }

}
