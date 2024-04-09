/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package org.eclipse.edc.connector.dataplane.http.params.decorators;

import org.eclipse.edc.connector.dataplane.http.spi.HttpRequestParams;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.HttpDataAddress;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SovityBaseSourceHttpParamsDecoratorTest {

    @Test
    void shouldFindTheHttpMethodOnProxyTransfers() {
        // arrange
        final var method = "METHOD";
        final var address = HttpDataAddress.Builder.newInstance()
                .property("https://w3id.org/edc/v0.0.1/ns/proxyMethod", "true")
                .properties(Map.of("https://sovity.de/method", method))
                .build();
        final var edcRequest = processBuilderPreFilled(address)
                .build();
        final var decorator = new BaseSourceHttpParamsDecorator();
        final var params = HttpRequestParams.Builder.newInstance().baseUrl("http://example.com");

        // act
        final var httpRequest = decorator
                .decorate(edcRequest, address, params)
                .build();

        // assert
        assertThat(httpRequest.getMethod()).isEqualTo(method);
    }

    private static DataFlowRequest.Builder processBuilderPreFilled(DataAddress destinationDataAddress) {
        return DataFlowRequest.Builder.newInstance()
                .processId("processId")
                .sourceDataAddress(dataAddressBuilderPreFilled().build())
                .destinationDataAddress(destinationDataAddress)
                .traceContext(Map.of());
    }

    private static <B extends DataAddress.Builder<DataAddress, B>> B dataAddressBuilderPreFilled() {
        return DataAddress.Builder.<B>newInstance().type("type");
    }
}