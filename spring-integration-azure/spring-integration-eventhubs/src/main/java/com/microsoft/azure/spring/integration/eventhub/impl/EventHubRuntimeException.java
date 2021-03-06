/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.integration.eventhub.impl;

import org.springframework.core.NestedRuntimeException;

/**
 * The Azure Event Hub specific {@link NestedRuntimeException}.
 *
 * @author Warren Zhu
 */
public class EventHubRuntimeException extends NestedRuntimeException {

    public EventHubRuntimeException(String msg) {
        super(msg);
    }

    public EventHubRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
