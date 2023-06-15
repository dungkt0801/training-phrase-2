package com.gateway.loadbalancer;

import io.vertx.ext.web.client.WebClient;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoadBalancer {

  private final List<WebClient> clients;
  private final AtomicInteger index;

  public WebClient next() {
    int currentIndex = index.getAndUpdate(i -> (i + 1) % clients.size());
    return clients.get(currentIndex);
  }

}
