# Rapidw MQTT Codec

[![Build Status](https://travis-ci.org/rapidw/mqtt-codec.svg?branch=master)](https://travis-ci.org/rapidw/mqtt-codec)
[![Maven Central](http://img.shields.io/maven-central/v/io.rapidw.mqtt/rapidw-mqtt-codec)](https://search.maven.org/artifact/io.rapidw.mqtt/rapidw-mqtt-codec)
[![Bintray](http://img.shields.io/bintray/v/rapidw/maven/rapidw-mqtt-codec)](https://bintray.com/rapidw/maven/rapidw-mqtt-codec/_latestVersion)
[![License](https://img.shields.io/github/license/rapidw/mqtt-codec)](https://github.com/rapidw/mqtt-codec/blob/master/LICENSE)

a MQTT 3.1.1 codec implemented by Java & Netty

R.I.P Dr.Li WenLiang

- Fast
- Easy of use
- Fully compliant to MQTT 3.1.1

# Packet Supported
- [x] CONNECT
- [x] CONACK
- [x] PUBLISH
- [x] PUBACK
- [ ] PUBREC
- [ ] PUBREL
- [ ] PUBCOMP
- [x] SUBSCRIBE
- [x] SUBACK
- [x] UNSUBSCRIBE
- [x] UNSUBACK
- [x] PINGREQ
- [x] PINGRESP
- [x] DISCONNECT

## Benchmark

- Core i7 8700K @3.70GHz
- Windows 10 x64 1909
- JDK 1.8.0_201
- JMH 1.21

```
Benchmark                               Mode  Cnt        Score        Error  Units
BenchmarkRunner.benchmarkNettyDecoder  thrpt   25  1509941.913 ±  21972.901  ops/s
BenchmarkRunner.benchmarkNettyEncoder  thrpt   25  3411239.721 ± 114748.424  ops/s
BenchmarkRunner.benchmarkWaferDecoder  thrpt   25  1503838.785 ±  12059.573  ops/s
BenchmarkRunner.benchmarkWaferEncoder  thrpt   25  3239811.040 ±  45346.984  ops/s
```
