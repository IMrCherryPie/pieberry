package com.cherrypie.pieberry.repo;

import com.cherrypie.pieberry.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Message, Long> {

}
