package com.classes.service.impl;

import com.classes.dto.ClassDto;
import com.classes.entity.Class;
import com.classes.repository.ClassRepository;
import com.classes.service.ClassService;
import com.classes.util.ClassUtil;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

  private final ClassRepository classRepository;

  @Override
  public Single<List<ClassDto>> findAll(JsonObject query) {
    return classRepository.findAll(query)
      .flatMap(classes -> {
        List<Single<ClassDto>> classDtoSingles = classes.stream()
          .map(classObject -> Single.just(ClassUtil.classToClassDto(classObject)))
          .collect(Collectors.toList());
        return Single.zip(classDtoSingles, (Object[] objects) -> {
          List<ClassDto> classDtos = new ArrayList<>();
          for (Object object : objects) {
            classDtos.add((ClassDto) object);
          }
          return classDtos;
        });
      });
  }

  @Override
  public Maybe<ClassDto> findById(String id) {
    return classRepository.findById(id)
      .map(ClassUtil::classToClassDto);
  }

  @Override
  public Single<Class> insertOne(Class clazz) {
    return null;
  }

  @Override
  public Maybe<ClassDto> updateOne(String id, Class clazz) {
    return classRepository.updateOne(id, clazz)
      .map(ClassUtil::classToClassDto);
  }

  @Override
  public Single<List<String>> findClassIdsByName(String name) {
    return null;
  }

}
