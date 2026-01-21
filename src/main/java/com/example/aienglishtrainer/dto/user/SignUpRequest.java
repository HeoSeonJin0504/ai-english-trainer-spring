package com.example.aienglishtrainer.dto.user;

import com.example.aienglishtrainer.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 50, message = "아이디는 4~50자 사이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문, 숫자, 언더스코어만 가능합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "핸드폰 번호는 필수입니다.")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 핸드폰 번호 형식이 아닙니다.")
    private String phone;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;  // NULL 허용

    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @NotNull(message = "나이는 필수입니다.")
    @Min(value = 1, message = "나이는 1세 이상이어야 합니다.")
    @Max(value = 150, message = "올바른 나이를 입력해주세요.")
    private Integer age;
}