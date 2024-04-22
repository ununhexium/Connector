# Changelog

## [x.x.x] - UNRELEASED

### Overview

#### Why do we need this?

#### How does it work?

#### How do we resolve this conflict?


## [0.2.1.1] - 2024-04-22

### Overview

Adds a workaround to preserve the parameterization feature from pre-0.1 releases.

#### Requirements

Eclipse EDC before version `0.1` used a different communication protocol that was capable of sending the parameterization data to the provider.

This is not possible anymore due to the current IDS specification for [transfer messages](https://docs.internationaldataspaces.org/ids-knowledgebase/v/dataspace-protocol/transfer-process/transfer.process.protocol#21-transfer-request-message).

This feature is needed by one of our clients.

#### Implementation

The only field (as of IDS version `2024-01`) that lets us send data in the concerned message is the `DataAddress`'s properties. This is the field, in combination with specific properties, that we use in this workaround to transfer the missing information to the Provider.
This extra information is extracted on the Provider's side and put back where it used to be for consumption as it used to be in earlier versions.

With this workaround, a parameterized asset can be requested with the following query:

```json
{
  "@type": "https://w3id.org/edc/v0.0.1/ns/TransferRequest",
  "https://w3id.org/edc/v0.0.1/ns/assetId": "{{ASSET_ID}}",
  "https://w3id.org/edc/v0.0.1/ns/contractId": "{{CONTRACT_ID}}",
  "https://w3id.org/edc/v0.0.1/ns/connectorAddress": "https://{{PROVIDER_EDC_FQDN}}/api/dsp",
  "https://w3id.org/edc/v0.0.1/ns/connectorId": "{{PROVIDER_EDC_PARTICIPANT_ID}}",
  "https://w3id.org/edc/v0.0.1/ns/dataDestination": {
    "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
    "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{DATA_SINK_URL}}",
    "https://sovity.de/workaround/proxy/param/pathSegments": "{{PARAMETERIZATION_PATH}}",
    "https://sovity.de/workaround/proxy/param/method": "{{PARAMETERIZATION_METHOD}}",
    "https://sovity.de/workaround/proxy/param/queryParams": "{{PARAMETERIZATION_QUERY}}",
    "https://sovity.de/workaround/proxy/param/mediaType": "{{PARAMETERIZATION_CONTENTTYPE}}",
    "https://sovity.de/workaround/proxy/param/body": "{{PARAMETERIZATION_BODY}}"
  },
  "https://w3id.org/edc/v0.0.1/ns/privateProperties": {},
  "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
  "https://w3id.org/edc/v0.0.1/ns/managedResources": false
}
```

Where the `https://sovity.de/workaround/proxy/param/*` carry the parameterization data.

#### Resolution plan

There is a ticket open on the [IDS side](https://github.com/International-Data-Spaces-Association/ids-specification/discussions/262)

The goal is to
* have this feature standardize
* have it implemented in core EDC
* use the new core EDC version

#### Compatibility

The parameterization feature will only work between 2 EDCs that use this forked version.

Expecting no incompatibilities with core EDC 0.2.1.
