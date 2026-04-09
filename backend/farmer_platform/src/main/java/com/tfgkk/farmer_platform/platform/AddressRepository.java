package com.tfgkk.farmer_platform.platform;

import com.tfgkk.farmer_platform.user.UserAccount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    List<AddressEntity> findByUserIdOrderByIdAsc(Long userId);
    List<AddressEntity> findByUserOrderByIdAsc(UserAccount user);
}
