package com.luxepricing.pulsarstats;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import org.apache.pulsar.client.admin.PulsarAdmin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "App", mixinStandardHelpOptions = true, version = "1.0",
         description = "Fetches topic stats from Apache Pulsar using PulsarAdmin.")
public class App implements Callable<Integer> {

    @Option(names = {"-s", "--server"}, description = "Pulsar Admin URL", defaultValue = "http://localhost:8080")
    private String pulsarAdminUrl;

    @Option(names = {"-f", "--file"}, description = "Output filename (if not provided, output to terminal)")
    private String outputFilename;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        try (PulsarAdmin admin = PulsarAdmin.builder()
                .serviceHttpUrl(pulsarAdminUrl)
                .build()) {

            ObjectMapper mapper = new ObjectMapper();
            PulsarStatsService statsService = new PulsarStatsService(admin, mapper);

            // Fetch stats
            List<ObjectNode> output = statsService.fetchStats();

            // Output to JSON
            String jsonOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(output);

            if (outputFilename == null) {
                System.out.println(jsonOutput);
            } else {
                Files.write(Paths.get(outputFilename), jsonOutput.getBytes(StandardCharsets.UTF_8));
            }
        }
        return 0;
    }
}