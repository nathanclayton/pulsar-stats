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

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.common.policies.data.TopicStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class PulsarStatsService {

    private final PulsarAdmin admin;
    private final ObjectMapper mapper;

    public PulsarStatsService(PulsarAdmin admin, ObjectMapper mapper) {
        this.admin = admin;
        this.mapper = mapper;
    }

    public List<ObjectNode> fetchStats() throws Exception {
        List<ObjectNode> output = new ArrayList<>();
        List<String> tenants = admin.tenants().getTenants();

        for (String tenant : tenants) {
            List<String> namespaces = admin.namespaces().getNamespaces(tenant);

            for (String namespace : namespaces) {
                List<String> topics = admin.topics().getList(namespace);

                for (String topic : topics) {
                    try {
                        boolean isPartitioned = admin.topics()
                                .getPartitionedTopicMetadata(topic)
                                .partitions > 0;

                        TopicStats stats = isPartitioned
                                ? admin.topics().getPartitionedStats(topic, true)
                                : admin.topics().getStats(topic);

                        ObjectNode node = mapper.createObjectNode();
                        node.put("tenant", tenant);
                        node.put("namespace", namespace);
                        node.put("topic", topic);
                        node.set("stats", mapper.valueToTree(stats));

                        output.add(node);
                    } catch (Exception e) {
                        System.err.println("Failed to fetch stats for topic " + topic + ": " + e.getMessage());
                    }
                }
            }
        }

        return output;
    }
}