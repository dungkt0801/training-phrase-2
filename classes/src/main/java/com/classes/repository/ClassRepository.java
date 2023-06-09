package com.classes.repository;

import com.classes.entity.Class;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.util.List;

public interface ClassRepository {

  Single<List<Class>> findAll(JsonObject query);

  Maybe<Class> findById(String id);

  Single<Class> insertOne(Class clazz);

  Single<String> updateOne(String id, Class clazz);

  Single<List<String>> findClassIdsByName(String name);

}
