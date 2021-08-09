package com.B305.ogym.domain.users;

import com.B305.ogym.domain.users.common.UserBase;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRepository extends JpaRepository<UserBase, Long> {

    @EntityGraph(attributePaths = "authority")
    UserBase findOneWithAuthoritiesByEmail(String email);

    UserBase findByEmail(String email);
<<<<<<< HEAD
=======
    boolean existsByEmail(String email);
    boolean existsByNickname(String email);
>>>>>>> 091e6aa5c83db24a5d5b183e28fef92ad935d842
}
