.PHONY: help test fmt fmt-check lint verify clean

help: ## Show this help message
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'

test: ## Run tests (H2 in-memory DB via Quarkus test profile)
	mvn test

fmt: ## Auto-format code with Spotless (google-java-format AOSP)
	mvn spotless:apply

fmt-check: ## Check formatting without making changes (use in CI)
	mvn spotless:check

lint: fmt-check ## Alias for fmt-check

verify: fmt-check test ## Run format check + full test suite

clean: ## Remove build output
	mvn clean
