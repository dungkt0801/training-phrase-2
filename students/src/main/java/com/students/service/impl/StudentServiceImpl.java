package com.students.service.impl;

import com.common.dto.ClassDto;
import com.common.dto.StudentDto;
import com.students.eventbus.EventBusSender;
import com.students.repository.StudentRepository;
import com.students.util.StudentUtil;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import com.students.entity.Student;
import com.students.service.StudentService;

@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

  private final EventBusSender eventBusSender;

  private final StudentRepository studentRepository;

  @Override
  public Single<List<StudentDto>> findAll(JsonObject query) {

    // check if query by className
    if(query.containsKey("className")) {
      return eventBusSender.sendGetClassIdsByName(query.remove("className").toString())
        .map(classIds -> classIds.stream().map(classId -> new JsonObject().put("$oid", classId)).collect(
          Collectors.toList()))
        .map(classObjectIds -> query.put("classId", new JsonObject().put("$in", classObjectIds)))
        .flatMap(this::getAllStudents);
    }

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
      .flatMap(student ->
        eventBusSender.sendClassInfoRequest(student.getClassId())
          .toMaybe()
          .onErrorResumeNext(Maybe.just(new JsonObject()))
          .map(clazz -> buildStudentResponseDto(student, clazz))
      );
  }

  @Override
  public Single<StudentDto> insertOne(Student student) {
    return eventBusSender.sendClassInfoRequest(student.getClassId())
      .flatMap(clazz -> checkClassAvailableAndInsertStudent(clazz, student))
      .flatMap(insertedStudent ->
        eventBusSender.sendClassInfoRequest(insertedStudent.getClassId())
          .map(clazzInfo -> buildStudentResponseDto(student, clazzInfo))
      )
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

  @Override
  public Single<StudentDto> updateOne(String id, Student student) {
    return findById(id)
      .switchIfEmpty(Maybe.error(new NoSuchElementException("No student was found with the id " + id)))
      .flatMap(oldStudentDto -> {
        student.setId(id);
        String oldClassId = oldStudentDto.getClassInfo().getId();
        return eventBusSender.sendClassInfoRequest(student.getClassId())
          .flatMap(clazz -> checkClassAvailableAndUpdateStudent(clazz, student))
          .flatMap(insertedStudent -> updateOldClass(oldClassId)
            .flatMap(response -> eventBusSender.sendClassInfoRequest(insertedStudent.getClassId())
              .map(classInfo -> buildStudentResponseDto(student, classInfo))
            )
          )
          .toMaybe();
      })
      .toSingle()
      .onErrorResumeNext(Single::error);
  }

  private Single<Student> checkClassAvailableAndUpdateStudent(JsonObject clazz, Student student) {
    Long enrolledStudents = clazz.getLong("enrolledStudents");
    Long totalStudents = clazz.getLong("totalStudents");
    return (enrolledStudents < totalStudents)
      ? updateStudentAndUpdateClass(clazz, student)
      : Single.error(new IllegalArgumentException("The class is at maximum enrollment capacity"));
  }

  private Single<Student> updateStudentAndUpdateClass(JsonObject clazz, Student student) {
    return studentRepository.updateOne(student.getId(), student)
      .flatMap(insertedStudent -> {
        clazz.put("enrolledStudents", clazz.getLong("enrolledStudents") + 1);
        return eventBusSender.sendUpdateClassRequest(clazz.getString("id"), clazz)
          .map(response -> insertedStudent);
      });
  }

  private Single<JsonObject> updateOldClass(String oldClassId) {
    return eventBusSender.sendClassInfoRequest(oldClassId)
      .flatMap(oldClazz -> {
        oldClazz.put("enrolledStudents", oldClazz.getLong("enrolledStudents") - 1);
        return eventBusSender.sendUpdateClassRequest(oldClassId, oldClazz);
      });
  }

  @Override
  public Maybe<Student> deleteOne(String id) {
    return studentRepository.deleteOne(id)
      .flatMap(deletedStudent ->
        eventBusSender.sendClassInfoRequest(deletedStudent.getClassId()).toMaybe()
          .flatMap(clazz -> updateOldClass(clazz.getString("id")).toMaybe())
          .map(updatedClass -> deletedStudent)
      );
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
