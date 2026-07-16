.PHONY: clean spotless build clean-build docker-up docker-down docker-logs logs help

clean:
	./gradlew clean

spotless:
	./gradlew spotlessApply

build:
	./gradlew spotlessApply build

clean-build:
	./gradlew clean spotlessApply build

docker-up:
	docker compose up -d

docker-down:
	docker compose down

docker-logs:
	docker compose logs -f

logs: docker-logs

help:
	@echo "Available targets:"
	@echo "  make clean        Run Gradle clean"
	@echo "  make spotless     Run Gradle spotlessApply"
	@echo "  make build        Run Gradle spotlessApply build"
	@echo "  make clean-build  Run Gradle clean spotlessApply build"
	@echo "  make docker-up    Start Docker Compose services"
	@echo "  make docker-down  Stop Docker Compose services"
	@echo "  make docker-logs  Follow Docker Compose logs"
	@echo "  make logs         Alias for docker-logs"
