package com.cherrypie.pieberry.repo;

import com.cherrypie.pieberry.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepo extends JpaRepository<User, String> {

}

