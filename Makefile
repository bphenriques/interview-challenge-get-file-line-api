.PHONY: all clean compile lint test package

all: clean compile lint test docker-build

# Cleans the project.
clean:
	sbt clean

# Compiles the project
compile:
	sbt compile

# Runs Scala linter.
lint:
	sbt scalastyle

# Unit tests.
test:
	sbt test

# Builds docker image.
docker-build:
	sbt docker:publishLocal

# Start the line server.
start:
	cd docker && docker-compose up
