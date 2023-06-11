package com.students.eventbus;

import io.reactivex.Single;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventBusSender {

  private final EventBus eventBus;

  public Single<JsonObject> sendClassInfoRequest(String classId) {
    return Single.create(emitter -> {
      JsonObject request = new JsonObject().put("classId", classId);

      eventBus.<JsonObject>send("request.classinfo", request, reply -> {
        if (reply.succeeded()) {
          JsonObject classInfo = reply.result().body();
          emitter.onSuccess(classInfo);
        } else {
          emitter.onError(reply.cause());
        }
      });
    });
  }

}

