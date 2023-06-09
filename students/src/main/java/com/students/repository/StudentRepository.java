package com.students.repository;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.util.List;
import com.students.entity.Student;

public interface StudentRepository {

  Single<List<Student>> findAll(JsonObject query);

}
