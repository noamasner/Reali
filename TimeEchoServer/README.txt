The server exposes echoAtTime as a HTTP GET method with 2 parameters, message and timestamp.
The timestamp can be either in seconds or milliseconds. For second use "tssec" and for milliseconds use "tsms".
Example:
/echoAtTime?message=<message>&tssec=<timestamp> OR
/echoAtTime?message=<message>&tsms=<timestamp>
If you use both tssec & tsms, then the first one will be used.

To start the program, you have to specify the server http port, redis host & redis port.
Usage:    TimeEchoServer [server_port] [redis_host] [redis_port]