# Xenon

A API/Library for analyzing data coming out of Adomni

# Deploying the lambda
mvn package lambda:deploy-lambda

# Working on the request object
http://docs.aws.amazon.com/lambda/latest/dg/java-handler-using-predefined-interfaces.html

# TODO


# Info on configuring lambda triggers 

---

                        "triggers": [
                        {
                        "integration": "CloudWatch Events - Schedule",
                        "ruleName": "every-minute",
                        "ruleDescription": "foo bar",
                        "scheduleExpression": "rate(1 minute)"
                        },
                        {
                        "integration": "DynamoDB",
                        "dynamoDBTable": "myTable",
                        "batchSize": 100,
                        "startingPosition": "TRIM_HORIZON"
                        },
                        {
                        "integration": "Kinesis",
                        "kinesisStream": "myStream",
                        "batchSize": 100,
                        "startingPosition": "TRIM_HORIZON"
                        },
                        {
                        "integration": "SNS",
                        "SNSTopic": "SNSTopic-1"
                        },
                        {
                        "integration": "SNS",
                        "SNSTopic": "SNSTopic-2"
                        },
                        {
                        "integration": "Alexa Skills Kit"
                        }
                        ],
                        "environmentVariables": {
                        "key1": "value1",
                        "key2": "value2"
                        }
                        },
                        {
                        "functionName": "my-function-name-1",
                        "description": "I am awesome function too",
                        "handler": "no.flowlab.lambda1"
                        }
                        ]
                        
                        
                        <functionNameSuffix>${lambda.functionNameSuffix}</functionNameSuffix>
                                            