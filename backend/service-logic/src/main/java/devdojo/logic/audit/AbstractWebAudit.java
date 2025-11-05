package devdojo.logic.audit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public abstract class AbstractWebAudit implements IWebAudit {

  public final static @NotNull String TIMESTAMP_REQUEST_PROCESS_BEGIN = "req-begin";

  /**
   * Executed before the controller handles the request.
   *
   * @param request  The HTTP request being processed.
   * @param response The HTTP response being generated.
   * @param handler  The handler for the request (controller method).
   * @return true if the execution chain should proceed with the next interceptor or the handler itself.
   */
  @Override
  public boolean preHandle(@NotNull final HttpServletRequest request,
                           @NotNull final HttpServletResponse response,
                           @NotNull final Object handler) {
    // Set the current timestamp as the start time for request processing
    request.setAttribute(TIMESTAMP_REQUEST_PROCESS_BEGIN, System.currentTimeMillis());

    //Set logger placeholder
    MDC.put("method", request.getMethod());
    MDC.put("uri", request.getRequestURI());

    return true;
  }

  /**
   * Executed after the request has been processed by the controller.
   *
   * @param request   The HTTP request being processed.
   * @param response  The HTTP response being generated.
   * @param handler   The handler for the request (controller method).
   * @param exception Any exception that occurred during processing, or null if none.
   */
  @Override
  public void afterCompletion(@NotNull final HttpServletRequest request,
                              @NotNull final HttpServletResponse response,
                              @NotNull final Object handler,
                              final Exception exception) {
    // Calculate the duration of request processing
    long duration = Optional
      .ofNullable((Long) request.getAttribute(TIMESTAMP_REQUEST_PROCESS_BEGIN))
      .map(start -> System.currentTimeMillis() - start)
      .orElse(-1L);

    // Log the request processing duration and status
    log.info("[{}]'{}' took a total of {} ms. (status={}, contentType={})",
      request.getMethod(),
      request.getRequestURI(),
      duration,
      response.getStatus(),
      response.getContentType());

    //Clear log finger print, prevent memory leaks.
    MDC.clear();
    log.debug("MDC content was cleared successfully.");
  }
}
