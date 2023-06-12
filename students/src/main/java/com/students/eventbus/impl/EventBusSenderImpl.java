package com.students.eventbus.impl;

import com.students.eventbus.EventBusSender;
import io.reactivex.Single;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventBusSenderImpl implements EventBusSender {

  private final EventBus eventBus;

  public Single<JsonObject> sendClassInfoRequest(String classId) {
    return Single.create(emitter -> {
      JsonObject request = new JsonObject().put("classId", classId);

      eventBus.<JsonObject>send("request.classInfo", request, reply -> {
        if (reply.succeeded()) {
          System.out.println(reply.result().body());
          emitter.onSuccess(reply.result().body());
        } else {
          emitter.onError(reply.cause());
        }
      });
    });
  }

  public Single<JsonObject> sendUpdateClassRequest(String id, JsonObject clazzJson) {
    return Single.create(emitter -> {
      System.out.println("sendUpdateClassRequest: " + id);
      JsonObject request = new JsonObject()
        .put("classId", id)
        .put("classRequest", clazzJson);
      eventBus.<JsonObject>send("request.updateClass", request, reply -> {
        System.out.println(reply.result());
        if (reply.succeeded()) {
          emitter.onSuccess(reply.result().body());
        } else {
          emitter.onError(reply.cause());
        }
      });
    });
  }

}

