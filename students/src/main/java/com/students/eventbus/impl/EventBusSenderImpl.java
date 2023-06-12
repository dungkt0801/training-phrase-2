package com.students.eventbus.impl;

import com.students.eventbus.EventBusSender;
import io.reactivex.Single;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventBusSenderImpl implements EventBusSender {

  private final EventBus eventBus;

  public Single<JsonObject> sendClassInfoRequest(String classId) {
    return Single.create(emitter -> {
      JsonObject request = new JsonObject().put("classId", classId);

      eventBus.<JsonObject>send("request.classInfo", request, reply -> {
        if (reply.succeeded()) {
          JsonObject responseBody = reply.result().body();
          if(responseBody.getString("error") == null) {
            emitter.onSuccess(reply.result().body());
          } else {
            emitter.onError(new NoSuchElementException("No class was found with the id " + classId));
          }
        } else {
          emitter.onError(reply.cause());
        }
      });
    });
  }

  public Single<JsonObject> sendUpdateClassRequest(String id, JsonObject clazzJson) {
    return Single.create(emitter -> {
      JsonObject request = new JsonObject()
        .put("classId", id)
        .put("classRequest", clazzJson);
      eventBus.<JsonObject>send("request.updateClass", request, reply -> {
        if (reply.succeeded()) {
          JsonObject responseBody = reply.result().body();
          if(responseBody.getString("error") == null) {
            emitter.onSuccess(reply.result().body());
          } else {
            emitter.onError(new NoSuchElementException("No class was found with the id " + id));
          }
        } else {
          emitter.onError(reply.cause());
        }
      });
    });
  }

}

