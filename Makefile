clean:
	rm -rf classes

compile: clean
	mkdir -p classes
	javac -d classes src/*.java

benchmark: compile
	perf stat -d java -cp ./lib/*:./classes QueueBenchmark 16 1000000 custom

benchmark-java: compile
	perf stat -d java -cp ./lib/*:./classes QueueBenchmark 16 1000000 java