package com.gateway.handler.impl;

import static com.gateway.constants.Constants.API_VERSION;

import com.common.util.Util;
import com.gateway.handler.GatewayHandler;
import com.gateway.loadbalancer.LoadBalancer;
import com.gateway.util.ApiGatewayUtil;
import io.reactivex.Single;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GatewayHandlerImpl implements GatewayHandler {

  private final LoadBalancer loadBalancer;

  @Override
  public void handleGet(RoutingContext routingContext) {
    String query = routingContext.request().query();
    String base_url = API_VERSION + routingContext.normalisedPath();

    final String url = query != null ? base_url + "?" + query : base_url;

    loadBalancer.next(ApiGatewayUtil.extractKeywordFromPath(routingContext.normalisedPath()) + "-service").flatMap(webClient ->
      Single.create(emitter -> webClient
        .get(url)
        .send(ar -> {
          if(ar.succeeded()) {
            emitter.onSuccess(ar.result());
          } else {
            emitter.onError(ar.cause());
          }
        }))
    ).subscribe(
      result -> {
        HttpResponse<Buffer> response = (HttpResponse<Buffer>) result;
        System.out.println(response.headers().get("Service-Port"));
        ApiGatewayUtil.onClientSuccessResponse(routingContext, response.statusCode(), response.bodyAsString());
      },
      throwable -> Util.onErrorResponse(routingContext, 500, throwable)
    );
  }


  @Override
  public void handlePost(RoutingContext routingContext) {
//    LoadBalancer loadBalancer = loadBalancers.get(ApiGatewayUtil.extractKeywordFromPath(routingContext.normalisedPath()) + "-service");
//    System.out.println(ApiGatewayUtil.extractKeywordFromPath(routingContext.normalisedPath()) + "-service");
//
//    loadBalancer.next()
//      .flatMap(webClient ->
//        Single.create(emitter -> webClient
//          .post(API_VERSION + routingContext.normalisedPath())
//          .sendJsonObject(routingContext.getBodyAsJson(), ar -> {
//            if(ar.succeeded()) {
//              emitter.onSuccess(ar.result());
//            } else {
//              emitter.onError(ar.cause());
//            }
//          }))
//      ).subscribe(
//      result -> {
//        HttpResponse<Buffer> response = (HttpResponse<Buffer>) result;
//        System.out.println(response.headers().get("Service-Port"));
//        ApiGatewayUtil.onClientSuccessResponse(routingContext, response.statusCode(), response.bodyAsString());
//      },
//      throwable -> Util.onErrorResponse(routingContext, 500, throwable)
//    );
  }

  @Override
  public void handlePut(RoutingContext routingContext) {
//    LoadBalancer loadBalancer = loadBalancers.get(ApiGatewayUtil.extractKeywordFromPath(routingContext.normalisedPath()) + "-service");
//    System.out.println(ApiGatewayUtil.extractKeywordFromPath(routingContext.normalisedPath()) + "-service");
//
//    loadBalancer.next()
//      .flatMap(webClient ->
//        Single.create(emitter -> webClient
//          .put(API_VERSION + routingContext.normalisedPath())
//          .sendJsonObject(routingContext.getBodyAsJson(), ar -> {
//            if(ar.succeeded()) {
//              emitter.onSuccess(ar.result());
//            } else {
//              emitter.onError(ar.cause());
//            }
//          }))
//      ).subscribe(
//        result -> {
//          HttpResponse<Buffer> response = (HttpResponse<Buffer>) result;
//          System.out.println(response.headers().get("Service-Port"));
//          ApiGatewayUtil.onClientSuccessResponse(routingContext, response.statusCode(), response.bodyAsString());
//        },
//        throwable -> Util.onErrorResponse(routingContext, 500, throwable)
//      );
  }

  @Override
  public void handleDelete(RoutingContext routingContext) {
//    LoadBalancer loadBalancer = loadBalancers.get(ApiGatewayUtil.extractKeywordFromPath(routingContext.normalisedPath()) + "-service");
//    System.out.println(ApiGatewayUtil.extractKeywordFromPath(routingContext.normalisedPath()) + "-service");
//
//    loadBalancer.next()
//      .flatMap(webClient ->
//        Single.create(emitter -> webClient
//          .delete(API_VERSION + routingContext.normalisedPath())
//          .send(ar -> {
//            if(ar.succeeded()) {
//              emitter.onSuccess(ar.result());
//            } else {
//              emitter.onError(ar.cause());
//            }
//          }))
//      ).subscribe(
//        result -> {
//          HttpResponse<Buffer> response = (HttpResponse<Buffer>) result;
//          System.out.println(response.headers().get("Service-Port"));
//          ApiGatewayUtil.onClientSuccessResponse(routingContext, response.statusCode(), response.bodyAsString());
//        },
//        throwable -> Util.onErrorResponse(routingContext, 500, throwable)
//      );
  }
}
