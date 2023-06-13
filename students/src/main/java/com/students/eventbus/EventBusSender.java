package com.students.eventbus;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import java.util.List;

public interface EventBusSender {

  Single<JsonObject> sendClassInfoRequest(String classId);

  Single<JsonObject> sendUpdateClassRequest(String id, JsonObject clazzJson);

  Single<List<String>> sendGetClassIdsByName(String name);

}
