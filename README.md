# Email Service
Create simple but beautiful email templates and send email using REST. The idea is to keep things simple, without having to write complicated HTML or CSS files. A simple JSON object is all you need to define a new template!
Integrates seamlessly with [Mail Gun](https://www.mailgun.com/).

## Features
* RESTful
* Responsive HTML
* Dynamic Templates
* Global Variables
* Conditional Expressions
* Internationalization
* Fine-grained Permissions

## Setup
A configuration file is required to let the service connect to the MongoDB database and the RailGun API. Create a file in the same folder as the `.jar`-file called `application.properties` with the following content:
```properties
# MongoDB Settings
spring.data.mongodb.database = emaildb
spring.data.mongodb.host     = localhost
spring.data.mongodb.port     = 27017

# MailChimp Settings
mailgun.apiKey =
mailgun.domain =
```

To set up the initial user (with all permissions set), run the jar file like this:
```shell
java -jar email-service.jar --init -u admin -p password
```
The user will be saved in the MongoDB database so it doesn't have to be created each time.

## API Examples
Here are some examples on how to use the service. All commands require a valid API Key to be specified using Basic Authentication. 

### Set Option
Define global variables to be used in all templates:

`POST /options/{name}`
```json
{
    "value" : "..."
}
```

### Create a Template
Create a new template with support for two languages:

`POST /templates`
```json
{
    "name"     : "registerConfirm",
    "sender"   : "Membaza",
    "email"    : "noreply@membaza.com",
    "replyTo"  : "info@membaza.com",
    "languages": {
        "en": {
            "_subject": "${firstname?Hi, {firstname}!|Hello!}!",
            "p1"      : "Welcome to ${sitename}!",
            "p2"      : "Did you get this by mistake?",
            "create"  : "Create Account",
            "report"  : "Let us know!"
        },
        "sv": {
            "_subject": "${firstname?Hej, {firstname}!|Hejsan!}!",
            "p1"      : "Välkommen till ${sitename}!",
            "p2"      : "Fick du detta av misstag?",
            "create"  : "Skapa Konto",
            "report"  : "Anmäl detta!"
        }
    },
    "content": [
        {"type": "title",  "value": "${subject}"},
        {"type": "text",   "value": "${p1}"},
        {"type": "button", "value": "${create}", "href": "${url.verify}"},
        {"type": "text",   "value": "${p2} <a href=\"${report}\">${url.cancel}</a>"},
    ]
}
```

### Send an Email
Send an email to a single person using the template defined above.

`POST /send?lang=sv`
```json
{
    "template"  : "registerConfirm",
    "recipients": [
        {"name": "Emil Forslund", "email": "emil.duncan@gmail.com"}
    ],
    "args" : {
        "firstname" : "Emil",
        "lastname"  : "Forslund",
        "code"      : "dasdlsajdlsajdlijsakda",
        "userId"    : 324723874923748,
        "url.verify": "https://membaza.com/register/${userId}/verify",
        "url.cancel": "https://membaza.com/register/${userId}/cancel"
    }
}
```

## License
Copyright 2017 Emil Forslund

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.