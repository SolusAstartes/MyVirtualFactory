This protocol is a Client-Server type protocol. It has a single server and serves two types of Clients. These are the Planner and the Machine.
Although the sample code is written using the TCP infrastructure and the DataOutputStream structure of the Java programming language, the protocol is suitable for writing in other languages.

The messages of the protocol consist of a body and a header. Protocol messages are not text-based, but in a block structure of variable length binary data.
Since there is no user type information in the protocol message, it is the user's responsibility to send the correct messages to the server by the appropriate user type.