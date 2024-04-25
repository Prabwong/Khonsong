#include <Adafruit_Fingerprint.h>
#include <LiquidCrystal_I2C.h>
#include <SoftwareSerial.h>
#include <Wire.h>

///// Comunicate /////
// SoftwareSerial mySerial(1, 3); // TX, RX for recieve data from car

///// FINGERPRINT and tx rx /////
// yellow line TX --> 13 (D7)
// white line RX --> 15 (D8)
SoftwareSerial fingerSerial(13, 15); // fingerprint sensor
Adafruit_Fingerprint finger = Adafruit_Fingerprint(&fingerSerial);

int count = 0;
int state = 1;

///// LCD /////
// brown line TX --> (D1)
// white line RX --> (D2)
LiquidCrystal_I2C lcd(0x27,  16, 2);

///// Camera /////
bool sendToCamera = false; // state to send digital signal
int cameraPin = 14; // (D5)

bool test = true;

void setup() {

  Serial.begin(115200);
  pinMode(cameraPin, OUTPUT);
  // mySerial.begin(9600);

  // setup fingerprint
  finger.begin(57600);

  // set up LCD
  lcd.init();
  lcd.backlight();
  lcd.setCursor(0,0);
  lcd.print("AWAIT");
}

void loop() {
  if (Serial.available() > 0){
    int mystr = Serial.read();
    // Serial.println(mystr);
    if (mystr == 9) {
      state = 1;
      while (state == 1){
        Serial.println("Put you finger");
        fingerScan();

      }
    }
  }
}

int getFingerprintIDez() {
  uint8_t p = finger.getImage();
  if (p != FINGERPRINT_OK)  return -1;

  p = finger.image2Tz();
  if (p != FINGERPRINT_OK)  return -1;

  p = finger.fingerFastSearch();
  if (p != FINGERPRINT_OK)  return 0;

  // found a match!
  Serial.print("Found ID #"); Serial.print(finger.fingerID);
  Serial.print(" with confidence of "); Serial.println(finger.confidence);
  return finger.fingerID;
}

void fingerScan() {

  lcd.setCursor(0,0);
  lcd.print("PLEASE SCAN");
  lcd.setCursor(0,1);
  lcd.print("YOUR FINGER");

  int data = getFingerprintIDez();
  if (data == 0) {
    count += 1;
    Serial.println("Try Again!");
    if (count == 3) {
      Serial.println("No matching");
      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print("NO MATCHING");
      lcd.setCursor(0,1);
      lcd.print("FINGERPRINT");
      count = 0;
    }
  } 

  else if (data != -1) {
    Serial.println(data);
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("USER ID");
    lcd.setCursor(0,1);
    lcd.print(data);

    count = 0;
    state = 0;

    Serial.write(int(200));
    lcd.setCursor(0,0);
    lcd.print("ACCESS");
    lcd.setCursor(0,1);
    lcd.print("GRANTED!");
    delay(500);
    lcd.clear();

    return;
  }
  delay(1000);

}
