package dev.mccue.todoapp.handlers;

import dev.mccue.json.Json;
import dev.mccue.json.JsonDecoder;
import dev.mccue.json.JsonEncodable;
import dev.mccue.microhttp.handler.RouteHandler;
import dev.mccue.microhttp.json.JsonResponse;
import dev.mccue.todoapp.Utils;
import org.microhttp.Request;
import org.sqlite.SQLiteDataSource;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PostTodoHandler extends RouteHandler {
    private final SQLiteDataSource db;

    public PostTodoHandler(SQLiteDataSource db) {
        super("POST", Pattern.compile("/"));
        this.db = db;
    }

    record Todo(String title, Optional<Integer> order) {
        static Todo fromJson(Json json) {
            return new Todo(
                    JsonDecoder.field(json, "title", JsonDecoder::string),
                    JsonDecoder.optionalField(json, "order", JsonDecoder::int_)
            );
        }
    }

    @Override
    protected JsonResponse handleRoute(Matcher routeMatch, Request request) throws Exception {
        var json = Json.readString(new String(request.body(), StandardCharsets.UTF_8));
        var todo = Todo.fromJson(json);

        try (var conn = db.getConnection();
             var stmt = conn.prepareStatement("""
                     INSERT INTO todo(title, "order")
                     VALUES (?, ?)
                     RETURNING id, title, completed, "order"
                     """)) {
            stmt.setString(1, todo.title);
            stmt.setInt(2, todo.order.orElse(0));
            var rs = stmt.executeQuery();
            rs.next();
            return new JsonResponse(Utils.todoJson(request, rs));
        }
    }
}
