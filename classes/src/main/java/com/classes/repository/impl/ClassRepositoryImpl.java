package com.classes.repository.impl;

import com.classes.entity.Class;
import com.classes.repository.ClassRepository;
import com.classes.util.ClassUtil;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassRepositoryImpl implements ClassRepository {

  private static final String COLLECTION_NAME = "classes";

  private final MongoClient mongoClient;

  @Override
  public Single<List<Class>> findAll(JsonObject query) {
    return Single.create(emitter -> mongoClient.find(COLLECTION_NAME, query, res -> {
      if (res.succeeded()) {
        List<Class> classes = res.result().stream()
          .map(Class::new)
          .collect(Collectors.toList());

        emitter.onSuccess(classes);
      } else {
        emitter.onError(res.cause());
      }
    }));
  }

  @Override
  public Maybe<Class> findById(String id) {
    return Maybe.create(emitter -> {
      JsonObject query = new JsonObject().put("_id", new JsonObject().put("$oid", id));
      mongoClient.findOne(COLLECTION_NAME, query, null, result -> {
        if (result.succeeded()) {
          if (result.result() != null) {
            Class clazz = new Class(result.result());
            emitter.onSuccess(clazz);
          } else {
            emitter.onComplete();
          }
        } else {
          emitter.onError(result.cause());
        }
      });
    });
  }

  @Override
  public Single<Class> insertOne(Class clazz) {
    return null;
  }

  @Override
  public Maybe<Class> updateOne(String id, Class clazz) {
    return findById(id)
      .flatMap(existingClass -> {
        existingClass.setClassName(clazz.getClassName());
        existingClass.setTotalStudents(clazz.getTotalStudents());
        existingClass.setEnrolledStudents(clazz.getEnrolledStudents());

        return updateClass(existingClass, existingClass.getVersion());
      });
  }

  private Maybe<Class> updateClass(Class clazz, Long version) {

    final JsonObject query = new JsonObject()
      .put("_id", new JsonObject().put("$oid", clazz.getId()))
      .put("version", version);

    JsonObject update = new JsonObject()
      .put("$set", ClassUtil.jsonObjectFromClass(clazz))
      .put("$inc", new JsonObject().put("totalStudents", -1))
      .put("$inc", new JsonObject().put("version", 1));

    return Maybe.create(emitter -> mongoClient.updateCollection(
      COLLECTION_NAME,
      query, update,
      res -> {
        if (res.succeeded()) {
          if (res.result().getDocMatched() > 0) {
            emitter.onSuccess(new Class(res.result().toJson()));
          } else {
            emitter.onComplete();
          }
        } else {
          emitter.onError(res.cause());
        }
      }
    ));
  }

  @Override
  public Single<List<String>> findClassIdsByName(String name) {
    return null;
  }
}
