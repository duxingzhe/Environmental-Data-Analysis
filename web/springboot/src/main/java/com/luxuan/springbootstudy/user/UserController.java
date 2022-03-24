package com.luxuan.springbootstudy.user;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/users")
    public List<User> getList() {
        return userRepository.findAll();
    }

    @PostMapping(value= "/api/addUser")//改和增
    public User addUser(@RequestParam(value="name", required=true) String name,
                        @RequestParam(value="email", required=true) String email,
                        @RequestParam(value="telephone", required=true) String telephone,
                        @RequestParam(value="job", required=true) String job,
                        @RequestParam(value="age", required=true) String age) {
        User newUser=new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setTelephone(telephone);
        newUser.setJob(job);
        newUser.setAge(age);
        return userRepository.save(newUser);
    }

    @PostMapping(value="/api/editUser")
    public User editUser(
            @RequestParam(value="uid", required=true) Integer uid,
            @RequestParam(value="name", required=true) String name,
            @RequestParam(value="email", required=true) String email,
            @RequestParam(value="telephone", required=true) String telephone,
            @RequestParam(value="job", required=true) String job,
            @RequestParam(value="age", required=true) String age
            ){
        User editedBean=userRepository.findById(uid).orElse(null);
        if(editedBean!=null){
            editedBean.setName(name);
            editedBean.setEmail(email);
            editedBean.setTelephone(telephone);
            editedBean.setJob(job);
            editedBean.setAge(age);
            userRepository.save(editedBean);
        }
        return editedBean;
    }

    @PostMapping(value = "/api/deleteUser")//删
    public void deleteUser(@RequestParam(value="uid", required=true) Integer uid) {
        userRepository.deleteById(uid);
    }
}
