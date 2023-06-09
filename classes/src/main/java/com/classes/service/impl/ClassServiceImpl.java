package com.classes.service.impl;

import com.classes.entity.Class;
import com.classes.repository.ClassRepository;
import com.classes.service.ClassService;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

  private final ClassRepository classRepository;

  @Override
  public Single<List<Class>> findAll(JsonObject query) {
    return classRepository.findAll(query);
  }

  @Override
  public Maybe<Class> findById(String id) {
    System.out.println("class service: " + id);
    return classRepository.findById(id);
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
