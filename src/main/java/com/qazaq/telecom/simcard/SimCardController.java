package com.qazaq.telecom.simcard;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/simcard")
@RequiredArgsConstructor
public class SimCardController {

    private final SimCardService simCardService;

    @PostMapping("/makeSimCard/{customerId}")
    public void MakeSimCard(@PathVariable Long customerId, @Valid @RequestBody GetSimCardRequest getSimCardRequest){
        simCardService.makeSimCard(customerId, getSimCardRequest);
    }

    @GetMapping("/getPhoneNumber/{id}")
    public GetPhoneNumberRequest getPhoneNumber(@PathVariable Long id){
        return simCardService.getPhoneNumber(id);
    }


}
