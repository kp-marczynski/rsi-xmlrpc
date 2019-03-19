# Sample XML-RPC app
Realisation of task for Uni 'Distributed IT Systems' course

## How to build
    gradlew generateJars
    
## Example how to run
    java -jar serverRpc.jar -n MojSerwer -p 1000 
    java -jar clientRpc.jar -n MojSerwer -p 1000 -i localhost -a -m race 1000
    
## Cli options
Server:

    -n,--server-name <arg>   server name
    -p,--port <arg>          server port
    
Client:

    -a,--async               specifying if method should be called asynchronously
    -i,--ip <arg>            server ip
    -m,--method <arg>        server method
    -n,--server-name <arg>   server name
    -p,--port <arg>          server port
