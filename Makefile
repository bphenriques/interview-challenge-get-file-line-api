.PHONY: all clean compile lint test package

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

# Start the line server. Use FILE=<path> make start
start:
	cd docker && docker-compose up
