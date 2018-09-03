/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.cloud.autoconfigure.cosmosdb;

import com.microsoft.azure.management.cosmosdb.CosmosDBAccount;
import com.microsoft.azure.management.cosmosdb.DatabaseAccountKind;
import com.microsoft.azure.management.cosmosdb.DatabaseAccountListConnectionStringsResult;
import com.microsoft.azure.spring.cloud.autoconfigure.context.AzureContextAutoConfiguration;
import com.microsoft.azure.spring.cloud.autoconfigure.telemetry.TelemetryAutoConfiguration;
import com.microsoft.azure.spring.cloud.autoconfigure.telemetry.TelemetryCollector;
import com.microsoft.azure.spring.cloud.context.core.AzureAdmin;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoOperations;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass(MongoOperations.class)
@AutoConfigureBefore(TelemetryAutoConfiguration.class)
@AutoConfigureAfter(AzureContextAutoConfiguration.class)
@ConditionalOnProperty(name = "spring.cloud.azure.cosmosdb.enabled", matchIfMissing = true)
@EnableConfigurationProperties(AzureCosmosDbProperties.class)
public class AzureCosmosDbMongoDbAutoConfiguration {
    private static final String COSMOS_DB_MONGO = "CosmosDbMongo";

    @PostConstruct
    public void collectTelemetry() {
        TelemetryCollector.getInstance().addService(COSMOS_DB_MONGO);
    }

    @ConditionalOnMissingBean
    @Primary
    @Bean
    MongoProperties mongoProperties(AzureAdmin azureAdmin, AzureCosmosDbProperties azureCosmosDbProperties) {
        String accountName = azureCosmosDbProperties.getAccountName();
        String readReplication = azureCosmosDbProperties.getReadReplication();
        CosmosDBAccount cosmosDBAccount =
                azureAdmin.getOrCreateCosmosDBAccount(DatabaseAccountKind.MONGO_DB, readReplication, accountName);
        MongoProperties mongoProperties = new MongoProperties();
        DatabaseAccountListConnectionStringsResult connectionStrings = cosmosDBAccount.listConnectionStrings();
        mongoProperties.setUri(connectionStrings.connectionStrings().get(0).connectionString());
        mongoProperties.setDatabase(azureCosmosDbProperties.getDatabase());
        return mongoProperties;
    }
}