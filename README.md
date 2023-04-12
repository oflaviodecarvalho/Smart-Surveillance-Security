# Smart-Surveillance-Security
CA1 Distributed Systems

# Setup
### Requirements
- Intellij IDEA Community
- Java 8+ (This project uses 11)
- Basic Java Knowledge

### Project Structure
    SmartGrpcServer (Root Folder)
     -src(source files)
      -main(server project files)
       -proto(protobuf files)
      -test(tests)
       -proto(protobuf test files)
      -client(client project files)
### Running the project

* Open the project with intellij IDEA
* Connect to the internet and wait for gradle build to finish successfully
* Click on Build->Build Project. This is necessary to compile the proto files.
* Switch to src->test->java->SmartGrpcServerTest.java
* Run the tests.All tests should be passing


### Milestones

- Project Setup ****** Done
- Service Setup ***** Done
- Proto Design ***** Done
- Server Tests ***** Done
- Server Implementation ***** Done
- UI ***** TODO
- Client ***** Doing
- jmDNS ****** TODO