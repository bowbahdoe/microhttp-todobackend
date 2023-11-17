package dev.mccue.todoapp;

import dev.mccue.json.Json;
import dev.mccue.json.JsonEncodable;
import org.microhttp.Request;

public record TodoUrl(Request request, int id) implements JsonEncodable {
    @Override
    public Json toJson() {
        return Json.of("https://" + request.header("host") + "/" + id);
    }
}
