package com.students.service.impl;

import com.students.dto.ClassDto;
import com.students.eventbus.EventBusSender;
import com.students.repository.StudentRepository;
import io.reactivex.Maybe;
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
          .onErrorResumeNext(Single.just(new JsonObject()))
          .map(clazz -> buildStudentResponseDto(student, clazz)))
      .toList();
  }

  @Override
  public Maybe<StudentDto> findById(String id) {
    return studentRepository.findById(id)
      .flatMap(student -> eventBusSender.sendClassInfoRequest(student.getClassId())
        .toMaybe()
        .onErrorResumeNext(Maybe.just(new JsonObject()))
        .map(clazz -> buildStudentResponseDto(student, clazz))
      );
  }

  @Override
  public Single<StudentDto> insertOne(Student student) {
    return eventBusSender.sendClassInfoRequest(student.getClassId())
      .flatMap(clazz -> checkClassAvailableAndInsertStudent(clazz, student)
        .map(insertedStudent -> buildStudentResponseDto(student, clazz)))
      .onErrorResumeNext(Single::error);
  }

  private Single<Student> checkClassAvailableAndInsertStudent(JsonObject clazz, Student student) {
    Long enrolledStudents = clazz.getLong("enrolledStudents");
    Long totalStudents = clazz.getLong("totalStudents");
    if(enrolledStudents < totalStudents) {
      return insertStudentAndUpdateClass(clazz, student);
    } else {
      return Single.error(new IllegalArgumentException("The class is at maximum enrollment capacity"));
    }
  }

  private Single<Student> insertStudentAndUpdateClass(JsonObject clazz, Student student){
    return studentRepository.insertOne(student)
      .flatMap(insertedStudent -> {
        clazz.put("enrolledStudents", clazz.getLong("enrolledStudents") + 1);
        return eventBusSender.sendUpdateClassRequest(clazz.getString("id"), clazz)
          .map(response -> insertedStudent);
      });
  }

  private StudentDto buildStudentResponseDto(Student student, JsonObject clazzJson) {
    return StudentDto.builder()
      .id(student.getId())
      .name(student.getName())
      .birthday(student.getBirthday())
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
