# Alpakka with IBM MQ Example

## Start IBM MQ Docker image

IBM provides a [Docker image](https://hub.docker.com/r/ibmcom/mq/) that runs MQ and sets up a few demonstration queues.
Our example application will send messages to these demonstration queues.

To start a Docker container based on this image, run:

```
$ docker run --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --publish 1414:1414 --publish 9443:9443 ibmcom/mq:9
```

Note that the `--env LICENSE=accept` argument indicates that you [accept the Docker image licenses](https://github.com/ibm-messaging/mq-docker#usage).
 
Once the container is running you'll see a message like:
```
IBM MQ Queue Manager QM1 is now fully running
```
or

```
Started web server
```

You can view the IBM MQ administration interface at [https://localhost:9443]().

Docker will continue to run the container until you terminate the process, for example by typing _Control-C_ in the terminal.
