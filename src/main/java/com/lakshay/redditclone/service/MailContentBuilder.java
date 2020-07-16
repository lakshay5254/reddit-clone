package com.lakshay.redditclone.service;

// email content builder combining message with template
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class MailContentBuilder {

    private final TemplateEngine templateEngine;

    public String build(String message) {  //takes msg we want to send to user
        Context context = new Context(); // tymeleafs context object to set email message
        context.setVariable("message", message);
        return templateEngine.process("mailTemplate", context);  //(file name, context) tyme leaf will automatically add message to email method
    }
}