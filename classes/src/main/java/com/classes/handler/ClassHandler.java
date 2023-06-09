package com.classes.handler;

import io.vertx.reactivex.ext.web.RoutingContext;

public interface ClassHandler {

  void findAll(RoutingContext rc);

  void findById(RoutingContext rc);

  void insertOne(RoutingContext rc);

  void updateOne(RoutingContext rc);

  void deleteOne(RoutingContext rc);

}
