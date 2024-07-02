package com.company.smartgarage.helpers;


import com.company.smartgarage.models.User;
import com.company.smartgarage.models.dtos.RegisterDto;
import com.company.smartgarage.models.dtos.UserDto;
import com.company.smartgarage.models.enums.UserRole;
import com.company.smartgarage.models.enums.UserStatus;
import com.company.smartgarage.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserService userService;

    @Autowired
    public UserMapper(UserService userService) {
        this.userService = userService;
    }


    public User fromDto(int id, UserDto userDto){
        User user = userService.getById(id);
        user.setUserId(id);
        user.setUserStatus(userDto.getUserStatus());
        user.setUserRole(userDto.getUserRole());
        user.setUsername(userDto.getUsername());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setNames(userDto.getNames());
        return user;
    }



    public User fromDto(UserDto userDto) {
        User user = new User();
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserRole(UserRole.CUSTOMER);
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setNames(userDto.getNames());

        hashPassword(user, userDto.getPassword());

        return user;
    }

    public void hashPassword(User user, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPassword(hashedPassword);
    }


    public UserDto toDto(User user){
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setNames(user.getNames());
        userDto.setUserRole(user.getUserRole());
        userDto.setUserStatus(user.getUserStatus());
        return userDto;
    }

    public User registerUserFromDto(RegisterDto registerDto){
        User user = new User();
        user.setNames(registerDto.getNames());
        user.setEmail(registerDto.getEmail());
        user.setUsername(registerDto.getUsername());
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setUserStatus(UserStatus.ACTIVE);
        user.setUserRole(UserRole.CUSTOMER);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(registerDto.getPassword());
        user.setPassword(encodedPassword);

        return user;
    }



}
