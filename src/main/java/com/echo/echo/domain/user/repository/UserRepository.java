package com.echo.echo.domain.user.repository;

import com.echo.echo.domain.user.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

}
