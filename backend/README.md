# Wedding Backend (Spring Boot)

This module contains a simple Spring Boot REST API for the wedding site.

## Features

- MongoDB persistence for gallery items and RSVPs
- JWT-based admin authentication (skeleton only)
- Cloudinary integration for media uploads (not implemented)

## Build & Run

Make sure you have JDK 17 and Maven installed.

```powershell
cd "d:\akka wedding\backend"
# build jar
mvn clean package
# run
java -jar target/wedding-backend-0.0.1-SNAPSHOT.jar
```

## Notes

- Replace MongoDB URI, JWT secret, and Cloudinary credentials in `application.properties` or provide them through environment variables.
  - by default the app now uses `mongodb://localhost:27017/wedding` so you can run a local MongoDB instance for testing.
  - to connect to Atlas set `MONGODB_URI` environment variable or update the property.
- File upload endpoint `/api/gallery/upload` handles multipart files and stores them under `backend/uploads`; media is served at `/uploads/...`.
- Security configuration and JWT filter are not yet included; add `SecurityConfig` and `JwtUtils` classes as needed.

## Development

Use your IDE to edit and run the project. The API base path is `/api`.
