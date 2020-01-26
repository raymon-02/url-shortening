package io.service.url.shortening.service;

import io.service.url.shortening.exception.UrlNotFoundException;
import io.service.url.shortening.model.ServerId;
import io.service.url.shortening.model.UrlData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class UrlShorteningService {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    private final Deque<String> shortUrls = new ConcurrentLinkedDeque<>();
    private final AtomicLong shortUrlNumber = new AtomicLong(0);
    private final ExecutorService generateUrlsExecutor = Executors.newSingleThreadExecutor(
            runnable -> new Thread(runnable, "GenerateUrls")
    );

    private volatile ServerId serverId;


    @Value("${url.generate.preset:500000}")
    private Integer presetUrlAmount;

    @Autowired
    private RestService restService;

    @Autowired
    private HazelcastService hazelcastService;


    @PostConstruct
    public void init() {
        serverId = restService.getServerId();
        Long currentShortUrlNumber = hazelcastService.getCurrentShortUrlNumber(serverId.getId());
        shortUrlNumber.addAndGet(currentShortUrlNumber == null ? 0 : currentShortUrlNumber);
        log.info("Server starting with: serverId={}, shortUrlNumber={}", serverId, shortUrlNumber);
    }

    @EventListener(ApplicationStartedEvent.class)
    public void startAppListener() {
        log.info("Pre-generating urls started");
        generateUrls();
        log.info("Pre-generating urls finished");
    }

    public String getRedirectUrl(String shortUrl) {
        log.info("Getting full url for short {}", shortUrl);
        String url = hazelcastService.getUrlByShortUrl(shortUrl);

        if (url == null) {
            log.error("Url for short {} not found", shortUrl);
            throw new UrlNotFoundException();
        }
        log.info("Url for short {} found: url={}", shortUrl, url);
        return url;
    }


    public String getShortUrl(UrlData urlData) {
        log.info("Short url generating for: {}", urlData.getUrl());
        String shortUrl = shortUrls.pollFirst();
        if (shortUrl == null) {
            generateUrlsConcurrently();
            shortUrl = generateUrlByNumber(serverId.getId(), hazelcastService.getAndAddSequenceNumber());
        }
        log.info("Short url generated: {} -> {}", shortUrl, urlData.getUrl());

        hazelcastService.saveUrlData(shortUrl, urlData.getUrl());

        return shortUrl;
    }

    private void generateUrls() {
        long start = shortUrlNumber.get();
        shortUrlNumber.addAndGet(presetUrlAmount);
        hazelcastService.saveCurrentShortUrlNumber(serverId.getId(), shortUrlNumber.get());
        long end = shortUrlNumber.get();
        for (long i = start; i < end; i++) {
            shortUrls.addLast(generateUrlByNumber(serverId.getId(), i));
        }
    }

    private void generateUrlsConcurrently() {
        generateUrlsExecutor.execute(this::generateUrls);
    }

    private static String generateUrlByNumber(char id, long number) {
        StringBuilder result = new StringBuilder();
        while (number != 0) {
            long index = number % ALPHABET.length();
            number /= ALPHABET.length();
            result.append(ALPHABET.charAt((int) index));
        }
        result.append(id);
        return result.reverse().toString();
    }

}
