# Pulsar Stats

A quick little util to extract stats from a Pulsar cluster. No guarantees it won't blow up your cluster. Use at your own risk.

You can feed the JSON output into something like ChatGPT and get some usable info out of it. I tested it on `o4-mini-high` and had good luck with it.

Contributions are welcome.

## Usage

To build:
```
./gradlew :app:shadowJar
```

To use:
```
java -jar app/build/libs/app-fat.jar --server http://localhost:808 -f pulsar-stats.json
```