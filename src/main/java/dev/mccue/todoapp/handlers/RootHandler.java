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
    /**
     * @param handlers   The list of handlers, in the order they should be tried.
     * @param notHandled The {@link IntoResponse} to use if no handler matches the request.
     */
    public RootHandler(List<Handler> handlers, IntoResponse notHandled) {
        super(handlers, notHandled);
    }

    @Override
    public IntoResponse handle(Request request) throws Exception {
        var response = super.handle(request);
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
