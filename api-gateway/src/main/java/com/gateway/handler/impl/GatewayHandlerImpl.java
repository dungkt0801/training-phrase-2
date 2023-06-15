package com.gateway.handler.impl;

import static com.gateway.constants.Constants.API_VERSION;

import com.common.util.Util;
import com.gateway.handler.GatewayHandler;
import com.gateway.util.ApiGatewayUtil;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GatewayHandlerImpl implements GatewayHandler {

  private final WebClient webClient;

  @Override
  public void handleGet(RoutingContext routingContext) {
    String query = routingContext.request().query();
    String url =  API_VERSION + routingContext.normalisedPath();
    if(query != null) {
      url += "?" + query;
    }

    webClient
      .get(url)
      .send(result -> {
        if(result.succeeded()) {
          ApiGatewayUtil.onClientSuccessResponse(routingContext, result.result().statusCode(), result.result().bodyAsString());
        } else {
          Util.onErrorResponse(routingContext, 500, result.cause());
        }
      });
  }

  @Override
  public void handlePost(RoutingContext routingContext) {
    webClient
      .post(API_VERSION + routingContext.normalisedPath())
      .sendJsonObject(routingContext.getBodyAsJson(), result -> {
        if(result.succeeded()) {
          ApiGatewayUtil.onClientSuccessResponse(routingContext, result.result().statusCode(), result.result().bodyAsString());
        } else {
          Util.onErrorResponse(routingContext, 500, result.cause());
        }
      });
  }

  @Override
  public void handlePut(RoutingContext routingContext) {
    webClient
      .put(API_VERSION + routingContext.normalisedPath())
      .sendJsonObject(routingContext.getBodyAsJson(), result -> {
        if(result.succeeded()) {
          ApiGatewayUtil.onClientSuccessResponse(routingContext, result.result().statusCode(), result.result().bodyAsString());
        } else {
          Util.onErrorResponse(routingContext, 500, result.cause());
        }
      });
  }

  @Override
  public void handleDelete(RoutingContext routingContext) {
    webClient
      .delete(API_VERSION + routingContext.normalisedPath())
      .send(result -> {
        if(result.succeeded()) {
          ApiGatewayUtil.onClientSuccessResponse(routingContext, result.result().statusCode(), result.result().bodyAsString());
        } else {
          Util.onErrorResponse(routingContext, 500, result.cause());
        }
      });
  }
}
