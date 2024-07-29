package com.echo.echo.domain.text.controller;

import com.echo.echo.domain.text.TextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("text")
public class TextController {

    private final TextService textService;

}
