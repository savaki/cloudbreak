package com.sequenceiq.cloudbreak.service.cluster.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.domain.HostMetadata;
import com.sequenceiq.cloudbreak.domain.InstanceMetaData;
import com.sequenceiq.cloudbreak.repository.InstanceMetaDataRepository;

@Component
public class ConsulServerFilter implements HostFilter {

    @Autowired
    private InstanceMetaDataRepository instanceMetadataRepository;

    @Override
    public List<HostMetadata> filter(long stackId, Map<String, String> config, List<HostMetadata> hosts) throws HostFilterException {
        List<HostMetadata> copy = new ArrayList<>(hosts);
        for (HostMetadata host : hosts) {
            InstanceMetaData instanceMetaData = instanceMetadataRepository.findHostInStack(stackId, host.getHostName());
            if (instanceMetaData.getConsulServer()) {
                copy.remove(host);
            }
        }
        return copy;
    }

}
