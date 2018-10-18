package me.mupu.server.service;

import me.mupu.server.form.RegistrationForm;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.PersonRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

import static org.jooq.generated.Tables.PERSON;

@Service("registrationService")
public class RegistrationService {

	@Autowired
	private DSLContext dslContext;

	@Autowired
	private EmailService emailService;

	public PersonRecord registerUser(@Valid RegistrationForm registrationForm, HttpServletRequest request) {
		PersonRecord person;
		try {
			// add person to database
			person = dslContext.insertInto(PERSON)
					.columns(PERSON.VORNAME,
							PERSON.NACHNAME,
							PERSON.EMAIL,
							PERSON.TOKEN)
					.values(registrationForm.getVorname(),
							registrationForm.getNachname(),
							registrationForm.getEmail(),
							UUID.randomUUID().toString())
					.returning().fetchOne();

			String appUrl = request.getScheme() + "://" + request.getServerName();

			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(person.getEmail());
			mail.setSubject("Registration Confirmation");
			mail.setText("To confirm your e-mail address, please click the link below:\n"
					+ appUrl + "/confirmation?token=" + person.getToken());
			mail.setFrom("noreply@domain.com");

			emailService.sendEmail(mail);
		} catch (Exception e) {
			person = null;
		}
		return person;
	}

}
