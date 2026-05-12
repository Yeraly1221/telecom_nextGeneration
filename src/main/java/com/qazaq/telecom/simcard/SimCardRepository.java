package com.qazaq.telecom.simcard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SimCardRepository extends JpaRepository<SimCard, Long> {


    Optional<SimCard> findSimCardById(Long id);


    Boolean existsSimCardByPhoneNumber(String phoneNumber);
}

