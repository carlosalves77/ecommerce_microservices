package com.carldev.auth_service.repository;

import com.carldev.auth_service.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findAllByUserAuthUserId(UUID userId);

    boolean existsByUserAuthUserId(UUID id);

    @Modifying @Query("UPDATE Address a SET a.defaultAddress = false WHERE a.userAuth.userId = :userId")
    void removeDefaultFromAllUserAddresses(UUID userId);
}
