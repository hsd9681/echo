package com.echo.echo.domain.text;

import com.echo.echo.domain.text.repository.TextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TextService {

    private final TextRepository repository;

}
