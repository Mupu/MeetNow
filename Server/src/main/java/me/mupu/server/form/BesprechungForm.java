package me.mupu.server.form;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
public class BesprechungForm {

    @NotBlank
    @Size(min=2, max=32)
    private String thema;

    @NotNull
    private int raumId = -1;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @FutureOrPresent
    @NotNull
    private LocalDateTime zeitraumStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Future
    @NotNull
    private LocalDateTime  zeitraumEnde;

    @NotNull
    private String[] invitedUsers;

    @NotNull
    private String[] chosenItems;

    @NotNull
    private String[] chosenItemsCount;
}
