package com.lakshay.redditclone.service;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
//  to send email
import org.springframework.stereotype.Service;
import com.lakshay.redditclone.exception.SpringRedditException;
import com.lakshay.redditclone.model.NotificationEmail;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {
	
	 private final JavaMailSender mailSender;
	    private final MailContentBuilder mailContentBuilder;

	    @Async  // db work and email server api calls will happen in async mode less time faster
	    void sendMail(NotificationEmail notificationEmail) { // this NE class containts all the details we need for an email like subject, recipient, body
	        MimeMessagePreparator messagePreparator = mimeMessage -> {
	            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
	            //we are creating instance of MMH of type MMP, inside instance of MMH we are passing all data
	            messageHelper.setFrom("springreddit@email.com");
	            messageHelper.setTo(notificationEmail.getRecipient()); // mapping these fields from NE object
	            messageHelper.setSubject(notificationEmail.getSubject());
	            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));  // in build method present in MCB we are passing message so build(message)
	        };
	        try {
	            mailSender.send(messagePreparator); //using java mail sender class to send email
	            log.info("Activation email sent!!"); // object of SLF4J of lombok class to create instance of sljf logger object to inject in our class
	        } catch (MailException e) {
	            log.error("Exception occurred when sending mail", e);
	            // created custom exception class to handle our exception
	            throw new SpringRedditException("Exception occurred when sending mail to " + notificationEmail.getRecipient());
	        }
	    }
	

}
