package com.example.animore.Search;

import com.example.animore.Search.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    //User getUserIdBy(); //user_id찾기
}

