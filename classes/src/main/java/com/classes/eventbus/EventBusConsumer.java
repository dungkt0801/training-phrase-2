package com.classes.eventbus;

import com.classes.service.ClassService;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventBusConsumer {

  private final EventBus eventBus;

  private final ClassService classService;

  public Completable registerConsumers() {
    return Completable.create(emitter -> {
      try {
        eventBus.consumer("request.classinfo", this::handleClassInfoRequest);
//        eventBus.consumer("request.studentinfo", this::handleStudentInfoRequest);
        emitter.onComplete();
      } catch (Exception e) {
        emitter.onError(e);
      }
    });
  }

  private void handleClassInfoRequest(Message<Object> message) {
    JsonObject request = (JsonObject) message.body();
    String classId = request.getString("classId");

    System.out.println("handleClassInfoRequest: " + classId);
    classService.findById(classId)
      .subscribe(
        classInfo -> {
          System.out.println("handleClassInfoRequest classInfo: " + classInfo);
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

  private void handleStudentInfoRequest(Message<Object> message) {
    // Similar to handleClassInfoRequest, but fetch student info instead
  }

}

