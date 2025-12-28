# Monitoring Service – API Design (V2)

## Overview
Create a comprehensive REST API design document for the Monitoring UI backend services. This document serves as a reference for developers implementing the APIs and for frontend teams consuming them.

## Scope
This document describes REST endpoints that will be consumed by the Monitoring UI and potentially other clients. It covers the following main resource areas:
- Document Requests: searching, retrieving metadata, blobs, batches, and errors.
- Reference Data: CRUD operations for various reference data types.
- Document Configurations: CRUD operations for document configuration settings.

Base URL: `/monitoring-service/api/v1`

---
## APIs

### 1. Reference Data

#### 1.1 Create Reference Data
- Method: `POST`
- Endpoint: `/reference-data`
- Purpose: Create new reference data.
- Request Body:
```json
{
  "refDataType": "DOCUMENT_TYPE",
  "refDataValue": "Invoice",
  "description": "Invoice document refDataType",
  "editable": true,
  "effectFromDat": "2024-06-01",
  "effectToDat": "4712-12-31"
}
```
- Response:
```json
{
  "id": 101,
  "refDataType": "DOCUMENT_TYPE",
  "refDataValue": "Invoice",
  "description": "Invoice document refDataType",
  "editable": true,
  "effectFromDat": "2024-06-01",
  "effectToDat": "4712-12-31",
  "createdDat": "2024-06-01T10:00:00Z",
  "lastUpdateDat": "2024-06-01T10:00:00Z",
  "create_uid": "GBSURT1",
  "last_update_uid": "GBSURT1"
}
```

#### 1.2 Update Reference Data
- Method: `PUT`
- Endpoint: `/reference-data/{id}`
- Purpose: Update reference data by id.
- Request Body: same fields as above; only updatable fields required.
- Response: same as create.

#### 1.3 Logical Delete Reference Data
- Method: `DELETE`
- Endpoint: `/reference-data/{id}`
- Purpose: Delete a reference data by id.
- Response: `204 No Content`

#### 1.5 Get Reference Data by ID
- Method: `GET`
- Endpoint: `/reference-data/{id}`
- Purpose: Retrieve reference data by id.
- Response: single object as per create.

#### 1.6 Get Reference Data by Type
- Method: `GET`
- Endpoint: `/reference-data/type/{type}`
- Purpose: Retrieve list of reference data for a type (entire set is returned; UI handles pagination, sorting, searching client-side).
- Response:
```json
[
  {
    "id": 101,
    "refDataType": "DOCUMENT_TYPE",
    "refDataValue": "Invoice",
    "description": "Invoice document refDataType",
    "editable": true,
    "effectFromDat": "2024-06-01",
    "effectToDat": "4712-12-31",
    "createdDat": "2024-06-01T10:00:00Z",
    "lastUpdateDat": "2024-06-01T10:00:00Z",
    "create_uid": "GBSURT1",
    "last_update_uid": "GBSURT1"
  },
  {
    "id": 102,
    "refDataType": "DOCUMENT_TYPE",
    "refDataValue": "Credit Note",
    "description": "Credit Note document refDataType",
    "editable": true,
    "effectFromDat": "2024-06-01",
    "effectToDat": "4712-12-31",
    "createdDat": "2024-06-01T10:05:00Z",
    "lastUpdateDat": "2024-06-01T10:05:00Z",
    "create_uid": "GBSURT1",
    "last_update_uid": "GBSURT1"
  }
]
```

---
### 2. Document Configuration APIs

#### 2.1 Create Document Configuration
- Method: `POST`
- Endpoint: `/document-configurations`
- Purpose: Create new document configuration.
- Request Body:
```json
{
  "omrdaFooterId": 10,
  "omrdaAppDocSpecId": 14,
  "omrdaCodeId": 18,
  "value": "GBVKAL1",
  "description": "First signee for footer 1, app_doc_spec IV.",
  "effectFromDat": "2024-06-01",
  "effectToDat": "4712-12-31"
}
```
- Response:
```json
{
  "id": 1,
  "footer": { "id": 10, "refDataValue": "1", "description": "Footer Id 1" },
  "appDocSpec": { "id": 14, "refDataValue": "IV", "description": "Invoice document refDataValue starting with IV" },
  "code": { "id": 18, "refDataValue": "SIGNEE_1", "description": "First signature" },
  "value": "GBVKAL1",
  "description": "First signee for footer 1, app_doc_spec IV.",
  "effectFromDat": "2024-06-01",
  "effectToDat": "4712-12-31",
  "createdDat": "2024-06-01T10:05:00Z",
  "lastUpdateDat": "2024-06-01T10:05:00Z",
  "create_uid": "GBSURT1",
  "last_update_uid": "GBSURT1"
}
```

#### 2.2 Update Document Configuration
- Method: `PUT`
- Endpoint: `/document-configurations/{id}`
- Purpose: Update document configuration by id.
- Request Body: fields as above.
- Response: same as create.

#### 2.3 Logical Delete Document Configuration
- Method: `DELETE`
- Endpoint: `/document-configurations/{id}`
- Purpose: Delete document configuration by id.
- Response: `204 No Content`

#### 2.4 List All Document Configurations
- Method: `GET`
- Endpoint: `/document-configurations`
- Purpose: List all document configurations (full set returned; UI handles pagination/sorting/search client-side).
- Response:
```json
[
  {
    "id": 1,
    "footer": { "id": 10, "refDataValue": "1", "description": "Footer Id 1" },
    "appDocSpec": { "id": 14, "refDataValue": "IV", "description": "Invoice document refDataValue starting with IV" },
    "code": { "id": 18, "refDataValue": "SIGNEE_1", "description": "First signature" },
    "value": "GBVKAL1",
    "description": "First signee for footer 1, app_doc_spec IV.",
    "effectFromDat": "2024-06-01",
    "effectToDat": "4712-12-31",
    "createdDat": "2024-06-01T10:05:00Z",
    "lastUpdateDat": "2024-06-01T10:05:00Z",
    "create_uid": "GBSURT1",
    "last_update_uid": "GBSURT1"
  },
  {
    "id": 2,
    "footer": { "id": 11, "refDataValue": "2", "description": "Footer Id 2" },
    "appDocSpec": { "id": 15, "refDataValue": "PO", "description": "Policy document starting with PO" },
    "code": { "id": 19, "refDataValue": "SIGNEE_2", "description": "Second signature" },
    "value": "GBVKAL2",
    "description": "Second signee for footer 2, app_doc_spec CN.",
    "effectFromDat": "2024-06-01",
    "effectToDat": "4712-12-31",
    "createdDat": "2024-06-01T10:10:00Z",
    "lastUpdateDat": "2024-06-01T10:10:00Z",
    "create_uid": "GBSURT1",
    "last_update_uid": "GBSURT1"
  }
]
```

#### 2.5 Get Document Configuration by ID
- Method: `GET`
- Endpoint: `/document-configurations/{id}`
- Purpose: Retrieve document configuration by id.
- Response: single configuration object.

#### 2.6 Get by Footer/App/Code Combination
- Method: `GET`
- Endpoint: `/document-configurations/search?footer=0&documentName=IVBRKCOM&code=SIGNEE_1`
- Purpose: Retrieve a document configuration based on a specific combination of footer ID, app document specification ID during document processing.
- Response: list of matching configuration objects same as list all.

---
### 3. Document Request APIs

#### 3.1 List Document Requests
- Method: `GET`
- Endpoint: `/document-requests?page=1&size=10`
- Purpose: Search document requests with flexible filters, pagination, and sorting. Supports both simple and advanced search scenarios.
- Request body (simple search):
```json
{
  "documentStatuses": [10],
  "fromDate": "2025-11-01",
  "toDate": "2025-11-18",
  "sorts": [ { "property": "id", "dir": "description" } ]
}
```
- Request body (advanced search with metadata and multiple ids):
```json
{
  "sourceSystems": [1, 3],
  "documentTypes": [2],
  "documentNames": [5, 6],
  "documentStatuses": [10],
  "requestIds": [101, 102],
  "batchIds": [5555, 6666],
  "fromDate": "2025-11-01",
  "toDate": "2025-11-18",
  "metadataChips": [ { "keyId": 2001, "value": "INV-2024" } ],
  "sorts": [ { "property": "id", "direction": "DESC" }, { "property": "createdDat", "direction": "ASC" } ]
}
```
- Notes:
  - Filters are combined with AND semantics. For example, providing both `requestIds` and `batchIds` will return requests whose id is in `requestIds` AND that have at least one batch whose id is in `batchIds`.
  - To avoid excessively large SQL IN clauses, implementations may cap `requestIds` and `batchIds` to a reasonable maximum (e.g., 100 values).
- Response:
```json
{
  "content": [
    {
      "id": 101,
      "sourceSystem": { "id": 1, "refDataValue": "ARCADE", "description": "Arcade" },
      "documentType": { "id": 2, "refDataValue": "INVOICE", "description": "Invoice documents" },
      "documentName": { "id": 5, "refDataValue": "IVZRECPA", "description": "Zero reconciliation and premium adjustment invoice document" },
      "documentStatus": { "id": 10, "refDataValue": "COMPLETED", "description": "Document request processed successfully" },
      "createdDat": "2025-11-18T12:04:55.000",
      "lastUpdateDat": "2025-11-18T12:05:10.000",
      "create_uid_header": "GBSURT1",
      "create_uid_token": "GBSURT1"
    },
    {
      "id": 102,
      "sourceSystem": { "id": 3, "refDataValue": "THUNDERHEAD", "description": "Thunderhead" },
      "documentType": { "id": 2, "refDataValue": "INVOICE", "description": "Invoice documents" },
      "documentName": { "id": 6, "refDataValue": "IVBRKCOM", "description": "Broker commission invoice document" },
      "documentStatus": { "id": 12, "refDataValue": "FAILED", "description": "Document request processing failed" },
      "createdDat": "2025-11-18T11:50:30.000",
      "lastUpdateDat": "2025-11-18T11:52:00.000",
      "create_uid_header": "GBSURT2",
      "create_uid_token": "GBSURT2"
    }
  ],
  "page": 3,
  "size": 10,
  "totalElements": 174,
  "totalPages": 18,
  "first": false,
  "last": false,
  "sorts": [
    { "property": "id", "direction": "DESC" },
    { "property": "createdDat", "direction": "ASC" }
  ],
  "links": {
    "self": "/document-requests/search?page=3&size=10",
    "first": "/document-requests/search?page=1&size=10",
    "previous": "/document-requests/search?page=2&size=10",
    "next": "/document-requests/search?page=4&size=10",
    "last": "/document-requests/search?page=18&size=10"
  }
}
```

#### 3.2 Get Metadata by Document Request ID
- Method: `GET`
- Endpoint: `/document-requests/{id}/metadata`
- Purpose: Retrieve metadata associated with document request.
- Response:
```json
[
  { 
    "id": 301,
    "metadataKey": { "id": 3, "refDataValue": "customerId", "description": "Customer Identifier" },
    "metadataValue": "12345"
  },
  { 
    "id": 302,
    "metadataKey": { "id": 4, "refDataValue": "invoiceNumber", "description": "Invoice Number" },
    "metadataValue": "INV-2024-0001"
  }
]
```

#### 3.3 Get JSON/XML Document Request Content
- Method: `GET`
- Endpoint: `/document-requests/{id}/content/json`
- Purpose: Retrieve the original JSON request payload associated with document request.
- Response: JSON payload in byte array.
```json
{
  "content": ""  
}
```

- Method: `GET`
- Endpoint: `/document-requests/{id}/content/xml`
- Purpose: Retrieve the final XML payload associated with document request.
- Response: Raw XML payload in byte array.
```json
{
  "content": ""
}
```

#### 3.4 Get Thunderhead Batches by Document Request ID
- Method: `GET`
- Endpoint: `/document-requests/{id}/th-batches`
- Purpose: Retrieve batches associated with a document request.
- Response:
```json
[
  {
    "id": 12,
    "requestId": 101,
    "batch_id": 76647,
    "batchStatus": { "id": 10, "refDataValue": "COMPLETED", "description": "Batch processed successfully" },
    "batchName": "TH-REQ-101-1",
    "dmsDocumentId": 8888,
    "syncStatus": 1,
    "eventStatus": 1,
    "retryCount": 0,
    "createdDat": "2025-11-18T10:22:33.000"
  },
  {
    "id": 13,
    "requestId": 101,
    "batch_id": 76648,
    "batchStatus": { "id": 12, "refDataValue": "FAILED", "description": "Batch processing failed" },
    "batchName": "TH-REQ-101-2",
    "dmsDocumentId": null,
    "syncStatus": 0,
    "eventStatus": 0,
    "retryCount": 1,
    "createdDat": "2025-11-18T10:25:00.000"
  }
]
```

#### 3.5 Get Error Details by Batch ID
- Method: `GET`
- Endpoint: `/th-batches/{batchId}/errors`
- Purpose: Retrieve error details for a batch.
- Response:
```json
[
  { 
    "id": 501,
    "errorCategory": "VALIDATION_ERROR", 
    "errorDescription": "Missing required field"
  },
  { 
    "id": 502,
    "errorCategory": "PROCESSING_ERROR", 
    "errorDescription": "Failed to connect to DMS"
  }
]
```

#### 3.6 Initiate Document Request Reprocessing
- Method: `POST`
- Endpoint: `/document-requests/reprocess`
- Purpose: Initiate reprocessing for selected document requests.
- Request Body:
```json
{ 
  "requestIds": [1232, 34645]
}
```
- Response:
```json
{ 
  "message": "Request/s submitted for reprocessing."
}
```

#### 3.7 Document Requests Summary
- Method: `GET`
- Endpoint: `/document-requests/summary?fromDate=2025-11-01&toDate=2025-11-18`
- Purpose: Get aggregated statistics for dashboard display—returns count of requests per status and total count.
- Response:
```json
{
  "totalCount": 347,
  "statusCounts": [
    { 
      "statusId": 12, 
      "statusName": "COMPLETED", 
      "statusDescription": "Request completed successfully", 
      "count": 15
    },
    { 
      "statusId": 13, 
      "statusName": "FAILED", 
      "statusDescription": "Request failed", 
      "count": 5
    }
  ]
}
```

---
## General Notes
- All endpoints require authentication as per Atradius standards.
- Standard HTTP status codes are used.
- Pagination, filtering, and sorting for Document Requests will be handled in the backend.
- Pagination, filtering, and sorting for Reference Data and Document Configurations will be handled client-side (UI).

---
## Appendix: API Summary Table

| #  | API Description                                   | Method | Endpoint                             |
|----|---------------------------------------------------|--------|--------------------------------------|
| 1  | Create reference data                             | POST   | /reference-data                      |
| 2  | Update reference data                             | PUT    | /reference-data/{id}                 |
| 3  | Delete reference data (logical)                   | DELETE | /reference-data/{id}                 |
| 4  | List all reference data                           | GET    | /reference-data                      |
| 5  | Get reference data by ID                          | GET    | /reference-data/{id}                 |
| 6  | Get reference data by type                        | GET    | /reference-data/type/{type}          |
| 7  | Create document configuration                     | POST   | /document-configurations             |
| 8  | Update document configuration                     | PUT    | /document-configurations/{id}        |
| 9  | Delete document configuration (logical)           | DELETE | /document-configurations/{id}        |
| 10 | List all document configuration                   | GET    | /document-configurations             |
| 11 | Get document configuration by ID                  | GET    | /document-configurations/{id}        |
| 12 | Get document config by footerId, appDocSpec, code | GET    | /document-configurations/search      |
| 13 | List document requests (filter/sort/paginate)     | GET    | /document-requests                   |
| 14 | Get metadata by document request ID               | GET    | /document-requests/{id}/metadata     |
| 15 | Get JSON content by document request ID           | GET    | /document-requests/{id}/content/json |
| 16 | Get XML content by document request ID            | GET    | /document-requests/{id}/content/xml  |
| 17 | Get thunderhead batches by document request ID    | GET    | /document-requests/{id}/th-batches   |
| 18 | Get error details by thunderhead batch ID         | GET    | /th-batches/{batchId}/errors         |
| 19 | Initiate document request reprocessing            | POST   | /document-requests/reprocess         |
| 20 | Document request summary                          | GET    | /document-requests/summary           |
