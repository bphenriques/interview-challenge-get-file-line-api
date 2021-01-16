.PHONY: all clean compile lint test package

# Cleans the project.
clean:
	sbt clean

# Compiles the project
compile:
	sbt compile

# Runs Scala linter.
lint:
	sbt scalafmtSbt

# Formats source files according to the Scala linter.
# Skips sbt files because the whitespace matters specifically in the Dependencies file.
lintFormat:
	sbt scalafmt


# Unit tests.
test:
	sbt test

# Builds docker image.
docker-build:
	sbt docker:publishLocal

# Start the line server. Use FILE=<path> make start
start:
	cd docker && docker-compose up
