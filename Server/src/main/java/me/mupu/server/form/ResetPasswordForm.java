package me.mupu.server.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ResetPasswordForm {

    @NotBlank
    @Size(min = 4, max = 50)
    private String password;

    @NotBlank
    @Size(min = 4, max = 50)
    private String repeatPassword;

    @AssertTrue     // tells programm to run this method
    public boolean isTest() {   // sets the actual value
        repeatedPasswordIsCorrect = password.equals(repeatPassword);
        return repeatedPasswordIsCorrect;
    }

    @AssertTrue(message = "Passwörter stimmen nicht überein")     // this is the attribute that is tested in the html file
    private boolean repeatedPasswordIsCorrect;  // <-- field name

}
