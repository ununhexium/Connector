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
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.METHOD;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.PATH;

class SovityBaseSourceHttpParamsDecoratorTest {

    private final String ROOT_KEY = "https://sovity.de/workaround/proxy/param/";

    @Test
    void shouldFindHttpMethodParam() {
        // arrange
        final var method = "METHOD";
        final var dstAddress = HttpDataAddress.Builder.newInstance()
                .property("https://w3id.org/edc/v0.0.1/ns/proxyMethod", "true")
                .build();
        final var edcRequest = processBuilderPreFilled(dstAddress)
                .properties(Map.of(METHOD, method))
                .build();
        final var decorator = new BaseSourceHttpParamsDecorator();
        final var params = HttpRequestParams.Builder.newInstance().baseUrl("http://example.com");

        // act
        final var httpRequest = decorator
                .decorate(edcRequest, dstAddress, params)
                .build();

        // assert
        assertThat(httpRequest.getMethod()).isEqualTo(method);
    }

    @Test
    void shouldFindThePathParam() {
        // arrange
        final var path = "segment1/segment2/segment3";
        final var dstAddress = HttpDataAddress.Builder.newInstance()
                .property("https://w3id.org/edc/v0.0.1/ns/proxyPath", "true")
                .build();
        final var edcRequest = processBuilderPreFilled(dstAddress)
                .properties(Map.of(PATH, path))
                .build();
        final var decorator = new BaseSourceHttpParamsDecorator();
        final var params = HttpRequestParams.Builder.newInstance().baseUrl("http://example.com/base");

        // act
        final var httpRequest = decorator
                .decorate(edcRequest, dstAddress, params)
                .build();

        // assert
        assertThat(httpRequest.getPath()).isEqualTo(path);
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