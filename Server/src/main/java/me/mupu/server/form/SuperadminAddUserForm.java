package me.mupu.server.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SuperadminAddUserForm {

    @NotBlank
    @Size(min=2, max=16)
    private String SAUFvorname;

    @NotBlank
    @Size(min=2, max=16)
    private String SAUFnachname;

    @NotBlank
    @Email
    @Size(min=2, max=32)
    private String SAUFemail;

    @NotBlank
    @Size(min = 4, max = 16)
    private String SAUFbenutzername;

    @NotBlank
    @Size(min = 4, max = 50)
    private String SAUFpasswort;

    String SAUFranks[];
}
