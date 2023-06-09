package com.classes.router;

import com.classes.handler.ClassHandler;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassRouter {

  private final Vertx vertx;

  private final ClassHandler classHandler;

  public Router getRouter() {
    final Router classRouter = Router.router(vertx);

    classRouter.route("/api/v1/classes*").handler(BodyHandler.create());
    classRouter.get("/api/v1/classes").handler(classHandler::findAll);
    classRouter.get("/api/v1/classes/:id").handler(classHandler::findById);
//    classRouter.post("/api/v1/classes").handler(classHandler::insertOne);
//    classRouter.put("/api/v1/classes/:id").handler(classHandler::updateOne);

    return classRouter;
  }

}
