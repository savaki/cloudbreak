package com.sequenceiq.cloudbreak.service.history;

import com.sequenceiq.cloudbreak.conf.ReactorConfig;
import com.sequenceiq.cloudbreak.domain.AwsCredential;
import com.sequenceiq.cloudbreak.domain.AwsTemplate;
import com.sequenceiq.cloudbreak.domain.AzureCredential;
import com.sequenceiq.cloudbreak.domain.AzureTemplate;
import com.sequenceiq.cloudbreak.domain.Blueprint;
import com.sequenceiq.cloudbreak.domain.BlueprintHistory;
import com.sequenceiq.cloudbreak.domain.Cluster;
import com.sequenceiq.cloudbreak.domain.ClusterHistory;
import com.sequenceiq.cloudbreak.domain.Credential;
import com.sequenceiq.cloudbreak.domain.CredentialHistory;
import com.sequenceiq.cloudbreak.domain.HistoryEvent;
import com.sequenceiq.cloudbreak.domain.ProvisionEntity;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.domain.StackHistory;
import com.sequenceiq.cloudbreak.domain.Template;
import com.sequenceiq.cloudbreak.domain.TemplateHistory;
import com.sequenceiq.cloudbreak.domain.User;
import com.sequenceiq.cloudbreak.repository.BlueprintHistoryRepository;
import com.sequenceiq.cloudbreak.repository.ClusterHistoryRepository;
import com.sequenceiq.cloudbreak.repository.CredentialHistoryRepository;
import com.sequenceiq.cloudbreak.repository.StackHistoryRepository;
import com.sequenceiq.cloudbreak.repository.TemplateHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.Reactor;
import reactor.event.Event;

import java.util.Calendar;

@Service
public class DefaultHistoryService implements HistoryService<ProvisionEntity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHistoryService.class);

    @Autowired
    private Reactor reactor;

    @Autowired
    private ClusterHistoryRepository clusterHistoryRepository;

    @Autowired
    private StackHistoryRepository stackHistoryRepository;

    @Autowired
    private BlueprintHistoryRepository blueprintHistoryRepository;

    @Autowired
    private CredentialHistoryRepository credentialHistoryRepository;

    @Autowired
    private TemplateHistoryRepository templateHistoryRepository;

    @Override
    public void recordHistory(ProvisionEntity entity, HistoryEvent historyEvent) {

        if (entity instanceof Cluster) {
            clusterHistoryRepository.save(createClusterHistory((Cluster) entity, historyEvent));
        } else if (entity instanceof Blueprint) {
            blueprintHistoryRepository.save(createBlueprintHistory((Blueprint) entity, historyEvent));
        } else if (entity instanceof Template) {
            templateHistoryRepository.save(createTemplateHistory((Template) entity, historyEvent));
        } else if (entity instanceof Stack) {
            stackHistoryRepository.save(createStackHistory((Stack) entity, historyEvent));
        } else if (entity instanceof Credential) {
            credentialHistoryRepository.save(createCredentialHistory((Credential) entity, historyEvent));
        } else if (entity instanceof User) {
            LOGGER.debug("User has been touched. Event: {}", historyEvent);
        }

    }

    private CredentialHistory createCredentialHistory(Credential entity, HistoryEvent historyEvent) {
        CredentialHistory credentialHistory = new CredentialHistory();

        credentialHistory.setName(entity.getCredentialName());
        credentialHistory.setDescription(entity.getDescription());
        credentialHistory.setCloudPlatform(entity.getCloudPlatform().name());
        credentialHistory.setPublickey(entity.getPublicKey());

        credentialHistory.setId(entity.getId());
        credentialHistory.setUserId(entity.getOwner().getId());
        credentialHistory.setEventTimestamp(Calendar.getInstance().getTime());
        credentialHistory.setEventType(historyEvent);

        switch (entity.getCloudPlatform()) {
            case AWS:
                AzureCredential azureCredential = (AzureCredential) entity;
                credentialHistory.setJks(azureCredential.getJks());
                credentialHistory.setSubscriptionid(azureCredential.getSubscriptionId());
                break;
            case AZURE:
                AwsCredential awsCredential = (AwsCredential) entity;
                credentialHistory.setKeyPairName(awsCredential.getKeyPairName());
                credentialHistory.setTemporaryAccessKeyId(awsCredential.getTemporaryAwsCredentials().getAccessKeyId());
                credentialHistory.setRoleArn(awsCredential.getRoleArn());
                break;
            default:
                throw new IllegalStateException("Unsupported cloud platform: " + entity.getCloudPlatform());
        }
        return credentialHistory;
    }

    private StackHistory createStackHistory(Stack entity, HistoryEvent historyEvent) {
        StackHistory stackHistory = new StackHistory();
        stackHistory.setUserId(entity.getUser().getId());
        stackHistory.setName(entity.getName());
        stackHistory.setId(entity.getId());
        stackHistory.setEventTimestamp(Calendar.getInstance().getTime());
        stackHistory.setEventType(historyEvent);
        stackHistory.setAmbariIp(entity.getAmbariIp());
        stackHistory.setClusterId(entity.getCluster().getId());
        stackHistory.setCredentialId(entity.getCredential().getId());
        stackHistory.setHash(entity.getHash());
        stackHistory.setMetadataReady(entity.isMetadataReady());
        stackHistory.setNodeCount(entity.getNodeCount());
        stackHistory.setStackCompleted(entity.isStackCompleted());
        stackHistory.setStatus(entity.getStatus().name());
        stackHistory.setStatusReason(entity.getStatusReason());
        stackHistory.setTemplateId(entity.getTemplate().getId());
        stackHistory.setTerminated(entity.getTerminated());
        stackHistory.setVersion(entity.getVersion());
        stackHistory.setDescription(entity.getDescription());
        return stackHistory;
    }

    private TemplateHistory createTemplateHistory(Template entity, HistoryEvent historyEvent) {
        TemplateHistory templateHistory = new TemplateHistory();

        switch (entity.cloudPlatform()) {
            case AZURE:
                AzureTemplate azureTemplate = (AzureTemplate) entity;
                templateHistory.setDescription(azureTemplate.getDescription());
                templateHistory.setName(azureTemplate.getName());
                templateHistory.setImageName(azureTemplate.getImageName());
                templateHistory.setVmType(azureTemplate.getVmType());
                templateHistory.setLocation(azureTemplate.getLocation());
                templateHistory.setUserId(azureTemplate.getAzureTemplateOwner().getId());
                break;
            case AWS:
                AwsTemplate awsTemplate = (AwsTemplate) entity;
                templateHistory.setName(awsTemplate.getName());
                templateHistory.setDescription(awsTemplate.getDescription());
                templateHistory.setRegion(awsTemplate.getRegion().name());
                templateHistory.setAmiid(awsTemplate.getAmiId());
                templateHistory.setInstancetype(awsTemplate.getInstanceType().name());
                templateHistory.setSshLocation(awsTemplate.getSshLocation());
                templateHistory.setUserId(awsTemplate.getOwner().getId());
                break;
            default:
                throw new IllegalStateException("Unsupported cloud platform: " + entity.cloudPlatform());
        }
        return templateHistory;
    }

    private BlueprintHistory createBlueprintHistory(Blueprint entity, HistoryEvent historyEvent) {
        BlueprintHistory blueprintHistory = new BlueprintHistory();
        blueprintHistory.setName(entity.getName());
        blueprintHistory.setBlueprintDescription(entity.getDescription());
        blueprintHistory.setBlueprintId(entity.getId());
        blueprintHistory.setBlueprintName(entity.getBlueprintName());
        blueprintHistory.setBlueprintText(entity.getBlueprintText());
        blueprintHistory.setHostGroupCount(entity.getHostGroupCount());
        blueprintHistory.setUserId(entity.getUser().getId());
        return blueprintHistory;
    }

    private ClusterHistory createClusterHistory(Cluster entity, HistoryEvent historyEvent) {
        ClusterHistory clusterHistory = new ClusterHistory();
        clusterHistory.setName(entity.getName());
        clusterHistory.setBlueprintId(entity.getBlueprint().getId());
        clusterHistory.setDescription(entity.getDescription());
        clusterHistory.setUserId(entity.getUser().getId());
        clusterHistory.setCreationFinished(entity.getCreationFinished());
        clusterHistory.setCreationStarted(entity.getCreationStarted());
        clusterHistory.setStatus(entity.getStatus().name());
        clusterHistory.setStatusReason(entity.getStatusReason());
        return clusterHistory;
    }

    @Override
    public void notify(ProvisionEntity entity, HistoryEvent historyEvent) {
        Event reactorEvent = Event.wrap(entity);
        reactorEvent.getHeaders().set("history.event", historyEvent.name());
        reactor.notify(ReactorConfig.HISTORY_EVENT, reactorEvent);
    }
}
