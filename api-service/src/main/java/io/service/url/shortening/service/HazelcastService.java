package io.service.url.shortening.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IMap;
import com.hazelcast.cp.CPSubsystem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class HazelcastService {

    private static final String URL_DATA_MAP = "url_data";
    private static final String URL_NUMBER_MAP = "url_number";

    private HazelcastInstance hazelcastInstance;

    public HazelcastService(
            @Qualifier("hzInstance") HazelcastInstance hazelcastInstance
    ) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public long getSequenceNumber(String fallBackId) {
        CPSubsystem cpSubsystem = hazelcastInstance.getCPSubsystem();
        IAtomicLong sequenceNumber = cpSubsystem.getAtomicLong(fallBackId);
        return sequenceNumber.get();
    }

    public long getAndAddSequenceNumber(String fallBackId) {
        CPSubsystem cpSubsystem = hazelcastInstance.getCPSubsystem();
        IAtomicLong sequenceNumber = cpSubsystem.getAtomicLong(fallBackId);
        return sequenceNumber.getAndIncrement();
    }

    public String getUrlByShortUrl(String shortUrl) {
        IMap<String, String> urlDataMap = hazelcastInstance.getMap(URL_DATA_MAP);
        return urlDataMap.get(shortUrl);
    }

    public long getCurrentShortUrlNumber(String id) {
        IMap<String, Long> urlNumberMap = hazelcastInstance.getMap(URL_NUMBER_MAP);
        Long result = urlNumberMap.get(id);
        return result == null ? 0L : result;
    }

    public void saveUrlData(String shortUrl, String url) {
        IMap<String, String> urlDataMap = hazelcastInstance.getMap(URL_DATA_MAP);
        urlDataMap.put(shortUrl, url);
    }

    public void saveCurrentShortUrlNumber(String id, long number) {
        IMap<String, Long> urlNumberMap = hazelcastInstance.getMap(URL_NUMBER_MAP);
        urlNumberMap.put(id, number);
    }

}
