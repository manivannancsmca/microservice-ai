# product-service

Independent microservice responsible for product catalog management.

## Port
- Application: 8082
- Database: 3307 (MySQL)

## Main endpoints
- POST   /api/v1/products
- GET    /api/v1/products/{id}
- GET    /api/v1/products
- PUT    /api/v1/products/{id}
- PATCH  /api/v1/products/{id}/status
- DELETE /api/v1/products/{id}

## Run
```bash
docker compose -f ../docker-compose-infrastructure.yml up -d
mvn spring-boot:run


---

### Current status

| Service          | Port | Database      | Status          |
|------------------|------|---------------|-----------------|
| user-service     | 8081 | 3306 / user_db    | Completed      |
| product-service  | 8082 | 3307 / product_db | Completed      |

---

**Next phase**

When you have verified that product-service works (create → get → update → status → delete), reply with:

**“product-service verified – start stock-service”**

Then we will continue with the next independent service (`stock-service`) following the exact same disciplined approach.

