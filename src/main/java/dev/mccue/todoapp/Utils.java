package dev.mccue.todoapp;

import dev.mccue.json.Json;
import org.microhttp.Request;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class Utils {
    private Utils() {}

    public static Json todoJson(Request request, ResultSet rs) throws SQLException {
        return Json.objectBuilder()
                .put("title", rs.getString("title"))
                .put("completed", rs.getBoolean("completed"))
                .put("url", "https://" + request.header("host") + "/" + rs.getInt("id"))
                .put("order", rs.getInt("order"))
                .build();
    }
}
