package com.boot.ping.strategy;

import com.boot.ping.dto.RegistryCommandDto;
import com.boot.ping.service.WinRegistryService;

import java.io.IOException;

public class RegistryStrategy  {

    public static class TcpAckFrequencyStrategy implements OptimizationStrategy {
        @Override
        public void apply(WinRegistryService registryService, String networkPath) throws IOException, InterruptedException {
            long currentValue = registryService.checkRegistryValue(networkPath, "TcpAckFrequency");
            System.out.println("TcpAckFrequency: " + currentValue);
            if (currentValue != 1L) {
                registryService.executeCommand(RegistryCommandDto.builder()
                        .path(networkPath)
                        .valueName("TcpAckFrequency")
                        .data(1L)
                        .build());
            }
        }
    }

    public static class TcpNoDelayStrategy implements OptimizationStrategy {
        @Override
        public void apply(WinRegistryService registryService, String networkPath) throws IOException, InterruptedException {
            long currentValue = registryService.checkRegistryValue(networkPath, "TCPNoDelay");
            System.out.println("TCPNoDelay: " + currentValue);
            if (currentValue != 1L) {
                registryService.executeCommand(RegistryCommandDto.builder()
                        .path(networkPath)
                        .valueName("TCPNoDelay")
                        .data(1L)
                        .build());
            }
        }
    }

    public static class MsmqTcpNoDelayStrategy implements OptimizationStrategy {
        private static final String MSMQ_PATH = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\MSMQ\\Parameters";

        @Override
        public void apply(WinRegistryService registryService, String networkPath) throws IOException, InterruptedException {
            long currentValue = registryService.checkRegistryValue(MSMQ_PATH, "TCPNoDelay");
            System.out.println("MSMQ TCPNoDelay: " + currentValue);
            if (currentValue != 1L) {
                registryService.executeCommand(RegistryCommandDto.builder()
                        .path(MSMQ_PATH)
                        .valueName("TCPNoDelay")
                        .data(1L)
                        .build());
            }
        }
    }

    public static class NetworkThrottlingStrategy implements OptimizationStrategy {
        private static final String THROTTLING_PATH = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Multimedia\\SystemProfile";

        @Override
        public void apply(WinRegistryService registryService, String networkPath) throws IOException, InterruptedException {
            long currentValue = registryService.checkRegistryValue(THROTTLING_PATH, "NetworkThrottlingIndex");
            System.out.println("NetworkThrottlingIndex: " + currentValue);
            if (currentValue != 0xFFFFFFFFL) {
                registryService.executeCommand(RegistryCommandDto.builder()
                        .path(THROTTLING_PATH)
                        .valueName("NetworkThrottlingIndex")
                        .type("REG_DWORD")
                        .data(0xFFFFFFFFL)
                        .build());
            }
        }
    }

}
