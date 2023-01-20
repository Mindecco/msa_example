package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.service.UserServiceimpl;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class UserController {
    private Environment env;
    private Greeting greeting;
    private UserService userService;


    @Autowired
    public UserController(Environment env,Greeting greeting,UserService userService) {
        this.env = env;
        this.greeting = greeting;
        this.userService = userService;
    }

    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in User Service , port(local.service.port)="+ env.getProperty("local.server.port")
                +",port(server.port)="+ env.getProperty("server.port")
                +",with token secret="+ env.getProperty("token.secret")
                +",with token time="+ env.getProperty("token.expiration_time")
        );
    }

    @GetMapping("/welcome")
    public String welcome(){
        return greeting.getMessage();
    }

    @PostMapping("users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);

        //userDto.setEam
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers(){
        Iterable<UserEntity> userlist = userService.getUserByAll();

        List<ResponseUser> returnUserList = new ArrayList<>();

        userlist.forEach(v->{
            returnUserList.add(new ModelMapper().map(v,ResponseUser.class));
        });
        return ResponseEntity.status(HttpStatus.OK).body(returnUserList);

    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId")String userId){
        UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser responseUser = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(responseUser);

    }
}

