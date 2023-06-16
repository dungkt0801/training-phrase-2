package com.gateway.loadbalancer;

import io.reactivex.Single;
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
    return getClients(serviceName).flatMap(clients -> {
      AtomicInteger serviceIndex = serviceIndices.computeIfAbsent(serviceName, k -> new AtomicInteger(0));
      int currentIndex = serviceIndex.getAndUpdate(i -> (i + 1) % clients.size());
      System.out.println(serviceName + ": " + clients.size());
      return Single.just(clients.get(currentIndex));
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

            // check if service is still working
//            client.get(API_VERSION + "/" + serviceName.split("-")[0]).send(result -> {
//              if (result.succeeded() && result.result().statusCode() == 200) {
//                System.out.print("add ne");
//                newClients.add(client);
//              }
//            });
            
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
