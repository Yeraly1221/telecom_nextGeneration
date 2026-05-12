package com.qazaq.telecom.tariff;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Long> {


    Optional<Tariff> findTariffById(Long id);
}
