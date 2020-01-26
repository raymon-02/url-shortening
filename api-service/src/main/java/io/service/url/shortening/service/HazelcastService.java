package io.service.url.shortening.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IMap;
import com.hazelcast.cp.CPSubsystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class HazelcastService {

    private static final String SEQUENCE_NUMBER = "sequence_number";
    private static final String URL_DATA_MAP = "url_data";
    private static final String URL_NUMBER_MAP = "url_number";

    @Autowired
    @Qualifier("hzInstance")
    private HazelcastInstance hazelcastInstance;

    public long getAndAddSequenceNumber() {
        CPSubsystem cpSubsystem = hazelcastInstance.getCPSubsystem();
        IAtomicLong sequenceNumber = cpSubsystem.getAtomicLong(SEQUENCE_NUMBER);
        return sequenceNumber.getAndIncrement();
    }

    public String getUrlByShortUrl(String shortUrl) {
        IMap<String, String> urlDataMap = hazelcastInstance.getMap(URL_DATA_MAP);
        return urlDataMap.get(shortUrl);
    }

    public Long getCurrentShortUrlNumber(char serverId) {
        IMap<Character, Long> urlNumberMap = hazelcastInstance.getMap(URL_NUMBER_MAP);
        return urlNumberMap.get(serverId);
    }

    public void saveUrlData(String shortUrl, String url) {
        IMap<String, String> urlDataMap = hazelcastInstance.getMap(URL_DATA_MAP);
        urlDataMap.put(shortUrl, url);
    }

    public void saveCurrentShortUrlNumber(char serverId, long number) {
        IMap<Character, Long> urlNumberMap = hazelcastInstance.getMap(URL_NUMBER_MAP);
        urlNumberMap.put(serverId, number);
    }

}
