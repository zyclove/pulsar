/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pulsar.broker.admin.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.apache.pulsar.common.partition.PartitionedTopicMetadata;
import org.apache.pulsar.common.policies.data.LocalPolicies;
import org.apache.pulsar.common.policies.data.NamespaceIsolationData;
import org.apache.pulsar.common.policies.data.Policies;
import org.apache.pulsar.common.policies.impl.NamespaceIsolationPolicies;
import org.apache.pulsar.metadata.api.MetadataStoreException;
import org.apache.pulsar.metadata.api.extended.MetadataStoreExtended;

@Getter
public class NamespaceResources extends BaseResources<Policies> {
    private IsolationPolicyResources isolationPolicies;
    private LocalPoliciesResources localPolicies;
    private PartitionedTopicResources partitionedTopicResources;
    private MetadataStoreExtended localStore;
    private MetadataStoreExtended configurationStore;

    public NamespaceResources(MetadataStoreExtended localStore, MetadataStoreExtended configurationStore,
            int operationTimeoutSec) {
        super(configurationStore, Policies.class, operationTimeoutSec);
        this.localStore = localStore;
        this.configurationStore = configurationStore;
        isolationPolicies = new IsolationPolicyResources(configurationStore, operationTimeoutSec);
        localPolicies = new LocalPoliciesResources(localStore, operationTimeoutSec);
        partitionedTopicResources = new PartitionedTopicResources(configurationStore, operationTimeoutSec);
    }

    public static class IsolationPolicyResources extends BaseResources<Map<String, NamespaceIsolationData>> {
        public IsolationPolicyResources(MetadataStoreExtended store, int operationTimeoutSec) {
            super(store, new TypeReference<Map<String, NamespaceIsolationData>>() {
            }, operationTimeoutSec);
        }

        public Optional<NamespaceIsolationPolicies> getPolicies(String path) throws MetadataStoreException {
            Optional<Map<String, NamespaceIsolationData>> data = super.get(path);
            return data.isPresent() ? Optional.of(new NamespaceIsolationPolicies(data.get())) : Optional.empty();
        }
    }

    public static class LocalPoliciesResources extends BaseResources<LocalPolicies> {
        public LocalPoliciesResources(MetadataStoreExtended configurationStore, int operationTimeoutSec) {
            super(configurationStore, LocalPolicies.class, operationTimeoutSec);
        }
    }

    public static class PartitionedTopicResources extends BaseResources<PartitionedTopicMetadata> {
        public PartitionedTopicResources(MetadataStoreExtended configurationStore, int operationTimeoutSec) {
            super(configurationStore, PartitionedTopicMetadata.class, operationTimeoutSec);
        }
    }
}
