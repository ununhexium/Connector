/*
 *  Copyright (c) 2022 Amadeus
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Amadeus - initial API and implementation
 *
 */

package org.eclipse.edc.connector.transfer.dataplane.flow;

import org.eclipse.edc.connector.dataplane.spi.client.DataPlaneClient;
import org.eclipse.edc.connector.transfer.spi.callback.ControlPlaneApiUrl;
import org.eclipse.edc.connector.transfer.spi.flow.DataFlowController;
import org.eclipse.edc.connector.transfer.spi.types.DataFlowResponse;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.response.ResponseStatus;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowRequest;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.BODY;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.MEDIA_TYPE;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.METHOD;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.PATH;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.QUERY_PARAMS;
import static org.eclipse.edc.connector.transfer.dataplane.spi.TransferDataPlaneConstants.HTTP_PROXY;

public class ProviderPushTransferDataFlowController implements DataFlowController {

    private static final String ROOT_KEY = "https://sovity.de/workaround/proxy/param/";
    private final ControlPlaneApiUrl callbackUrl;
    private final DataPlaneClient dataPlaneClient;

    public ProviderPushTransferDataFlowController(ControlPlaneApiUrl callbackUrl, DataPlaneClient dataPlaneClient) {
        this.callbackUrl = callbackUrl;
        this.dataPlaneClient = dataPlaneClient;
    }

    @Override
    public boolean canHandle(DataRequest dataRequest, DataAddress contentAddress) {
        return !HTTP_PROXY.equals(dataRequest.getDestinationType());
    }

    @Override
    public @NotNull StatusResult<DataFlowResponse> initiateFlow(DataRequest dataRequest, DataAddress contentAddress, Policy policy) {
        var dataFlowRequest = createRequest(dataRequest, contentAddress);
        var result = dataPlaneClient.transfer(dataFlowRequest);
        if (result.failed()) {
            return StatusResult.failure(ResponseStatus.FATAL_ERROR, "Failed to delegate data transfer to Data Plane: " + result.getFailureDetail());
        }
        return StatusResult.success(DataFlowResponse.Builder.newInstance().build());
    }

    private DataFlowRequest createRequest(DataRequest dataRequest, DataAddress sourceAddress) {
        Map<String, String> parameterization = new HashMap<>();

        Object methodParameterization = dataRequest.getDataDestination().getProperties().get(ROOT_KEY + METHOD);
        if (methodParameterization != null) {
            parameterization.put(METHOD, methodParameterization.toString());
        }

        Object bodyParameterization = dataRequest.getDataDestination().getProperties().get(ROOT_KEY + BODY);
        if (bodyParameterization != null) {
            parameterization.put(BODY, bodyParameterization.toString());
        }

        Object mediaTypeParameterization = dataRequest.getDataDestination().getProperties().get(ROOT_KEY + MEDIA_TYPE);
        if (mediaTypeParameterization != null) {
            parameterization.put(MEDIA_TYPE, mediaTypeParameterization.toString());
        }

        Object pathParameterization = dataRequest.getDataDestination().getProperties().get(ROOT_KEY + PATH);
        if (pathParameterization != null) {
            parameterization.put(PATH, pathParameterization.toString());
        }

        Object queryParameterization = dataRequest.getDataDestination().getProperties().get(ROOT_KEY + QUERY_PARAMS);
        if (queryParameterization != null) {
            parameterization.put(QUERY_PARAMS, queryParameterization.toString());
        }

        return DataFlowRequest.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .processId(dataRequest.getProcessId())
                .trackable(true)
                .sourceDataAddress(sourceAddress)
                .destinationType(dataRequest.getDestinationType())
                .destinationDataAddress(dataRequest.getDataDestination())
                .callbackAddress(callbackUrl != null ? callbackUrl.get() : null)
                .properties(parameterization)
                .build();
    }
}
