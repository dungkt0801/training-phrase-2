package com.classes.service;

import com.classes.entity.Class;
import com.common.dto.ClassDto;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.util.List;

public interface ClassService {

  Single<List<ClassDto>> findAll(JsonObject query);

  Maybe<ClassDto> findById(String id);

  Single<ClassDto> insertOne(Class clazz);

  Maybe<ClassDto> updateOne(String id, Class clazz);

  Single<List<String>> findClassIdsByName(String name);

}
