package api.gossip.uz.exception;

import api.gossip.uz.service.ResourceBundleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler2 {

    private final ResourceBundleService bundleService;
    private final ObjectMapper objectMapper;

    public ResponseEntity<GlobalExceptionResponse> globalExceptionResponseResponseEntity(GlobalExceptionResponse problem, Exception exception, HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (401 != problem.getStatus() && 404 != problem.getStatus() && (problem.getErrorCode() == null || 400 != problem.getErrorCode()) && !(exception instanceof BadCredentialsException)) {
            log.info("Path: {}, Problem: {}, Method: {}", path, problem, method);

        }
        if (500 == problem.getStatus()) {
            problem.setTitle(bundleService.getMessage("internal.server.error"));
            problem.setDetail(bundleService.getMessage("internal.server.error"));
            problem.setMessage(bundleService.getMessage("internal.server.error"));
        }
        return ResponseEntity
                .status(problem.getStatus())
                .body(problem);
    }


    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<GlobalExceptionResponse> handleNotFound(EntityNotFound ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(404)
                .title(ex.getLocalizedMessage())
                .detail(ex.getMessage())
                .build();
        return globalExceptionResponseResponseEntity(problem, ex, request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GlobalExceptionResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(400)
                .title(ex.getMessage())
                .detail(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .solution(ex.getSolution())
                .path(request.getRequestURI())
                .build();
        return globalExceptionResponseResponseEntity(problem, ex, request);
    }

    public ResponseEntity<GlobalExceptionResponse> handler(HttpClientErrorException ex, HttpServletRequest request) {
        try {
            Map<String, Object> response = objectMapper.readValue(ex.getResponseBodyAsString(StandardCharsets.UTF_8), new TypeReference<>() {
            });
            return globalExceptionResponseResponseEntity(GlobalExceptionResponse.builder()
                    .title(String.valueOf(response.getOrDefault("title", "Произошла ошибка при запросе")))
                    .detail(String.valueOf(response.getOrDefault("detail", "Произошла ошибка при запросе")))
                    .status(ex.getStatusCode().value())
                    .build(), ex, request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
