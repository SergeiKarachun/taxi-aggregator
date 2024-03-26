package by.sergo.passengerservice.security;

import by.sergo.passengerservice.service.exception.ErrorMessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Date;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getOutputStream().println(objectMapper.writerWithDefaultPrettyPrinter().
                writeValueAsString(
                        new ErrorMessageResponse("Forbidden", HttpStatus.FORBIDDEN, new Date())));
    }
}
