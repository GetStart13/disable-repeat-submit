package com.fqq.disablerepeatsubmit.controller;


import com.fqq.disablerepeatsubmit.annotations.RepeatSubmit;
import com.fqq.disablerepeatsubmit.domain.Student;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/index")
public class SubmitController {

    @RepeatSubmit(interval = 10000, massage = "禁止重复提交！")
    @GetMapping("/submit")
    public String submitResult(Student student) {
        System.out.println(student.getId() + " " + student.getName());
        return "submit success!";
    }
}
