// Use NodeMCU 0.9 (ESP-12 Module) as a Board

#include <ESP8266WiFi.h>
#include <ESP8266httpClient.h>
#include <WiFiClientSecure.h>
#include <ArduinoJson.h>
#include <time.h>

const char WIFI_SSID[] = "X21-2.4G-153B0C"; 
const char WIFI_PASSWORD[] = "764056B5"; 

// const char WIFI_SSID[] = "NongFoam2"; 
// const char WIFI_PASSWORD[] = "PeopleDonT"; 
 
float h;
float t;
unsigned long lastMillis = 0;
unsigned long previousMillis = 0;
const long interval = 5000;
 
// ================= INFO ====================
// checkpoints array
const int MAX_CHECKPOINTS = 3;
char checkpoints[MAX_CHECKPOINTS] = {'\0', '\0', '\0'};
// retries for publish message
const int MAX_RETRIES = 3;
 // current checkpoint (test assum as A for now, please change)
//char checkpoint[20] = "";
// restart status
bool restart = false;
// current routeID
int curr_routeID;
// status of car
String process_status;
String pos;
String staffName;

// ================= For movement ====================
#define left_sensor 5
#define right_sensor 4

const int pwm1 = 0 ;	// ENA pin
const int pwm2 = 2 ; // ENB pin
const int in_1 = 14 ; // bacl left
const int in_2 = 12 ; // left
const int in_3 = 13 ; // back right
const int in_4 = 15 ; // right

const int startpoint = 0; // declare the starting point
int currentCheckpoint = 0; // current point of car
int destinationCheckpoint = 0; // The destination point
int checkpointCounter = 0;

bool isSender = true; // First the car is be the sender

// ============ HTTP ==============
WiFiClient client;
HTTPClient http;
//String baseURL = "http://54.82.55.108:8080";
String baseURL = "http://ec2-54-82-55-108.compute-1.amazonaws.com:8080";
String getEndpoint = "/currentStatus";

void getInput() {
  // get request every 1 second
  if (!http.begin(client, baseURL + "/arduino/getInput")) {
    Serial.println("Connection failed");
  }
  int httpCode = http.GET();
  if (httpCode > 0) {
    String payload = http.getString();
    Serial.println(payload);
    if (!payload.equals("no")) {
    char *splitData = strtok(const_cast<char*>(payload.c_str()), ",");
    while (splitData != NULL) {
    // Convert the C-style string to Arduino String
    String token = String(splitData);
    
      if (token == "start") {
        process_status = token;
      } else {
        // covert checkpoint
        if (token.length() <= MAX_CHECKPOINTS) {
        for (int i = 0; i < token.length() && i < MAX_CHECKPOINTS; ++i) {
            checkpoints[i] = token[i];
    }}}
    // Get the next token
    splitData = strtok(NULL, ",");
    }
  }
  } else {
    Serial.println("Error on HTTP request");
  }

  http.end();
  delay(500);
}

// resend ver
void postRestart() {
  bool success = false;
  do {
    if (!http.begin(client, baseURL + "/arduino/restart")) {
      Serial.println("Connection failed");
      break;
    }
    
    //Create a JSON document
    StaticJsonDocument<512> doc;
    doc["restart"] = true;
    char jsonBuffer[512];
    serializeJson(doc, jsonBuffer);
    
    // Set Content-Type header to application/json
    http.addHeader("Content-Type",  "application/json");
    int httpCode = http.POST(jsonBuffer);
    
    if (httpCode > 0) {
      Serial.printf("[HTTP] POST... code: %d\n", httpCode);
      if (httpCode == HTTP_CODE_OK) {
        String payload = http.getString();
        Serial.println(payload);
        if (!payload.equals("no")) {
          // Convert the C-style string to Arduino String
          process_status = "";
          success = true;
        } else {
          success = false;
        }
        
      }
    } else {
      Serial.println("Error on HTTP request");
    }
    
    http.end();
    
    if (!success) {
      delay(500); // Delay before retrying
    }
  } while (!success);
  
  infoSetup();
}

void postReceived() {
  bool success = false;
  
  do {
    if (!http.begin(client, baseURL + "/route/update")) {
      Serial.println("Connection failed");
      break;
    }
    
    // Create a JSON document
    StaticJsonDocument<512> doc;
    //doc["staffName"] = "Karnpitcha Kasemsirinavin"; // please change according to fingerprint
    doc["staffName"] = staffName;
    doc["routeID"] = String(curr_routeID);

    char jsonBuffer[512];
    serializeJson(doc, jsonBuffer);

    // Set Content-Type header to application/json
    http.addHeader("Content-Type",  "application/json");
    int httpCode = http.POST(jsonBuffer);

    if (httpCode > 0) {
      Serial.printf("[HTTP] POST... code: %d\n", httpCode);
      if (httpCode == HTTP_CODE_OK) {
        String payload = http.getString();
        Serial.println(payload);
        if (!payload.equals("no")) {
          // Convert the C-style string to Arduino String
          Serial.println("printting from post received");
          Serial.println(process_status);
          process_status = payload;
        }
        success = true; // Indicate success
      }
    } else {
      Serial.println("Error on HTTP request");
    }

    http.end();
    delay(500);
  } while (!success); // Repeat until success
}

void postArrived() {
  bool success = false;

  do {
    if (!http.begin(client, baseURL + "/route/create")) {
      Serial.println("Connection failed");
      break;
    }
    
    // Create a JSON document
    StaticJsonDocument<512> doc;
    doc["checkpoint"] = pos;
    doc["numberCheckpoint"] = currentCheckpoint;

    char jsonBuffer[512];
    serializeJson(doc, jsonBuffer);

    // Set Content-Type header to application/json
    http.addHeader("Content-Type",  "application/json");
    int httpCode = http.POST(jsonBuffer);

    if (httpCode > 0) {
      Serial.printf("[HTTP] POST... code: %d\n", httpCode);
      if (httpCode == HTTP_CODE_OK) {
        String payload = http.getString();
        Serial.println("payload from arrived: ");
        Serial.println(payload);
        if (!payload.equals("no")) {
          // Convert the C-style string to Arduino String
          curr_routeID = payload.toInt();
        }
        success = true; // Indicate success
      }
    } else {
      Serial.println("Error on HTTP request");
      Serial.println(httpCode);
    }

    http.end();
    delay(500);
  } while (!success); // Repeat until success
}



// ++++++++++++++++++++++++++++++ car functions ++++++++++++++++++++++++++++++

void carSetup() {
  pinMode(pwm1,OUTPUT) ;  	//we have to set PWM pin as output
  pinMode(pwm2,OUTPUT) ;
  pinMode(in_1,OUTPUT) ; 	//Logic pins are also set as output
  pinMode(in_2,OUTPUT) ; 	//Logic pins are also set as output
  pinMode(in_3,OUTPUT) ;
  pinMode(in_4,OUTPUT) ;
}

void passCheckpoint(int motor1, int motor2) {
  Serial.println("Detect checkpoint");

  bool passCheckpoint = 0;
  while (passCheckpoint == 0) {
    bool right = digitalRead(right_sensor);
    bool left = digitalRead(left_sensor);

    //Serial.println(passCheckpoint);
    
    if (left == 1 && right == 1 ) {

      analogWrite(pwm1, 100); //ENA   pin
      analogWrite(pwm2, 100);
      digitalWrite(motor1,HIGH) ;
      digitalWrite(motor2,HIGH) ;
    }
    // digitalWrite(in_2,HIGH) ;
    // digitalWrite(in_4,HIGH) ;
    
    else {
      passCheckpoint = 1 ;
      Serial.println("Pass Checkpoint!");
      if (process_status == "finish") {
        currentCheckpoint --;
      }
    }

    delay(100);
  }

  passCheckpoint = 0;
  return;
}

void backToStartpoint() {
  while (digitalRead(right_sensor) == 1 && digitalRead(left_sensor == 1)) {
    analogWrite(pwm1, 100); //ENA   pin
    analogWrite(pwm2, 100);
    digitalWrite(in_1,HIGH) ;
    digitalWrite(in_3,HIGH) ;
  }

  delay(100);

  Serial.println("Starting Back to Startpoint");

  bool reachedStartpoint = false;
  while (!reachedStartpoint) {

    bool right = digitalRead(right_sensor);
    bool left = digitalRead(left_sensor);
    analogWrite(pwm1, 100); //ENA   pin
    analogWrite(pwm2, 100);

    // Backward
    if(left==0 && right==0){
      //Forward
      digitalWrite(in_1,HIGH) ;
      digitalWrite(in_3,HIGH) ;
    }

    // Turnback Right
    else if(left==1 && right==0){
      //F
      digitalWrite(in_1, LOW) ;
      digitalWrite(in_3, HIGH) ;
    }

    // Turnback Left
    else if(left==0 && right==1){
      digitalWrite(in_1, HIGH) ;
      digitalWrite(in_3, LOW) ;
    }

    // Reach Start
    else if(left==1 && right==1){
      digitalWrite(in_1, LOW) ;
      digitalWrite(in_3, LOW) ;

      Serial.print("Current Cheackpoint: ");
      Serial.println(currentCheckpoint);

      if (currentCheckpoint == startpoint){
        postRestart();
        Serial.println("Reach Startpoint");
        digitalWrite(in_1,LOW) ;
        digitalWrite(in_3,LOW) ;
      // reachedCheckpoint = true;
      return;
      }
      delay(100);
      passCheckpoint(in_1, in_3);

    }

    delay(100);
  }

}

void moveToCheckpoint() {
  Serial.println("pass move to checkpoint");
  bool stratMoving = 0;
  while (stratMoving == 0) {

    bool right = digitalRead(right_sensor);
    bool left = digitalRead(left_sensor);

    analogWrite(pwm1, 100); //ENA   pin
    analogWrite(pwm2, 100); //ENB   pin
    digitalWrite(in_2,HIGH) ;
    digitalWrite(in_4,HIGH) ;

    if (right != 1 || left != 1) {
      delay(100);
      stratMoving = 1;
    }

    delay(100);

  }

  stratMoving = 0;

  Serial.println("Starting Go to Destination Point");

  bool reachedCheckpoint = false;
  while (!reachedCheckpoint) {

    bool right = digitalRead(right_sensor);
    bool left = digitalRead(left_sensor);
    analogWrite(pwm1, 100); //ENA   pin
    analogWrite(pwm2, 100);

    // Forward
    if(left==0 && right==0){
      digitalWrite(in_2,HIGH) ;
      digitalWrite(in_4,HIGH) ;
    }

    // Turn right
    else if(left==1 && right==0){
      digitalWrite(in_2,LOW) ;
      digitalWrite(in_4,HIGH) ;
    }

    // Turn left
    else if(left==0 && right==1){
      digitalWrite(in_2,HIGH) ;
      digitalWrite(in_4,LOW) ;
    }

    // Detect checkpoint
    else if(left==1 && right==1){
      digitalWrite(in_2,LOW) ;
      digitalWrite(in_4,LOW) ;
      currentCheckpoint ++;
      if  (process_status == "start") {
        if (currentCheckpoint == 1) {
          pos = "A";
        } else if (currentCheckpoint == 2) {
          pos = "B";
        } else if (currentCheckpoint == 3) {
          pos = "C";
        }
        postArrived();
      }
      // currentCheckpoint ++;
      // if (currentCheckpoint == 1) {
      //   pos = "A";
      // } else if (currentCheckpoint == 2) {
      //   pos = "B";
      // } else if (currentCheckpoint == 3) {
      //   pos = "C";
      // }
      // postArrived();

      Serial.print("Current Cheackpoint: ");
      Serial.println(currentCheckpoint);

      // Rech Destination Point
      if (currentCheckpoint == destinationCheckpoint){
        // reachedCheckpoint = true;
        // send arrived
        //postArrived();
        checkpointCounter++;
        process_status = "wait";
        Serial.println("Reach Destination Cheackpoint");
        Serial.write(int(9));
        return;
      }
      delay(100);
      passCheckpoint(in_2, in_4);


    }

    delay(100);
  }
}

void moveCar(char incomingByte) {
    Serial.println("command move");

// Check if checkpointCounter is within bounds
if (checkpointCounter < MAX_CHECKPOINTS) {
    Serial.println("incoming");
    Serial.println(incomingByte); // Print the incoming character
    // Set the destination checkpoint based on user input
    // Compare incomingByte with characters 'A', 'B', and 'C'
    if (incomingByte == 'A') {
        destinationCheckpoint = 1;
        //pos = "A";
    } else if (incomingByte == 'B') {
        destinationCheckpoint = 2;
        //pos = "B";
    } else if (incomingByte == 'C') {
        destinationCheckpoint = 3;
        //pos = "C";
    } else {
        Serial.println("Invalid destination checkpoint");
    }
    // Print destination and current checkpoints
    // Serial.println("dest");
    // Serial.println(destinationCheckpoint);
    // Serial.println("curr");
    // Serial.println(currentCheckpoint);
    // Move to the selected checkpoint if it's valid and different from the current checkpoint
    if (currentCheckpoint != destinationCheckpoint) {
        Serial.println("start move");
        moveToCheckpoint(); // Move to the selected checkpoint
    }
  }

}

void checkFinger() {

  if (Serial.available() > 0) {
    // Read the incoming data
    String receivedData = Serial.readString();
    // Print the received data
    if (receivedData.indexOf("Found ID") != -1) {
      for (int i = 0; i < receivedData.length(); i++) {
      char number = receivedData.charAt(i); // Get the character at index i
      
      // Check if the character is a digit
      if (isdigit(number)) {
        // Append the digit to the numberString
        Serial.print("detected user: ");
        if (number == '1') {
          staffName = "Karnpitcha Kasemsirinavin";
        } else if (number == '2') {
          staffName = "Pann Mekmok";
        } else if (number == '3') {
          staffName = "Kanyawat Raksawin";
        } else {
          staffName = "Prab Wongsekleo";
        }

        break;
        }
      }
      Serial.println("Finger Scane Done!");
      postReceived();
    }
  }

  delay(100);
}

void infoSetup() {
  int i = 0;
    for (int i = 0; i < MAX_CHECKPOINTS; i++) {
      checkpoints[i] = '\0';
    }
  checkpointCounter = 0;
}


// ++++++++++++++++++++++++++++++ execute code ++++++++++++++++++++++++++++++
void printValue() {
  Serial.print(process_status);
  Serial.print(curr_routeID);
  Serial.print("[");
  for (int i = 0; i < 3; i++) {
    Serial.print(checkpoints[i]);
    if (i < 2) { // Print comma and space for all elements except the last one
      Serial.print(", ");
    }
  }
  Serial.println("]");
  
  delay(500);
}

void reconnectWiFi() {
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
 
  Serial.println(String("Attempting to connect to SSID: ") + String(WIFI_SSID));
 
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(500);
  }
}

void setup()
{
  delay(3000);
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
 
  Serial.println(String("Attempting to connect to SSID: ") + String(WIFI_SSID));
 
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(500);
  }
 
  Serial.begin(115200);
  carSetup();
  infoSetup();
  Serial.println("sucessfully setup");
}

 
 
void loop()
{
  
  // reconnect AWS
  if (WiFi.status() != WL_CONNECTED) {
    reconnectWiFi();
  }

  // get input from user
  if (checkpoints[0] == '\0') {
      getInput();
      //postRestart();
      //Serial.println("check status");
      //Serial.println(process_status);
      //postReceived();
      //Serial.println(curr_routeID);
  }
  //postRequest();
  // move car
  if (process_status == "start") {
    Serial.println("check status in start");
    Serial.println(process_status);
    //checkpoints[checkpointCounter] != '\0'
    moveCar(checkpoints[checkpointCounter]);
  } else if (process_status == "wait") {
    // wait for fingerprint
    checkFinger();
  } else if (process_status == "finish") {
    // go back to start point
    backToStartpoint();
  }
}

