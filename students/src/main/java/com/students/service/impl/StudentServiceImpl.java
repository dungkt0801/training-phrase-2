package com.students.service.impl;

import com.students.dto.ClassDto;
import com.students.repository.StudentRepository;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import com.students.dto.StudentDto;
import com.students.entity.Student;
import com.students.service.StudentService;

@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

  private final EventBus eventBus;

  private final StudentRepository studentRepository;

  @Override
  public Single<List<StudentDto>> findAll(JsonObject query) {
    return getAllStudents(query);
  }

  private Single<List<StudentDto>> getAllStudents(JsonObject query) {
    return studentRepository.findAll(query)
      .flatMapObservable(Observable::fromIterable)
      .flatMapSingle(student ->
        getClassInfoFromEventBus(student.getClassId())
          .map(clazz -> buildStudentResponseDto(student, clazz)))
      .toList();
  }

  private Single<JsonObject> getClassInfoFromEventBus(String classId) {
    return Single.create(emitter -> {
      JsonObject request = new JsonObject().put("classId", classId);

      eventBus.send("request.classinfo", request, reply -> {
        if (reply.succeeded()) {
          JsonObject classInfo = (JsonObject) reply.result().body();
          emitter.onSuccess(classInfo);
        } else {
          emitter.onError(reply.cause());
        }
      });
    });
  }

  private StudentDto buildStudentResponseDto(Student student, JsonObject clazzJson) {
    return StudentDto.builder()
      .id(student.getId())
      .name(student.getName())
      .birthDay(student.getBirthDay())
      .classInfo(
        ClassDto.builder()
          .id(clazzJson.getString("id"))
          .className(clazzJson.getString("className"))
          .totalStudents(clazzJson.getLong("totalStudents"))
          .enrolledStudents(clazzJson.getLong("enrolledStudents"))
          .build()
      )
      .build();
  }

}
