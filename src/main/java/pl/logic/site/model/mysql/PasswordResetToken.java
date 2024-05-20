package pl.logic.site.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Immutable;

import java.util.Calendar;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "password_reset_token")
public class PasswordResetToken {
    private static final int EXPIRATION_MINUTES = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "recovery_token")
    private String recoveryToken;

    @OneToOne(targetEntity = SpringUser.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private SpringUser springUser;

    @Column(name = "expiration_date")
    private Date expirationDate;

    public PasswordResetToken(String token, SpringUser springUser) {
        this.recoveryToken = token;
        this.springUser = springUser;
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MINUTE, EXPIRATION_MINUTES);
        this.expirationDate = calendar.getTime();
    }
}
