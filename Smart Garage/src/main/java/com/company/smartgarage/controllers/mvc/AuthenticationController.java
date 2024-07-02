package com.company.smartgarage.controllers.mvc;

import com.company.smartgarage.exceptions.AuthorizationException;
import com.company.smartgarage.exceptions.EntityDuplicateException;
import com.company.smartgarage.exceptions.EntityNotFoundException;
import com.company.smartgarage.helpers.AuthenticationHelper;
import com.company.smartgarage.helpers.LinkUtil;
import com.company.smartgarage.helpers.UserMapper;
import com.company.smartgarage.models.User;
import com.company.smartgarage.models.dtos.LoginDto;
import com.company.smartgarage.models.dtos.RegisterDto;
import com.company.smartgarage.services.contracts.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import net.bytebuddy.utility.RandomString;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationHelper helper;

    private final JavaMailSender mailSender;

    private final UserService userService;

    private final UserMapper userMapper;


    public AuthenticationController(AuthenticationHelper helper,
                                    JavaMailSender mailSender,
                                    UserService userService,
                                    UserMapper userMapper) {
        this.helper = helper;
        this.mailSender = mailSender;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("loggedUser") != null;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        return "LoginView";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") LoginDto dto,
                              BindingResult bindingResult,
                              HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "LoginView";
        }
        try {
            User user = helper.verifyAuthentication(dto.getUsername(), dto.getPassword());
            session.setAttribute("loggedUser", user.getUsername());
            session.setAttribute("userRole", user.getUserRole());
            session.setAttribute("userStatus", user.getUserStatus());
            session.setAttribute("userId", user.getUserId());
            return "redirect:/";
        } catch (AuthorizationException e) {
            bindingResult.rejectValue("username", "auth_error", e.getMessage());
            return "LoginView";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("register", new RegisterDto());
        return "RegisterView";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("register") RegisterDto register,
                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "RegisterView";
        }

        if (!register.getPassword().equals(register.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password.");
            return "RegisterView";
        }
        try {
            User user = userMapper.registerUserFromDto(register);
            userService.create(user);
            return "redirect:/auth/login";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("username", "username_error", e.getMessage());
            return "RegisterView";
        }
    }

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "ForgotPasswordView";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            userService.updateResetToken(token, email);
            String resetPasswordLink = LinkUtil.getSiteURL(request) + "/auth/reset_password/" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error while sending email");
        }

        return "ForgotPasswordView";
    }

    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("team13techgarage@gmail.com", "Smart Garage Support");
        helper.setTo(recipientEmail);

        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<p>Note the link will be valid only 1 hour!</p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/reset_password/{token}")
    public String showResetPasswordForm(@PathVariable("token") String token, Model model) {
        model.addAttribute("token", token);

        try {
            userService.findByResetToken(token);
            return "ResetPasswordView";
        } catch (EntityNotFoundException | AuthorizationException e) {
            model.addAttribute("message", e.getMessage());
            return "redirect:/";
        }

    }

    @PostMapping("/reset_password/{token}")
    public String processResetPassword(@PathVariable("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model) {

        model.addAttribute("match", password.equals(confirmPassword));

        try {
            if (!password.equals(confirmPassword)) {
                model.addAttribute("message", "Passwords do not match");
                return "ResetPasswordView";
            }

            User user = userService.findByResetToken(token);
            userService.updatePassword(user, password);
            model.addAttribute("message", "You have successfully changed your password.");
            return "ResetPasswordView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("message", "Invalid Token");
            return "ResetPasswordView";
        } catch (AuthorizationException e) {
            model.addAttribute("message", e.getMessage());
            return "LoginView";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("loggedUser");
        session.removeAttribute("userRole");
        session.removeAttribute("userStatus");
        return "redirect:/";
    }



}
