package com.qazaq.telecom.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/makeSubscip/{id}")
    public void CreateSubscription(@PathVariable Long id, @RequestBody SubscriptionRequest subscriptionRequest){
        subscriptionService.createSubscription(id, subscriptionRequest.getTariff_id());
    }

    @PutMapping("/changeTariff/{id}")
    public void ChangeTariff(@PathVariable Long id, @RequestBody SubscriptionRequest subscriptionRequest){
        subscriptionService.changeTariff(id, subscriptionRequest.getTariff_id());
    }

    @GetMapping("/remaining/{id}")
    public CurrentRemainRequest getRemaining(@PathVariable Long id){
        return subscriptionService.getRemaining(id);
    }

    @PutMapping("/updateTariff/{id}")
    public void updateTariff(@PathVariable Long id){
        subscriptionService.updateTariff(id);
    }

}
