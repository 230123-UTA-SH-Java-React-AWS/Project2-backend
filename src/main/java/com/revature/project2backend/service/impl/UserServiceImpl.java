package com.revature.project2backend.service.impl;

import com.revature.project2backend.dto.AuthResponseDto;
import com.revature.project2backend.dto.LoginDto;
import com.revature.project2backend.dto.RegisterDto;
import com.revature.project2backend.dto.UserDto;
import com.revature.project2backend.exception.EmailConfirmationException;
import com.revature.project2backend.exception.LoginNotValidException;
import com.revature.project2backend.exception.RegisterNotValidException;
import com.revature.project2backend.model.EmailToken;
import com.revature.project2backend.model.UserEntity;
import com.revature.project2backend.repository.UserRepository;
import com.revature.project2backend.security.JwtGenerator;
import com.revature.project2backend.service.EmailSender;
import com.revature.project2backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final EmailTokenServiceImpl emailTokenService;
    private final EmailSender emailSender;


    @Autowired
    public UserServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator, EmailTokenServiceImpl emailTokenService, EmailSender emailSender) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.emailTokenService = emailTokenService;
        this.emailSender = emailSender;
    }

    //TODO: clean up response to include user details like username, wins, and profile photo
    public Optional<UserDto> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String userEmail = ((UserDetails) principal).getUsername();
            Optional<UserEntity> user = userRepository.findByEmail(userEmail);
            if (user.isPresent()) {
                UserDto userDTO = new UserDto();
                userDTO.setEmail(user.get().getEmail());
                userDTO.setUsername(user.get().getUsername());
                return Optional.of(userDTO);
            }
        }
        return Optional.empty();
    }

    
    public boolean isUsernameUnique(String username) {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        return !user.isPresent();
    }


    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    //TODO: include user details like username, wins, and profile photo
    @Override
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        if(Boolean.TRUE.equals((userRepository.existsByEmail(loginDto.getEmail())))){
            Optional<UserEntity> user = userRepository.findByEmail(loginDto.getEmail());
            if (user.isPresent() && passwordEncoder.matches( loginDto.getPassword(),user.get().getPassword())) {
                if(user.get().getEnabled()) {
                    // authenticate user with email & password that's passed to the authentication manager in Spring
                    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
                    // store authenticated user in Spring Security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // generate jwt token for the user and send it as a response
                    String jwtToken = jwtGenerator.generateJwtToken(authentication);
                    return new ResponseEntity<>(new AuthResponseDto(user.get().getUsername(), loginDto.getEmail(), jwtToken), HttpStatus.OK);
                }else{
                    // save confirmation token
                    String token  = UUID.randomUUID().toString();
                    EmailToken emailToken = new EmailToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15),user.get());
                    emailTokenService.saveEmailToken(emailToken);

                    //send email to confirm
                    String link = "http://stephens-blackjack.eastus.cloudapp.azure.com:4798/api/auth/confirm?token=" + token;
                    emailSender.send(user.get().getEmail(), buildEmail(user.get().getUsername(), link));
                    throw new LoginNotValidException("Email was not confirmed! Please check your email for a confirmation link.");
                }
            } else{
                throw new LoginNotValidException("Invalid password!");
            }
        } else{
            throw new LoginNotValidException("Email doesn't exist!");
        }
    }

    @Override
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        // check if email already exists
        if(Boolean.TRUE.equals(userRepository.existsByEmail(registerDto.getEmail()))){
            throw new RegisterNotValidException("Email is already in use!");
        }
        // check if the username already exists
        if(Boolean.TRUE.equals(userRepository.existsByUsername(registerDto.getUsername()))){
            throw new RegisterNotValidException("Username is already in use!");
        }
        // create user instance and set credentials
        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        // hash the password then set it
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // save the user to the database
        userRepository.save(user);

        // save confirmation token
        String token  = UUID.randomUUID().toString();
        EmailToken emailToken = new EmailToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15),user);
        emailTokenService.saveEmailToken(emailToken);

        //send email to confirm
        String link = "http://stephens-blackjack.eastus.cloudapp.azure.com:4798/api/auth/confirm?token=" + token;
        emailSender.send(user.getEmail(), buildEmail(user.getUsername(), link));

        return new ResponseEntity<>("User registered. PLease confirm your email by clicking the link sent to: " + user.getEmail(),HttpStatus.OK);

    }

    @Transactional
    public String confirmEmailToken(String token) {
        EmailToken emailToken = emailTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new EmailConfirmationException("Token not found!"));

        if (emailToken.getConfirmedAt() != null) {
            throw new EmailConfirmationException("Email already confirmed!");
        }

        LocalDateTime expiredAt = emailToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new EmailConfirmationException("Token expired! Please attempt to login to send a new confirmation email.");
        }

        emailTokenService.setConfirmedAt(token);
        userRepository.enableUser(
                emailToken.getUser().getEmail());
        return "User confirmed!";
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#FF0000\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}


/**
 * UserServiceImpl Class Documentation
 * This class is an implementation of the UserService interface and contains methods for user management, including registration, login, and retrieving user details. It uses Spring Security for authentication and JWT for token generation.
 *
 * Class Dependencies
 * AuthenticationManager: For managing user authentication.
 * UserRepository: For accessing the User database.
 * PasswordEncoder: For encoding and decoding user passwords.
 * JwtGenerator: For generating JWT tokens.
 * Constructor
 * The constructor takes the following parameters:
 *
 * AuthenticationManager authenticationManager
 * UserRepository userRepository
 * PasswordEncoder passwordEncoder
 * JwtGenerator jwtGenerator
 * These parameters are used to initialize the respective class properties.
 *
 * Methods
 * getCurrentUser()
 * This method returns the current authenticated user's details wrapped in an Optional<UserDto> object. If no user is authenticated, it returns an empty Optional.
 *
 * TODO: clean up response to include user details like username, wins, and profile photo.
 *
 * login(@RequestBody LoginDto loginDto)
 * This method takes a LoginDto object containing the user's email and password. It checks if the provided email exists in the repository, then validates the user's password. If the credentials are valid, it returns an AuthResponseDto object containing the user's username, email, and JWT token wrapped in a ResponseEntity with an HTTP status code of HttpStatus.OK. If either the email or password is invalid, a LoginNotValidException is thrown.
 *
 * TODO: include user details like username, wins, and profile photo.
 *
 * register(@RequestBody RegisterDto registerDto)
 * This method takes a RegisterDto object containing the user's desired username, email, and password. It checks if the provided email and username already exist in the repository. If either is already in use, a RegisterNotValidException is thrown. Otherwise, a new UserEntity is created, its password is hashed, and the user is saved in the database. Finally, it returns a ResponseEntity<String> containing a success message and an HTTP status code of HttpStatus.OK.
 * */