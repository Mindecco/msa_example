package com.example.userservice.service;


import com.example.userservice.dto.UserDto;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Primary
public class UserServiceimpl implements UserService{


    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceimpl(@Lazy UserRepository userRepository,@Lazy BCryptPasswordEncoder passwordEncoder ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }
    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper modelmapper = new ModelMapper();
        modelmapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelmapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
        userRepository.save(userEntity);

        UserDto returnUserDto = modelmapper.map(userRepository, UserDto.class);

        return returnUserDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {

        UserEntity  userEntity   = userRepository.findByUserId(userId)
                .orElseThrow(()->new UserNotFoundException(String.format("ID[%s] not found",userId)));


        UserDto returnUserDto = new ModelMapper().map(userEntity, UserDto.class);

        List<ResponseOrder> orders = new ArrayList<>();
        returnUserDto.setOrders(orders);
        return returnUserDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String userName) {
        UserEntity userEntity = userRepository.findByEmail(userName);
        if(userEntity == null)
            throw new UsernameNotFoundException(userName);
        UserDto userDto = new ModelMapper().map(userEntity,UserDto.class);

        return userDto;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);
        if(userEntity == null)
            throw new UsernameNotFoundException(username);
        return new User(userEntity.getEmail(),userEntity.getEncryptedPwd(),true
                ,true
                ,true
                ,true
                ,new ArrayList<>());
    }



}
