package com.students.repository.impl;

import com.students.entity.Student;
import com.students.repository.StudentRepository;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.mongo.MongoClient;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepository {

  private static final String COLLECTION_NAME = "students";

  private final MongoClient mongoClient;

  @Override
  public Single<List<Student>> findAll(JsonObject query) {
    return Single.create(emitter -> {
      mongoClient.find(COLLECTION_NAME, query, res -> {
        if (res.succeeded()) {
          List<Student> students = res.result().stream()
            .map(Student::new)
            .collect(Collectors.toList());

          emitter.onSuccess(students);
        } else {
          emitter.onError(res.cause());
        }
      });
    });
  }

}
