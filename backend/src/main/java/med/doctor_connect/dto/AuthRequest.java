package med.doctor_connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthRequest(@JsonProperty String emailOrPhoneNumber, @JsonProperty String password) {
}
