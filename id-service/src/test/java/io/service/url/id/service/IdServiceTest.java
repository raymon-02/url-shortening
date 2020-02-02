package io.service.url.id.service;

import io.service.url.id.exception.NoAvailableServerIdException;
import io.service.url.id.model.ServerId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class IdServiceTest {

    private static final String IDS = "ABCDEFGHIJKLMNOPQRTSUBWXYZ";

    @Mock
    private HazelcastService hazelcastService;

    private IdService idService;

    @Before
    public void setUp() {
        when(hazelcastService.getIdsInUse()).thenReturn(
                new HashSet<>(Arrays.asList("A", "B"))
        );
        idService = new IdService(hazelcastService);
    }

    @Test
    public void testGetServerId() {
        ServerId serverId = idService.getServerId();
        assertEquals("C", serverId.getId());
        assertEquals("c", serverId.getFallbackId());
    }

    @Test(expected = NoAvailableServerIdException.class)
    public void testGetServerIdWithNoAvailableId() {
        Set<String> idsInUse = IntStream.range(0, IDS.length())
                .boxed()
                .map(i -> IDS.substring(i, i + 1))
                .collect(toSet());
        when(hazelcastService.getIdsInUse()).thenReturn(idsInUse);
        idService.getServerId();
    }

    @Test
    public void testDeleteServerIdNoExceptions() {
        idService.deleteServerId("C");
    }
}