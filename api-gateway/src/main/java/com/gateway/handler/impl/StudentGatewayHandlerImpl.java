package com.gateway.handler.impl;

import static com.gateway.constants.Constants.STUDENTS_BASE_PATH;
import static com.gateway.constants.Constants.STUDENTS_SERVICE_ROOT;

import com.common.util.Util;
import com.gateway.handler.StudentGatewayHandler;
import com.gateway.util.ApiGatewayUtil;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StudentGatewayHandlerImpl implements StudentGatewayHandler {


  private final WebClient webClient;

  public void handleStudents(RoutingContext routingContext) {

    String query = routingContext.request().query();
    String url = STUDENTS_SERVICE_ROOT + STUDENTS_BASE_PATH + routingContext.normalisedPath();
    if(query != null) {
      url += "?" + query;
    }

    webClient
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
    webClient
      .postAbs(STUDENTS_SERVICE_ROOT + STUDENTS_BASE_PATH + routingContext.normalisedPath())
      .sendJsonObject(routingContext.getBodyAsJson(), result -> {
        if(result.succeeded()) {
          ApiGatewayUtil.onClientSuccessResponse(routingContext, 200, result.result().bodyAsString());
        } else {
          Util.onErrorResponse(routingContext, 500, result.cause());
        }
      });
  }

  public void handleStudentsPut(RoutingContext routingContext) {
    webClient
      .putAbs(STUDENTS_SERVICE_ROOT + STUDENTS_BASE_PATH + routingContext.normalisedPath())
      .sendJsonObject(routingContext.getBodyAsJson(), result -> {
        if(result.succeeded()) {
          ApiGatewayUtil.onClientSuccessResponse(routingContext, 200, result.result().bodyAsString());
        } else {
          Util.onErrorResponse(routingContext, 500, result.cause());
        }
      });
  }

  public void handleStudentsDelete(RoutingContext routingContext) {
    webClient
      .deleteAbs(STUDENTS_SERVICE_ROOT + STUDENTS_BASE_PATH + routingContext.normalisedPath())
      .send(result -> {
        if(result.succeeded()) {
          ApiGatewayUtil.onClientSuccessResponse(routingContext, 200, result.result().bodyAsString());
        } else {
          Util.onErrorResponse(routingContext, 500, result.cause());
        }
      });
  }

}
