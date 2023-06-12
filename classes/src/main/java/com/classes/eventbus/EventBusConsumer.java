package com.classes.eventbus;

import com.classes.entity.Class;
import com.classes.service.ClassService;
import com.classes.util.ClassUtil;
import io.reactivex.Completable;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventBusConsumer {

  private final EventBus eventBus;

  private final ClassService classService;

  public Completable registerConsumers() {
    return Completable.create(emitter -> {
      try {
        eventBus.consumer("request.classInfo", this::handleClassInfoRequest);
        eventBus.consumer("request.updateClass", this::handleUpdateClassRequest);
        emitter.onComplete();
      } catch (Exception e) {
        emitter.onError(e);
      }
    });
  }

  private void handleClassInfoRequest(Message<JsonObject> message) {
    String classId = message.body().getString("classId");

    classService.findById(classId)
      .subscribe(
        classInfo -> {
          JsonObject response = new JsonObject()
            .put("classId", classInfo.getId())
            .put("className", classInfo.getClassName())
            .put("totalStudents", classInfo.getTotalStudents())
            .put("enrolledStudents", classInfo.getEnrolledStudents());
          message.reply(response);
        },
        error -> {
          message.reply(new JsonObject().put("error", error.getMessage()));
        },
        () -> {
          message.reply(new JsonObject().put("error", "No class found with the id " + classId));
        }
      );
  }

  private void handleUpdateClassRequest(Message<JsonObject> message) {
    JsonObject request = message.body();
    System.out.println("request: " + request);
    final String id = request.getString("classId");
    final Class clazz = ClassUtil.classFromJsonObject(request.getJsonObject("classRequest"));
    System.out.println(request.getJsonObject("classRequest"));
    System.out.println(clazz.getEnrolledStudents());
    classService.updateOne(id, clazz)
      .subscribe(
        classInfo -> {
          System.out.println("updated class: " + classInfo);
          JsonObject response = new JsonObject()
            .put("classId", classInfo.getId())
            .put("className", classInfo.getClassName())
            .put("totalStudents", classInfo.getTotalStudents())
            .put("enrolledStudents", classInfo.getEnrolledStudents());
          System.out.println("updated class: " + response);
          message.reply(response);
        },
        error -> message.reply(new JsonObject().put("error", error.getMessage())),
        () -> message.reply(new JsonObject().put("error", "No class found with the id " + id))
      );
  }

}

