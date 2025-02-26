package com.example.auth.repository;

import com.example.auth.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByEmailAndFlwEmail(String email, String flwEmail);

    @Query("Select f.email from Follow f where f.flwEmail like :email")
    List<String> findByFlwEmail(String email);

    @Query("Select f.flwEmail from Follow f where f.email like :email")
    List<String> findByEmail(String email);
}
