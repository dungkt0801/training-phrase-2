package com.students.service;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.util.List;
import com.students.dto.StudentDto;

public interface StudentService {

  Single<List<StudentDto>> findAll(JsonObject query);

  Maybe<StudentDto> findById(String id);

}
