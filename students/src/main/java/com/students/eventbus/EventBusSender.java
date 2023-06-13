package com.students.eventbus;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

public interface EventBusSender {

  Single<JsonObject> sendClassInfoRequest(String classId);

  Single<JsonObject> sendUpdateClassRequest(String id, JsonObject clazzJson);

}
