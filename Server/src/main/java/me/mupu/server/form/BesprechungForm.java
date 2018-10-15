package me.mupu.server.form;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

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
    private Date zeitraumStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Future
    @NotNull
    private Date  zeitraumEnde;

    @NotNull
    private String[] invitedUsers;

    @NotNull
    private String[] chosenItemsCount;
}
