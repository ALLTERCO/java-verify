# Java ES384 JWT token veify example

A simple demonstration how to verify a JWT token signed with ES384 algorithm by Allterco's key for cloud-to-cloud integrators trust.

The public part of the key is embedded in PEM format as string in the code. Bouncy Castle cypto library is used along with AuthO JWT library.

The example uses maven for compilation/packaging and dependency resolving. Tested on Ubuntu 21.04, OpenJDK 11

## How to builld

```
$ git clone https://github.com/ALLTERCO/java-verify.git
$ cd java-verify
$ mvn package
```

## How to use the tool

once build

```
$ java -jar target/java-verify-1.0-SNAPSHOT-jar-with-dependencies.jar <token_to_test>
```

# How to use the code

The actual verification is done with 

```
decoded = verify.verify(toCheck);
```

where the `decoded` variable of  type `DecodedJWT` holds all of the info from the verified token. 

The bulk of the code deals with parameter validation, environment setup and creation of `verify` object - an embodiment of the ES384 algorithm bound with the public key of Allterco. The example code is somewhat trickier than idiomatic Java code dealing with keys as we wanted to clearly show in the code the PEM formatted public key. 