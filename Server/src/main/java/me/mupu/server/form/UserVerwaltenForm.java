package me.mupu.server.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserVerwaltenForm {

    @NotBlank
    @Size(min = 2, max = 16)
    String vorname;

    @NotBlank
    @Size(min = 2, max = 16)
    String nachname;

    @NotBlank
    @Email
    @Size(min = 2, max = 32)
    String email;

    @NotBlank
    @Size(min = 2, max = 16)
    String benutzername;

    @Size(max = 16)
    String passwort;

    String ranks[];
}
