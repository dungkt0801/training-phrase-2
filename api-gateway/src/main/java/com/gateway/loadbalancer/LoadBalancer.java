package com.gateway.loadbalancer;

import static com.gateway.constants.Constants.API_VERSION;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoadBalancer {

  private final Vertx vertx;
  private final ServiceDiscovery serviceDiscovery;
  private ConcurrentHashMap<String, AtomicInteger> serviceIndices = new ConcurrentHashMap<>();

  public Single<WebClient> next(String serviceName) {
    return getClients(serviceName)
      .flatMap(clients -> {
        if (clients.isEmpty()) {
          return Single.error(new RuntimeException("No available services"));
        }

        AtomicInteger serviceIndex = serviceIndices.computeIfAbsent(serviceName, k -> new AtomicInteger(0));
        System.out.println("Service index: " + serviceIndex.get());
        System.out.println(serviceIndices.size());

        return Single.create(emitter -> tryNext(serviceName, clients, serviceIndex, emitter));
      });
  }

  private void tryNext(String serviceName, List<WebClient> clients, AtomicInteger serviceIndex, SingleEmitter<WebClient> emitter) {
    WebClient selectedClient = clients.get(serviceIndex.updateAndGet(i -> (i + 1) % clients.size()));
    healthCheck(selectedClient, serviceName, clients, ar -> {
      if (ar.succeeded()) {
        emitter.onSuccess(selectedClient);
      } else {
        int nextIndex = serviceIndex.updateAndGet(i -> (i + 1) % clients.size());
        System.out.println("Next index: " + nextIndex);
        System.out.println("serviceIndices size: " + serviceIndices.size());
        AtomicInteger currentServiceIndex = serviceIndices.get(serviceName);
        System.out.println("serviceIndices: " + serviceIndex);
        System.out.println(currentServiceIndex.get());
        if (currentServiceIndex == null || serviceIndices.isEmpty()) {
          emitter.onError(new RuntimeException("No available services"));
        } else {
          currentServiceIndex.updateAndGet(i -> (i + 1) % clients.size());
          tryNext(serviceName, clients, currentServiceIndex, emitter);
        }
      }
    });
  }
  public void healthCheck(WebClient client, String serviceName, List<WebClient> clients, Handler<AsyncResult<Void>> resultHandler) {
    client.get(API_VERSION + "/" + serviceName.split("-")[0]).send(ar -> {
      if (ar.succeeded() && ar.result().statusCode() == 200) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        //serviceIndices.remove(serviceName);  // remove unhealthy service
        clients.remove(client);
        resultHandler.handle(Future.failedFuture("Unhealthy service"));
      }
    });
  }

  private Single<List<WebClient>> getClients(String serviceName) {
    return Single.create(emitter -> {
      List<WebClient> newClients = new ArrayList<>();
      serviceDiscovery.getRecords(record -> record.getName().equals(serviceName), ar -> {
        if (ar.succeeded()) {
          List<Record> records = ar.result();
          for (Record record : records) {
            WebClientOptions options = new WebClientOptions()
              .setDefaultHost(record.getLocation().getString("host"))
              .setDefaultPort(record.getLocation().getInteger("port"));
            WebClient client = WebClient.create(vertx, options);
            newClients.add(client);
          }

          System.out.println(newClients.size());
          emitter.onSuccess(newClients);
        } else {
          emitter.onError(ar.cause());
        }
      });
    });
  }
}
