/*
 * Copyright (C) 2025 Nathan Clayton
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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