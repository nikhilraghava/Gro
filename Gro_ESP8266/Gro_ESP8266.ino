#define BLYNK_PRINT Serial
#include <ESP8266WiFi.h>
#include <BlynkSimpleEsp8266.h>

char auth[] = "3e95bb6b78eb460e924e937aed3fdd15";

// WiFi credentials.
char ssid[] = "SSID";
char pass[] = "PSWD";

void setup()
{
  // Debug console
  Serial.begin(9600);
  Blynk.begin(auth, ssid, pass);
}

void loop()
{
  Blynk.run();
}

