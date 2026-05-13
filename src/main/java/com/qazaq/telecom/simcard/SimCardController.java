package com.qazaq.telecom.simcard;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simcard")
@RequiredArgsConstructor
public class SimCardController {

    private final SimCardService simCardService;

    @PostMapping("/getSimCard/{id}")
    public void GetSimCard(@PathVariable Long id, @RequestBody GetSimCardRequest getSimCardRequest){
        simCardService.getSimCard(id, getSimCardRequest);
    }

    @GetMapping("/getPhoneNumber/{id}")
    public GetPhoneNumberRequest getPhoneNumber(Long id){
        return simCardService.getPhoneNumber(id);
    }


}
