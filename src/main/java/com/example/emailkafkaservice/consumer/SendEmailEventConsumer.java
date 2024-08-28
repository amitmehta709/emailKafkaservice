package com.example.emailkafkaservice.consumer;

import com.example.emailkafkaservice.dto.SendEmailEventDto;
import com.example.emailkafkaservice.utils.EmailUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class SendEmailEventConsumer {

    private ObjectMapper objectMapper;

    public SendEmailEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "sendEmail", groupId = "emailService")
    public void consumeSendEmailEvent(String message) {
        try
        {
            System.out.println("Received email event: " + message);
            SendEmailEventDto emailEventDto = objectMapper.readValue(message, SendEmailEventDto.class);

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
            props.put("mail.smtp.port", "587"); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

            //create Authenticator object to pass in Session.getInstance argument
            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("testuserkafka@gmail.com", "Test@123$");
                }
            };

            Session session = Session.getInstance(props, auth);

            EmailUtil.sendEmail(session, emailEventDto.getTo(), emailEventDto.getSubject(), emailEventDto.getBody());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
