package io.service.url.shortening.service;

import io.service.url.shortening.exception.UrlNotFoundException;
import io.service.url.shortening.model.ServerId;
import io.service.url.shortening.model.UrlData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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

    private Integer presetUrlCount;
    private RestService restService;
    private HazelcastService hazelcastService;


    public UrlShorteningService(
            @Value("${url.generate.preset:500000}") Integer presetUrlCount,
            RestService restService,
            HazelcastService hazelcastService
    ) {
        this.presetUrlCount = presetUrlCount;
        this.restService = restService;
        this.hazelcastService = hazelcastService;
    }


    @PostConstruct
    public void init() {
        serverId = restService.getServerId();
        long currentShortUrlNumber = hazelcastService.getCurrentShortUrlNumber(serverId.getId());
        shortUrlNumber.addAndGet(currentShortUrlNumber);
        long sequenceNumber = hazelcastService.getSequenceNumber(serverId.getFallbackId());
        log.info("Service starting with [\n\n" +
                        "api-service: [\n" +
                        "    serverId={},\n" +
                        "    shortUrlNumber={},\n" +
                        "    shortUrlSequenceNumber={},\n" +
                        "    pregenerated url count={},\n" +
                        "]\n",
                serverId, shortUrlNumber, sequenceNumber, presetUrlCount
        );
    }

    @PreDestroy
    public void destroy() {
        log.info("Shutting down, cleaning server id...");
        restService.deleteServerId(serverId.getId());
        log.info("Cleaning server id finished");
        generateUrlsExecutor.shutdownNow();
    }

    @EventListener(ApplicationStartedEvent.class)
    public void startAppListener() {
        generateUrls();
    }

    public String getRedirectUrl(String shortUrl) {
        log.debug("Getting full url for short url {}", shortUrl);
        String url = hazelcastService.getUrlByShortUrl(shortUrl);
        if (url == null) {
            log.error("Url for short url={} not found", shortUrl);
            throw new UrlNotFoundException();
        }
        log.debug("Url for short url={} found: url={}", shortUrl, url);

        return url;
    }


    public String getShortUrl(UrlData urlData) {
        log.debug("Short url generating for: {}", urlData.getUrl());
        String shortUrl = shortUrls.pollFirst();
        if (shortUrl == null) {
            log.debug("No pre-generated urls");
            generateUrlsConcurrently();
            shortUrl = generateUrlByNumber(
                    serverId.getFallbackId(),
                    hazelcastService.getAndAddSequenceNumber(serverId.getFallbackId())
            );
        }
        log.debug("Short url generated: {} -> {}", shortUrl, urlData.getUrl());
        hazelcastService.saveUrlData(shortUrl, urlData.getUrl());

        return shortUrl;
    }

    private void generateUrls() {
        log.debug("Generating urls...");
        long start = shortUrlNumber.get();
        shortUrlNumber.addAndGet(presetUrlCount);
        hazelcastService.saveCurrentShortUrlNumber(serverId.getId(), shortUrlNumber.get());
        long end = shortUrlNumber.get();
        for (long i = start; i < end; i++) {
            shortUrls.addLast(generateUrlByNumber(serverId.getId(), i));
        }
        log.debug("Generating urls finished");
    }

    private void generateUrlsConcurrently() {
        generateUrlsExecutor.execute(this::generateUrls);
    }

    private static String generateUrlByNumber(String id, long number) {
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
