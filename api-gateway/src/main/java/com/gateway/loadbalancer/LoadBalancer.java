package com.gateway.loadbalancer;

import static com.gateway.constants.Constants.API_VERSION;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
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

  private final ConcurrentHashMap<String, AtomicInteger> serviceIndices;

  public Single<WebClient> next(String serviceName) {
    return getClients(serviceName)
      .flatMap(clients -> {
        AtomicInteger serviceIndex = serviceIndices.computeIfAbsent(serviceName, k -> new AtomicInteger(0));
        if(serviceIndex.get() >= clients.size()) {
          serviceIndex.set(0);
        }
        System.out.println("Service index: " + serviceIndex.get());
        return Single.create(emitter -> tryNext(serviceName, clients, serviceIndex, emitter, 0));
      });
  }

  private void tryNext(String serviceName, List<WebClient> clients, AtomicInteger serviceIndex, SingleEmitter<WebClient> emitter, int retries) {

    if (retries >= clients.size()) {
      emitter.onError(new RuntimeException("No available services"));
      return;
    }

    WebClient selectedClient = clients.get(
      serviceIndex.getAndUpdate(i -> (i + 1) % clients.size()));
    healthCheck(selectedClient, serviceName)
      .subscribe(
        result -> {
          System.out.println("success");
          emitter.onSuccess(selectedClient);
        },
        error -> {
          System.out.println("fail");
          AtomicInteger currentServiceIndex = serviceIndices.get(serviceName);
          System.out.println("currentServiceIndex: " + currentServiceIndex);
          tryNext(serviceName, clients, currentServiceIndex, emitter, retries + 1);
        }
      );
  }

  public Single<WebClient> healthCheck(WebClient client, String serviceName) {
    return Single.create(emitter -> {
      client.get(API_VERSION + "/" + serviceName).send(ar -> {
        if (ar.succeeded() && ar.result().statusCode() == 200) {
          emitter.onSuccess(client);
        } else {
          emitter.onError(new Exception("Unhealthy service"));
        }
      });
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

          System.out.println("Found clients with name '" + serviceName + "': " + newClients.size());
          emitter.onSuccess(newClients);
        } else {
          emitter.onError(ar.cause());
        }
      });
    });
  }
}
