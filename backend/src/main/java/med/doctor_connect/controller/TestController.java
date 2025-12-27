package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.UserDto;
import med.doctor_connect.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final UserService userService;

    @GetMapping("/test")
    public String test() {
        return userService.findUsernameByEmailOrPhone("murad_user@gmail.com")
                .map(UserDto::getEmailOrPhoneNumber)
                .orElse("Not Found");
    }
}
