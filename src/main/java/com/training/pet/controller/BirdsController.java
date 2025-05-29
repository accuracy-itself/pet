package com.training.pet.controller;

import com.training.pet.service.BirdsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/birds")
public class BirdsController {

    @Resource
    private BirdsService birdsService;

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/identify")
    public String identifyBird(@RequestParam("image") MultipartFile file) {
        return birdsService.identifyBird(file);
    }

}
