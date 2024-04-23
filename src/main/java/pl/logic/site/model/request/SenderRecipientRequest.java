package pl.logic.site.model.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SenderRecipientRequest {
    @NotNull
    private int senderId;
    private int recipientId;
}
