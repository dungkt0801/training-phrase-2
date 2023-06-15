package com.students.service;

import com.common.dto.StudentDto;
import com.students.entity.Student;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.util.List;

public interface StudentService {

  Single<List<StudentDto>> findAll(JsonObject query);

  Maybe<StudentDto> findById(String id);

  Single<StudentDto> insertOne(Student student);

  Single<StudentDto> updateOne(String id, Student student);

  Maybe<Student> deleteOne(String id);

}
