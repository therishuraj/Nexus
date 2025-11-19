# Bruno Collection for Funding MS

This folder contains Bruno (.bru) request files to exercise the Funding Microservice APIs.

## Structure
- environments/local.envbru : Local environment variables
- 01-create-funding-request.bru : POST create funding request
- 02-get-funding-views.bru : GET list views by funderId
- 03-funding-graphql.bru : POST GraphQL query

## Usage
1. Open this folder in Bruno GUI ("Open Collection").
2. Select environment: Local.
3. Execute requests in sequence:
   - Create Funding Request (copy returned id)
   - Funding GraphQL (paste id in query or use sample)
   - Get Funding Views (ensure funderId matches earlier request)

### Bruno CLI (optional)
```bash
bruno run . --env local
```

## Notes
- Replace the sample password and cluster details if different.
- Ensure the service is running at http://localhost:3003.
- Timestamps must be in ISO-8601 (yyyy-MM-dd'T'HH:mm:ss).

