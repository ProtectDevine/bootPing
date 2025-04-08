package com.boot.ping.strategy;

import com.boot.ping.service.WinRegistryService;

import java.io.IOException;

public interface OptimizationStrategy {

    void apply(WinRegistryService registryService, String networkPath) throws IOException, InterruptedException;

}
