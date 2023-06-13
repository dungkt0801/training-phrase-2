package com.classes.repository.impl;

import com.classes.entity.Class;
import com.classes.repository.ClassRepository;
import com.classes.util.ClassUtil;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

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

    JsonObject classJson = ClassUtil.jsonObjectFromClass(clazz)
      .put("_id", new JsonObject().put("$oid", new ObjectId().toString()))
      .put("version", 0);

    return Single.create(emitter -> mongoClient.insert(COLLECTION_NAME, classJson, result -> {
      if(result.succeeded()) {
        emitter.onSuccess(new Class(classJson));
      } else {
        emitter.onError(result.cause());
      }
    }));
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

    return Maybe.create(emitter -> mongoClient.findOneAndUpdateWithOptions(COLLECTION_NAME,
      query,
      update,
      new FindOptions(),
      new UpdateOptions().setReturningNewDocument(true),
      res -> {
        if (res.succeeded()) {
          if (res.result() != null) {
            emitter.onSuccess(new Class(res.result()));
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
