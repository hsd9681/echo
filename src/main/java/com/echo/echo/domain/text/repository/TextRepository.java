package com.echo.echo.domain.text.repository;

import com.echo.echo.domain.text.entity.Text;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TextRepository extends ReactiveCrudRepository<Text, Long> {

}
