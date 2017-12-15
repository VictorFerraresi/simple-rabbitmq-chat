# Simple RabbitMQ Chat
A simple RabbitMQ Chat room using Java

## Compiling and Running

1. Have RabbitMQ and it's dependencies installed and running (I've used the 3.7.0 version for this)
2. Clone the repository
   > https://help.github.com/articles/cloning-a-repository/
3. Travel to the src path `(cd simple-rabbitmq-chat/main/src)`
4. Edit the RabbitMQ instance IP inside the factory.setHost("localhost") call
5. Build the Chatter.java files:
```javac -cp amqp-client-4.0.2.jar Chatter.java```
6. Run how many Chatters you want.
```java -cp .;amqp-client-4.0.2.jar;slf4j-api-1.7.21.jar;slf4j-simple-1.7.22.jar Chatter```

7. Choose a nickname and start typing messages at the console.
~~8. I'm aware of the exception when you Ctrl+C and I'll fix it soon.~~  It's fixed
