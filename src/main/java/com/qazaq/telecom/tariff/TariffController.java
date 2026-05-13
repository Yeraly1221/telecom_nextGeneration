package com.qazaq.telecom.tariff;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/tariff")
@RequiredArgsConstructor
public class TariffController {
    private final TariffService tariffService;

    @PostMapping("/addTariff")
    public void AddTariff(@RequestBody AddTariffRequest addTariffRequest) {
        tariffService.addTariff(addTariffRequest);
    }
}
