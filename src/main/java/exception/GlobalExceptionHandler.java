package exception;
import exception.DataNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public String handleNotFound(DataNotFoundException e, HttpServletRequest request) {
        String uri = request.getRequestURI();

        // ASCII 상세/수정에서만 메시지를 다르게
        if (uri.startsWith("/ascii/detail/") || uri.startsWith("/ascii/modify/")) {
            return redirectAlert("이미 삭제되었거나 존재하지 않는 작품입니다.", "/ascii/list");
        }

        return redirectAlert("요청한 대상을 찾을 수 없습니다.", "/");
    }

    private String redirectAlert(String msg, String next) {
        String m = URLEncoder.encode(msg, StandardCharsets.UTF_8);
        String n = URLEncoder.encode(next, StandardCharsets.UTF_8);
        return "redirect:/alert?msg=" + m + "&next=" + n;
    }
}
