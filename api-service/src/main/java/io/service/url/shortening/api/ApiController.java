package io.service.url.shortening.api;

import io.service.url.shortening.dto.UrlDto;
import io.service.url.shortening.exception.UrlNotFoundException;
import io.service.url.shortening.model.UrlData;
import io.service.url.shortening.service.UrlShorteningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
public class ApiController {

    @Autowired
    private UrlShorteningService urlShorteningService;

    @GetMapping("/{shortUrl}")
    public RedirectView redirect(@PathVariable("shortUrl") String shortUrl) {
        log.info("Redirecting: url={}...", shortUrl);
        return new RedirectView(urlShorteningService.getRedirectUrl(shortUrl));
    }

    @PostMapping("/")
    public ResponseEntity<String> createShortUrl(@RequestBody UrlDto urlDto) {
        log.info("Shortening: url={}", urlDto.getUrl());
        return ResponseEntity.ok(
                urlShorteningService.getShortUrl(new UrlData(urlDto.getUrl()))
        );
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<String> urlNotFoundExceptionHandler() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No short url found");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> runtimeExceptionHandler() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Something went wrong");
    }

}
