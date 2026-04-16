package com.tfgkk.farmer_platform.repository;

import com.tfgkk.farmer_platform.entity.AddressEntity;
import com.tfgkk.farmer_platform.entity.UserAccount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    List<AddressEntity> findByUserIdOrderByIdAsc(Long userId);
    List<AddressEntity> findByUserOrderByIdAsc(UserAccount user);
}
