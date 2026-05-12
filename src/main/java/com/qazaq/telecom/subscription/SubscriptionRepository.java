package com.qazaq.telecom.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Boolean existsSubscriptionsBySimCardId(Long id);


    Optional<Subscription> findSubscriptionById(Long aLong);


}
