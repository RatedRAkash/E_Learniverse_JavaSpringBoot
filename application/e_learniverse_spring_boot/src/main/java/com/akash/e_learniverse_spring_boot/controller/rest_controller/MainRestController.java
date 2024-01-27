package com.akash.e_learniverse_spring_boot.controller.rest_controller;

import com.akash.e_learniverse_spring_boot.domain.dto.FootballPlayerDto;
import com.akash.e_learniverse_spring_boot.domain.entity.FootballPlayerEntity;
import com.akash.e_learniverse_spring_boot.mapper.CustomObjectMapper;
import com.akash.e_learniverse_spring_boot.response.ApiResponseDto;
import com.akash.e_learniverse_spring_boot.service.FootballPlayerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/")
public class MainRestController {
    private static final Logger logger = LogManager.getLogger(MainRestController.class);

    private CustomObjectMapper<FootballPlayerEntity, FootballPlayerDto> footballPlayerMapper;

    private final FootballPlayerService footballPlayerService;

    @Autowired
    public MainRestController(CustomObjectMapper<FootballPlayerEntity, FootballPlayerDto> footballPlayerMapper, FootballPlayerService footballPlayerService) {
        this.footballPlayerMapper = footballPlayerMapper;
        this.footballPlayerService = footballPlayerService;
    }

    @GetMapping("home")
    public ResponseEntity<?> home() {
        logger.info("Home Api");
        return ResponseEntity.ok(new ApiResponseDto("E-Learniverse Home Api"));
    }


    @PostMapping("create-player")
    public ResponseEntity<?> createPlayer(@RequestBody FootballPlayerDto footballPlayerDto) {
        logger.info("Received: " + footballPlayerDto.toString());

        FootballPlayerEntity playerEntity = footballPlayerMapper.mapFrom(footballPlayerDto);

        try{
            FootballPlayerEntity newSavedFootballPlayer = footballPlayerService.savePlayer(playerEntity);

//        return ResponseEntity.ok(new ApiResponseDto("New Player Created: " + newSavedFootballPlayer.toString()));
            return ResponseEntity.ok(footballPlayerMapper.mapTo(newSavedFootballPlayer));
        }
        catch (Exception ex){
            return ResponseEntity.ok(new ApiResponseDto(ex.toString()));
        }
    }


    @GetMapping("my-profile-info")
    public ResponseEntity<?> getMyPlayerInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //as during saving we used "Username = Email" of the FootballPlayer
        FootballPlayerEntity footballPlayer = footballPlayerService.getFootballPlayerByEmail(authentication.getName());

        if (footballPlayer != null) {
//            return ResponseEntity.ok(new ApiResponseDto("My Player Profile: " + footballPlayer.toString()));
            return ResponseEntity.ok(footballPlayer);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}