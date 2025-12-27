package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoRoomDto {
    private String id;
    private String appointmentId;
    private String meetUrl;
    private String meetingCode;
    private String conferenceId;
    private boolean isActive;
    private Date expiresAt;
    private Date createdAt;
}
