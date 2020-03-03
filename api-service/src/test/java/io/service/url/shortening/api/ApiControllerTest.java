package io.service.url.shortening.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.service.url.shortening.exception.UrlNotFoundException;
import io.service.url.shortening.model.UrlData;
import io.service.url.shortening.service.UrlShorteningService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ApiController.class)
public class ApiControllerTest {

    private static final String SHORT_URL = "shortUrl";
    private static final String URL = "longUrl";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UrlShorteningService urlShorteningService;

    @Test
    public void testRedirect() throws Exception {
        when(urlShorteningService.getRedirectUrl(anyString())).thenReturn(URL);
        mockMvc.perform(get("/shortUrl")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", URL));
    }

    @Test
    public void testRedirectWithNotExistingShortUrl() throws Exception {
        when(urlShorteningService.getRedirectUrl(anyString())).thenThrow(new UrlNotFoundException());
        mockMvc.perform(get("/shortUrl")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRedirectExceptionally() throws Exception {
        when(urlShorteningService.getRedirectUrl(anyString())).thenThrow(new RuntimeException());
        mockMvc.perform(get("/shortUrl")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCreateShortUrl() throws Exception {
        UrlData urlData = new UrlData(URL);
        when(urlShorteningService.getShortUrl(urlData)).thenReturn(SHORT_URL);
        mockMvc.perform(post("/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(urlData)))
                .andExpect(status().isOk())
                .andExpect(content().string(SHORT_URL));
    }

    @Test
    public void testCreateShortUrlExceptionally() throws Exception {
        UrlData urlData = new UrlData(URL);
        when(urlShorteningService.getShortUrl(urlData)).thenThrow(new RuntimeException());
        mockMvc.perform(post("/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(urlData)))
                .andExpect(status().isInternalServerError());
    }
}