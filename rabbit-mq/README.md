# Alpakka with RabbitMQ Example

## Start RabbitMQ Docker image

The Docker community provides a [Docker image](https://hub.docker.com/_/rabbitmq/) that runs RabbitMQ.
Our example application will create some demonstration queues and send messages to them.

To start a Docker container based on this image, run:

```
$ docker-compose up amqp
```
 
Once the container is running you'll see a message like:

```
Server startup complete
```

Docker will continue to run the container until you terminate the process, for example by typing _Control-C_ in the terminal.
