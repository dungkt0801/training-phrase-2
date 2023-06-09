package com.classes.repository.impl;

import com.classes.entity.Class;
import com.classes.repository.ClassRepository;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.mongo.MongoClient;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassRepositoryImpl implements ClassRepository {

  private static final String COLLECTION_NAME = "classes";

  private final MongoClient mongoClient;

  @Override
  public Single<List<Class>> findAll(JsonObject query) {
    return Single.create(emitter -> {
      mongoClient.find(COLLECTION_NAME, query, res -> {
        if (res.succeeded()) {
          List<Class> classes = res.result().stream()
            .map(Class::new)
            .collect(Collectors.toList());

          emitter.onSuccess(classes);
        } else {
          emitter.onError(res.cause());
        }
      });
    });
  }

  @Override
  public Maybe<Class> findById(String id) {
    JsonObject query = new JsonObject().put("_id", new JsonObject().put("$oid", id));

    return mongoClient.rxFindOne(COLLECTION_NAME, query, null)
      .flatMap(result -> {
        System.out.println(result);
        final Class clazz = new Class(result);
        return Maybe.just(clazz);
      });
  }

  @Override
  public Single<Class> insertOne(Class clazz) {
    return null;
  }

  @Override
  public Single<String> updateOne(String id, Class clazz) {
    return null;
  }

  @Override
  public Single<List<String>> findClassIdsByName(String name) {
    return null;
  }
}
