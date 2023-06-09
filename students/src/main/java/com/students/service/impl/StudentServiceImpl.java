package com.students.service.impl;

import com.students.dto.ClassDto;
import com.students.eventbus.EventBusSender;
import com.students.repository.StudentRepository;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.util.List;
import lombok.RequiredArgsConstructor;
import com.students.dto.StudentDto;
import com.students.entity.Student;
import com.students.service.StudentService;

@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

  private final EventBusSender eventBusSender;

  private final StudentRepository studentRepository;

  @Override
  public Single<List<StudentDto>> findAll(JsonObject query) {
    return getAllStudents(query);
  }

  private Single<List<StudentDto>> getAllStudents(JsonObject query) {
    return studentRepository.findAll(query)
      .flatMapObservable(Observable::fromIterable)
      .flatMapSingle(student ->
        eventBusSender.sendClassInfoRequest(student.getClassId())
          .map(clazz -> buildStudentResponseDto(student, clazz)))
      .toList();
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
