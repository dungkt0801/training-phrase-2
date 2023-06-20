package com.gateway.handler.impl;

import static com.gateway.constants.Constants.API_VERSION;

import com.common.util.Util;
import com.gateway.handler.GatewayHandler;
import com.gateway.loadbalancer.LoadBalancer;
import com.gateway.util.ApiGatewayUtil;
import io.reactivex.Single;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GatewayHandlerImpl implements GatewayHandler {

  private final LoadBalancer loadBalancer;

  @Override
  public void handleGet(RoutingContext routingContext) {
    loadBalancer.next(ApiGatewayUtil.getDestinationService(routingContext.normalisedPath()))
      .flatMap(webClient -> {
        String query = routingContext.request().query();
        String baseUrl = API_VERSION + routingContext.normalisedPath();
        final String url = query != null ? baseUrl + "?" + query : baseUrl;

        HttpRequest<Buffer> request = webClient.get(url);
        MultiMap headers = routingContext.request().headers();
        ApiGatewayUtil.putHeadersForForwardingRequest(request, headers);

        return Single.create(emitter ->
          request.send(ar -> {
            if(ar.succeeded()) {
              emitter.onSuccess(ar.result());
            } else {
              emitter.onError(ar.cause());
            }
          })
        );
      })
      .subscribe(result -> {
        HttpResponse<Buffer> response = (HttpResponse<Buffer>) result;
        System.out.println(response.headers().get("Service-Port"));
        ApiGatewayUtil.onClientSuccessResponse(routingContext, response.statusCode(), response.bodyAsString());
        },
        throwable -> Util.onErrorResponse(routingContext, 500, throwable)
      );
  }

  @Override
  public void handlePost(RoutingContext routingContext) {
    loadBalancer.next(ApiGatewayUtil.getDestinationService(routingContext.normalisedPath()))
      .flatMap(webClient ->
        {
          final String url = API_VERSION + routingContext.normalisedPath();
          HttpRequest<Buffer> request = webClient.post(url);
          MultiMap headers = routingContext.request().headers();
          ApiGatewayUtil.putHeadersForForwardingRequest(request, headers);

          return Single.create(emitter ->
            request.sendJsonObject(routingContext.getBodyAsJson(), ar -> {
              if (ar.succeeded()) {
                emitter.onSuccess(ar.result());
              } else {
                emitter.onError(ar.cause());
              }
            })
          );
        }
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
  public void handlePut(RoutingContext routingContext) {
    loadBalancer.next(ApiGatewayUtil.getDestinationService(routingContext.normalisedPath()))
      .flatMap(webClient ->
        {
          final String url = API_VERSION + routingContext.normalisedPath();
          HttpRequest<Buffer> request = webClient.put(url);
          MultiMap headers = routingContext.request().headers();
          ApiGatewayUtil.putHeadersForForwardingRequest(request, headers);

          return Single.create(emitter ->
            request.sendJsonObject(routingContext.getBodyAsJson(), ar -> {
              if (ar.succeeded()) {
                emitter.onSuccess(ar.result());
              } else {
                emitter.onError(ar.cause());
              }
            }));
        }
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
  public void handleDelete(RoutingContext routingContext) {
    loadBalancer.next(ApiGatewayUtil.getDestinationService(routingContext.normalisedPath()))
      .flatMap(webClient ->
        {
          final String url = API_VERSION + routingContext.normalisedPath();
          HttpRequest<Buffer> request = webClient.delete(url);
          MultiMap headers = routingContext.request().headers();
          ApiGatewayUtil.putHeadersForForwardingRequest(request, headers);

          return Single.create(emitter ->
            request.send(ar -> {
              if (ar.succeeded()) {
                emitter.onSuccess(ar.result());
              } else {
                emitter.onError(ar.cause());
              }
            }));
        }
      ).subscribe(
        result -> {
          HttpResponse<Buffer> response = (HttpResponse<Buffer>) result;
          System.out.println(response.headers().get("Service-Port"));
          ApiGatewayUtil.onClientSuccessResponse(routingContext, response.statusCode(), response.bodyAsString());
        },
        throwable -> Util.onErrorResponse(routingContext, 500, throwable)
      );
  }
}
