package com.boot.ping.service;

import com.boot.ping.MainResponseDto;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class MainService {

    public MainResponseDto.PingDto getPingMessage() throws IOException {

        InetAddress target = InetAddress.getByName("www.google.com");
        boolean isConnect = target.isReachable(1000);
        List<Long> startPings = new ArrayList<>();
        List<Long> endPings = new ArrayList<>();
        String responseMessage = null;

        if (isConnect) {

            int i;
            for (i = 0; i <= 5; i++) {
                Long startPing = System.nanoTime();
                boolean reConnect = target.isReachable(1000);
                Long endPing = System.nanoTime();
                startPings.add(startPing);
                endPings.add(endPing);
            }

            List<Long> averagePings =
                    LongStream.range(0, endPings.size())
                            .mapToObj(k -> endPings.get((int) k) - startPings.get((int) k))
                            .collect(Collectors.toList());

            Long average = (long) averagePings.stream().mapToLong(Long::longValue).average().orElse(0);

            responseMessage = average / 1_000_000 + "평균 ms";

        } else {

            responseMessage = "connection lost";

        }

        return  MainResponseDto.PingDto.builder()
                .responsePing(responseMessage)
                .build();
    }


}
