package com.students.repository;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.util.List;
import com.students.entity.Student;
public interface StudentRepository {

  Single<List<Student>> findAll(JsonObject query);

  Maybe<Student> findById(String id);

  Single<Student> insertOne(Student student);

  Single<Student> updateOne(String id, Student student);

  Maybe<Student> deleteOne(String id);

}
