package dev.mccue.todoapp.handlers;

import dev.mccue.microhttp.handler.DelegatingHandler;
import dev.mccue.microhttp.handler.Handler;
import dev.mccue.microhttp.handler.IntoResponse;
import org.microhttp.Header;
import org.microhttp.Request;
import org.microhttp.Response;

import java.util.ArrayList;
import java.util.List;

public final class RootHandler extends DelegatingHandler {
    public RootHandler(List<Handler> handlers, IntoResponse notHandled) {
        super(handlers, notHandled);
    }

    @Override
    public IntoResponse handle(Request request) throws Exception {
        IntoResponse response;
        if (request.method().equalsIgnoreCase("options")) {
            response = () -> new Response(
                    200, "OK", List.of(
                    new Header("access-control-allow-headers", "*"),
                    new Header("access-control-allow-methods", "*")
            ), new byte[] {});
        }
        else {
            response = super.handle(request);
        }

        return () -> {
            var actualResponse = response.intoResponse();
            var headers = new ArrayList<>(actualResponse.headers());
            headers.add(new Header("access-control-allow-origin", "*"));
            return new Response(
                    actualResponse.status(),
                    actualResponse.reason(),
                    headers,
                    actualResponse.body()
            );
        };
    }
}
