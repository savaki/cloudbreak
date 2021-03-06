package com.sequenceiq.cloudbreak.converter;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.controller.json.CloudbreakEventsJson;
import com.sequenceiq.cloudbreak.domain.CloudbreakEvent;

@Component
public class CloudbreakEventConverter extends AbstractConverter<CloudbreakEventsJson, CloudbreakEvent> {

    @Override
    public CloudbreakEventsJson convert(CloudbreakEvent entity) {
        CloudbreakEventsJson json = new CloudbreakEventsJson();
        json.setAccount(entity.getAccount());
        json.setBlueprintId(entity.getBlueprintId());
        json.setBlueprintName(entity.getBlueprintName());
        json.setCloud(entity.getCloud());
        json.setEventMessage(entity.getEventMessage());
        json.setEventType(entity.getEventType());
        json.setEventTimestamp(entity.getEventTimestamp().getTime());
        json.setRegion(entity.getRegion());
        json.setOwner(entity.getOwner());
        json.setStackId(entity.getStackId());
        json.setStackName(entity.getStackName());
        json.setStackStatus(entity.getStackStatus());
        json.setNodeCount(entity.getNodeCount());
        json.setInstanceGroup(entity.getInstanceGroup());
        return json;
    }

    @Override
    public CloudbreakEvent convert(CloudbreakEventsJson json) {
        CloudbreakEvent entity = new CloudbreakEvent();
        entity.setAccount(json.getAccount());
        entity.setBlueprintId(json.getBlueprintId());
        entity.setBlueprintName(json.getBlueprintName());
        entity.setCloud(json.getCloud());
        entity.setEventMessage(json.getEventMessage());
        entity.setEventType(json.getEventType());
        entity.setEventTimestamp(new Date(json.getEventTimestamp()));
        entity.setRegion(json.getRegion());
        entity.setOwner(json.getOwner());
        entity.setStackId(json.getStackId());
        entity.setStackName(json.getStackName());
        entity.setStackStatus(json.getStackStatus());
        entity.setNodeCount(json.getNodeCount());
        entity.setInstanceGroup(json.getInstanceGroup());
        return entity;
    }
}
