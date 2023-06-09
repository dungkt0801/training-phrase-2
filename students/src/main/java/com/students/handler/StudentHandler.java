package com.students.handler;

import io.vertx.reactivex.ext.web.RoutingContext;

public interface StudentHandler {

  void findAll(RoutingContext rc);

}
