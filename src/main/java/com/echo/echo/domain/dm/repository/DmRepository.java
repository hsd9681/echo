package com.echo.echo.domain.dm.repository;

import com.echo.echo.domain.dm.entity.Dm;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DmRepository extends ReactiveCrudRepository<Dm, String> {
}
