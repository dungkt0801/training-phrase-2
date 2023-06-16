package com.gateway.loadbalancer;

import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoadBalancer {

  private final ServiceDiscovery serviceDiscovery;

  private final Vertx vertx;

  private final String serviceName;

  private AtomicInteger index = new AtomicInteger(0);


  public Single<WebClient> next() {
    return getClients().flatMap(clients -> {
      int currentIndex = index.getAndUpdate(i -> (i + 1) % clients.size());
      System.out.println(serviceName + ": " + clients.size());
      return Single.just(clients.get(currentIndex));
    });
  }

  private Single<List<WebClient>> getClients() {
    return Single.create(
      emitter -> serviceDiscovery.getRecords(record -> record.getName().equals(serviceName),
        ar -> {
          if (ar.succeeded()) {
            List<WebClient> newClients = new ArrayList<>();
            for (Record record : ar.result()) {
              WebClient client = WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost(record.getLocation().getString("host"))
                .setDefaultPort(record.getLocation().getInteger("port")));
              newClients.add(client);
            }
            emitter.onSuccess(newClients);
          } else {
            emitter.onError(ar.cause());
          }
        }));
  }

}
