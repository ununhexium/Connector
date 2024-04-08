package org.eclipse.edc.connector.dataplane.http.params.decorators;

import org.eclipse.edc.connector.dataplane.http.spi.HttpRequestParams;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.HttpDataAddress;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SovityBaseSourceHttpParamsDecoratorTest {

    @Test
    void shouldFindTheHttpMethodOnProxyTransfers() {
        // arrange
        final var METHOD = "METHOD";
        final var address = HttpDataAddress.Builder.newInstance()
                .property("https://w3id.org/edc/v0.0.1/ns/proxyMethod", "true")
                .properties(Map.of("https://sovity.de/method", METHOD))
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
        assertThat(httpRequest.getMethod()).isEqualTo(METHOD);
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