package com.students.repository.impl;

import com.students.entity.Student;
import com.students.repository.StudentRepository;
import com.students.util.StudentUtil;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepository {

  private static final String COLLECTION_NAME = "students";

  private final MongoClient mongoClient;

  @Override
  public Single<List<Student>> findAll(JsonObject query) {
    return Single.create(emitter -> mongoClient.find(COLLECTION_NAME, query, res -> {
      if (res.succeeded()) {
        List<Student> students = res.result().stream()
          .map(Student::new)
          .collect(Collectors.toList());

        emitter.onSuccess(students);
      } else {
        emitter.onError(res.cause());
      }
    }));
  }

  @Override
  public Maybe<Student> findById(String id) {
    return Maybe.create(emitter -> {
      JsonObject query = new JsonObject().put("_id", new JsonObject().put("$oid", id));
      mongoClient.findOne(COLLECTION_NAME, query, null, res -> {
        if(res.succeeded()) {
          if(res.result() != null) {
            emitter.onSuccess(new Student(res.result()));
          } else {
            emitter.onComplete();
          }
        } else {
          emitter.onError(res.cause());
        }
      });
    });
  }

  @Override
  public Single<Student> insertOne(Student student) {
    JsonObject studentDocument = StudentUtil.jsonObjectFromStudent(student);
    String id = new ObjectId().toString();
    studentDocument.put("_id", new JsonObject().put("$oid", id));
    student.setId(id);

    return Single.create(emitter -> mongoClient.insert(COLLECTION_NAME, studentDocument, res -> {
      if (res.succeeded()) {
        emitter.onSuccess(student);
      } else {
        emitter.onError(res.cause());
      }
    }));
  }

  @Override
  public Single<Student> updateOne(String id, Student student) {

    final JsonObject query = new JsonObject()
      .put("_id", new JsonObject().put("$oid", id));

    JsonObject update = new JsonObject()
      .put("$set", StudentUtil.jsonObjectFromStudent(student));

    return Single.create(emitter -> mongoClient.findOneAndUpdateWithOptions(
      COLLECTION_NAME,
      query,
      update,
      new FindOptions(),
      new UpdateOptions().setReturningNewDocument(true),
      res -> {
        if (res.succeeded() && res.result() != null) {
          emitter.onSuccess(new Student(res.result()));
        } else {
          emitter.onError(new NoSuchElementException("No student was found with the id " + id));
        }
      }
    ));
  }

  @Override
  public Maybe<Student> deleteOne(String id) {

    final JsonObject query = new JsonObject().put("_id", new JsonObject().put("$oid", id));

    return Maybe.create(emitter ->
      mongoClient.findOneAndDelete(COLLECTION_NAME, query, res -> {
        if(res.succeeded()) {
          JsonObject removedStudent = res.result();
          if(res.result() != null) {
            emitter.onSuccess(new Student(removedStudent));
          } else {
            emitter.onComplete();
          }
        } else {
          emitter.onError(res.cause());
        }
      })
    );
  }

}
