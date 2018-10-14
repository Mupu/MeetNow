package me.mupu.server.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RegistrationForm {

    @NotBlank
    @Size(min=2, max=16)
    private String vorname;

    @NotBlank
    @Size(min=2, max=16)
    private String nachname;

    @NotBlank
    @Email
    @Size(min=2, max=32)
    private String email;

}
