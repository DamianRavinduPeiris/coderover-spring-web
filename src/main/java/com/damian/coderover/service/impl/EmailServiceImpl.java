package com.damian.coderover.service.impl;

import com.damian.coderover.dto.ReportEmailDTO;
import com.damian.coderover.response.Response;
import com.damian.coderover.service.EmailService;
import com.damian.coderover.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailServiceImpl implements EmailService {

    private static final String COOKIE_NAME = "access_token";
    private static final String CLAIM_EMAIL = "email";

    private final JwtUtils jwtUtils;
    private final HttpServletRequest request;

    @Value("${mail.from:}")
    private String from;

    @Value("${spring.mail.host}")
    private String smtpHost;

    @Value("${spring.mail.port}")
    private int smtpPort;

    @Value("${spring.mail.username}")
    private String smtpUsername;

    @Value("${spring.mail.password}")
    private String smtpPassword;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}")
    private boolean smtpStartTls;

    @Value("${spring.mail.properties.mail.smtp.starttls.required:false}")
    private boolean smtpStartTlsRequired;

    @Value("${spring.mail.properties.mail.smtp.auth:true}")
    private boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.connectiontimeout:0}")
    private int connectionTimeoutMs;

    @Value("${spring.mail.properties.mail.smtp.timeout:0}")
    private int readTimeoutMs;

    @Value("${spring.mail.properties.mail.smtp.writetimeout:0}")
    private int writeTimeoutMs;

    @Value("${spring.mail.properties.mail.smtp.ssl.trust:}")
    private String sslTrust;

    @Override
    public ResponseEntity<Response> sendReport(ReportEmailDTO dto) {
        var token = extractTokenFromCookies();
        if (token == null) {
            log.debug("No access_token cookie found in request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Response("Invalid token,Please Re-Authenticate!", null, HttpStatus.UNAUTHORIZED.value()));
        }
        final Claims claims;
        try {
            claims = jwtUtils.parseJwt(token);
            log.debug("Parsed JWT claims: {}", claims);
        } catch (JwtException ex) {
            log.warn("JWT parse failed: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Response("Invalid token,Please Re-Authenticate!", null, HttpStatus.UNAUTHORIZED.value()));
        }
        var toEmail = claims.get(CLAIM_EMAIL, String.class);
        log.debug("Extracted recipient email from JWT: {}", toEmail);
        if (toEmail == null || toEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response("Email claim not found in token", null, HttpStatus.BAD_REQUEST.value()));
        }

        var subject = "Your CodeRover Report.";
        var body = buildBody(dto);

        log.debug("Preparing to send email. To: {}, Subject: {}, Body:\n{}", toEmail, subject, body);

        try {
            sendMimeEmail(toEmail, subject, body);
            log.info("Email sent successfully to {}", toEmail);
            return ResponseEntity.ok(new Response("Report email sent successfully", null, HttpStatus.OK.value()));
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("Failed to send email", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    private void sendMimeEmail(String toEmail, String subject, String textBody) throws MessagingException {
        var props = getProperties();

        Authenticator authenticator = smtpAuth
                ? new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        }
                : null;

        var session = Session.getInstance(props, authenticator);

        var message = new MimeMessage(session);
        var effectiveFrom = (from != null && !from.isBlank()) ? from : smtpUsername;
        if (effectiveFrom != null && !effectiveFrom.isBlank()) {
            message.setFrom(new InternetAddress(effectiveFrom));
        }
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(textBody);

        log.debug("Final email message ready. From={}, To={}, Subject={}", effectiveFrom, toEmail, subject);

        Transport.send(message);
    }

    private Properties getProperties() {
        var props = new Properties();
        props.put("mail.smtp.auth", String.valueOf(smtpAuth));
        props.put("mail.smtp.starttls.enable", String.valueOf(smtpStartTls));
        props.put("mail.smtp.starttls.required", String.valueOf(smtpStartTlsRequired));
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
        if (connectionTimeoutMs > 0) props.put("mail.smtp.connectiontimeout", String.valueOf(connectionTimeoutMs));
        if (readTimeoutMs > 0) props.put("mail.smtp.timeout", String.valueOf(readTimeoutMs));
        if (writeTimeoutMs > 0) props.put("mail.smtp.writetimeout", String.valueOf(writeTimeoutMs));
        if (sslTrust != null && !sslTrust.isBlank()) props.put("mail.smtp.ssl.trust", sslTrust);
        return props;
    }

    private String extractTokenFromCookies() {
        var cookies = request.getCookies();
        if (cookies == null) {
            log.debug("No cookies present in request");
            return null;
        }
        for (Cookie cookie : cookies) {
            log.debug("Inspecting cookie: {}={}", cookie.getName(), cookie.getValue());
            if (COOKIE_NAME.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    private String buildBody(ReportEmailDTO dto) {
        var sb = new StringBuilder();
        sb.append("Hello,\n\nHere is your report from CodeRover:\n\n");
        appendSection(sb, "Defects", dto.getDefects());
        appendSection(sb, "Performance", dto.getPerformance());
        appendSection(sb, "Vulnerabilities", dto.getVulnerabilities());
        sb.append("\nRegards,\nCodeRover Team.");
        return sb.toString();
    }

    private void appendSection(StringBuilder sb, String title, String[] items) {
        sb.append(title).append(":\n");
        if (items == null || items.length == 0) {
            sb.append("  - None\n\n");
            return;
        }
        for (String item : items) {
            sb.append("  - ").append(item).append("\n");
        }
        sb.append("\n");
    }
}
