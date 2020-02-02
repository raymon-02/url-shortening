package io.service.url.shortening.service;


import io.service.url.shortening.exception.UrlNotFoundException;
import io.service.url.shortening.model.ServerId;
import io.service.url.shortening.model.UrlData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class UrlShorteningServiceTest {

    private static final ServerId SERVER_ID = new ServerId("A", "a");
    private static final String SHORT_URL = "shortUrl";
    private static final String URL = "longUrl";
    private static final int PRESET_URL_AMOUNT = 10;

    @Mock
    private RestService restService;

    @Mock
    private HazelcastService hazelcastService;

    private UrlShorteningService urlShorteningService;

    @Before
    public void setUp() {
        when(restService.getServerId()).thenReturn(SERVER_ID);
        when(hazelcastService.getCurrentShortUrlNumber("A")).thenReturn(100L);
        urlShorteningService = new UrlShorteningService(PRESET_URL_AMOUNT, restService, hazelcastService);
        urlShorteningService.init();
        urlShorteningService.startAppListener();
    }

    @Test
    public void testGetUrlByShortUrl() {
        when(hazelcastService.getUrlByShortUrl(SHORT_URL)).thenReturn(URL);
        String result = urlShorteningService.getRedirectUrl(SHORT_URL);
        assertEquals(URL, result);
    }

    @Test(expected = UrlNotFoundException.class)
    public void testGetUrlByShortUrlThrowExceptionWhenNotPresent() {
        urlShorteningService.getRedirectUrl(SHORT_URL);
    }

    @Test
    public void testGetShortUrl() {
        // 100 % 62 = 38   => 'm'
        // 100 / 62 = 1
        // 1 % 62 = 1      => 'B'
        // serverId        => 'A'
        // 'A' + 'B' + 'm' => "ABm"
        String expectedResult = "ABm";

        doNothing().when(hazelcastService).saveUrlData(expectedResult, URL);
        String result = urlShorteningService.getShortUrl(new UrlData(URL));

        verify(hazelcastService, times(1)).saveUrlData(expectedResult, URL);
        assertEquals(expectedResult, result);
    }


    @Test
    public void testGetShortUrlWhenNoPregeneratedUrlsAvailable() {
        // 100 % 62 = 38    => 'm'
        // 100 / 62 = 1
        // 1 % 62 = 1       => 'B'
        // fallbackServerId => 'a'
        // 'a' + 'B' + 'm'  => "aBm"
        String expectedResult = "aBm";

        when(hazelcastService.getAndAddSequenceNumber("a")).thenReturn(100L);
        for (int i = 0; i < PRESET_URL_AMOUNT; i++) {
            urlShorteningService.getShortUrl(new UrlData(URL));
        }
        String result = urlShorteningService.getShortUrl(new UrlData(URL));

        verify(hazelcastService, times(1)).getAndAddSequenceNumber("a");
        verify(hazelcastService, times(11)).saveUrlData(anyString(), eq(URL));
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetShortUrlShouldGenerateUrlsAfterFallback() {
        // 100 + 10 = 110
        // 110 % 62 = 48    => 'w'
        // 100 / 62 = 1
        // 1 % 62 = 1       => 'B'
        // fallbackServerId => 'A'
        // 'A' + 'B' + 'm'  => "ABw"
        String expectedResult = "ABw";

        when(hazelcastService.getAndAddSequenceNumber("a")).thenReturn(100L);
        for (int i = 0; i < PRESET_URL_AMOUNT + 1; i++) {
            urlShorteningService.getShortUrl(new UrlData(URL));
        }
        String result = urlShorteningService.getShortUrl(new UrlData(URL));
        for (int i = 0; i < 10 && !result.startsWith("A"); i++) {
            result = urlShorteningService.getShortUrl(new UrlData(URL));
        }

        verify(hazelcastService, atLeast(1)).getAndAddSequenceNumber("a");
        verify(hazelcastService, atLeast(12)).saveUrlData(anyString(), eq(URL));
        assertEquals(expectedResult, result);
    }
}