package com.students.handler;

import io.vertx.ext.web.RoutingContext;

public interface StudentHandler {

  void findAll(RoutingContext rc);

  void findById(RoutingContext rc);

}
