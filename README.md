# <img src="docs/logo_color.png" width="72"> pCloud Java SDK

The official pCloud SDK for Java & Android for integrating with [ pCloud's API][docs].

## Requirements

- Java 7.0+
- Android 2.3+ (API9+)

## Documentation

- The documentation for the SDK can be found [here](https://pcloud.github.io/pcloud-sdk-java/).
- The pCloud API can be found [here][docs].

## Getting started
### Register your application

In order to use this SDK, you have to register your application in the [pCloud App Console](https://PUT/OUT/APP/CONSOLE/URL). Take note of the app key in the main page of your application once you create it.

### Set up your application

The SDK uses an OAuth 2.0 access token to authorize requests to the pCloud API. You can obtain a token using the SDK's authorization flow.
To allow the SDK to do that, find the 'Redirect URIs' section in your application configuration page and add a URI that will be redirected on a authorization grant.

### Install the SDK

#### Java

Download [the latest JAR][jar-java] or grab via Maven:

```xml
<dependency>
  <groupId>com.pcloud.sdk</groupId>
  <artifactId>java-core</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```
or Gradle:
```groovy
compile 'com.pcloud.sdk:java-core:1.0.1'
```

#### Android

Download [the latest JAR][jar-android] or grab via Maven:

```xml
<dependency>
  <groupId>com.pcloud.sdk</groupId>
  <artifactId>android</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```
or Gradle:
```groovy
compile 'com.pcloud.sdk:android:1.0.1'
```

### Samples

#### Java

See the code in the `sample` module or the sample application [here.](https://github.com/pCloud/pcloud-sdk-java/blob/master/sample/src/main/java/Main.java)

#### Android

See the code in the `sample-android` module or the sample application [here.](https://github.com/pCloud/pcloud-sdk-java/blob/master/sample-android/src/main/java/com/pcloud/sdk/sample/MainActivity.java)


#License
	Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



[site]: https://www.pcloud.com/
[docs]: https://docs.pcloud.com/
[bintray]: https://bintray.com/pcloud/pcloud-sdk/
[jar-android]: https://bintray.com/pcloud/pcloud-sdk/download_file?file_path=com%2Fpcloud%2Fsdk%2Fandroid%2F1.0.0%2Fandroid-1.0.0-javadoc.jar
[jar-java]: https://bintray.com/pcloud/pcloud-sdk/download_file?file_path=com%2Fpcloud%2Fsdk%2Fandroid%2F1.0.0%2Fandroid-1.0.0-javadoc.jar

