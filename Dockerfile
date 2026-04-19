FROM eclipse-temurin:24-jdk-alpine

WORKDIR /app

# Install Node.js, npm, and bash
RUN apk add --no-cache nodejs npm bash

# Copy the entire project source into the container
COPY . .

# Build the Frontend
RUN cd frontend && \
    npm ci && \
    npm run build

# Move frontend files to backend static resources
RUN mkdir -p backend/src/main/resources/static && \
    if [ -d "frontend/dist/frontend/browser" ]; then \
        cp -r frontend/dist/frontend/browser/* backend/src/main/resources/static/; \
    else \
        cp -r frontend/dist/frontend/* backend/src/main/resources/static/; \
    fi

# Build the Backend
RUN chmod +x gradlew && \
    ./gradlew :backend:build -x test

# Prepare the final JAR for execution
RUN cp backend/build/libs/*-SNAPSHOT.jar app.jar

# Standard runtime configuration
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
