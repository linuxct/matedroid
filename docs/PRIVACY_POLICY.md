# Privacy Policy for MateDroid

**Last updated:** January 16, 2025

MateDroid ("the App") is a free, open-source Android application that displays Tesla vehicle data from your self-hosted Teslamate instance. This privacy policy explains how the App handles your data.

## Summary

- MateDroid does **not** collect, track, or share your personal data with the developer
- All vehicle data comes from **your own** self-hosted Teslamate server
- Data is stored **locally on your device** only
- No analytics, advertising, or telemetry is included
- Two third-party services receive limited location data for geocoding and weather features

## Data Sources

### Your Teslamate Server

MateDroid connects exclusively to a Teslamate API server that **you** configure and control. The App retrieves:

- Vehicle information (model, name, color, VIN)
- Real-time status (battery level, charging state, climate, location)
- Drive history (routes, distances, efficiency, timestamps)
- Charge history (energy added, costs, locations, timestamps)
- Battery health statistics
- Software update history

**Important:** Your Teslamate server credentials (URL and API token) are stored securely on your device using Android's encrypted storage backed by the device Keystore. These credentials are never transmitted anywhere except to your own configured server.

## Data Stored on Your Device

The App stores the following data locally:

- **Settings:** Server URL, API token (encrypted), display preferences
- **Cached statistics:** Aggregated drive and charge summaries for offline viewing and quick loading

This data never leaves your device except when communicating with your Teslamate server or the third-party services described below.

## Third-Party Services

MateDroid uses two external services to enhance functionality:

### 1. OpenStreetMap Nominatim (Geocoding)

- **Purpose:** Convert GPS coordinates to human-readable addresses
- **Data sent:** Latitude and longitude coordinates (rounded to ~11m precision)
- **Provider:** OpenStreetMap Foundation
- **Privacy policy:** https://osmfoundation.org/wiki/Privacy_Policy

### 2. Open-Meteo (Weather Data)

- **Purpose:** Display historical weather conditions along drive routes
- **Data sent:** Latitude, longitude, and date range
- **Provider:** Open-Meteo (open-source weather API)
- **Privacy policy:** https://open-meteo.com/en/terms

Both services receive only location coordinates and timestamps, with no identifying information about you or your vehicle.

## What We Do NOT Collect

MateDroid does **not**:

- Include any analytics or tracking SDKs
- Collect crash reports or usage statistics
- Display advertisements
- Require account registration
- Share any data with the developer or third parties (beyond the geocoding/weather services above)
- Access contacts, camera, microphone, or other sensitive device features

## Permissions

The App requests only the following Android permissions:

| Permission | Purpose |
|------------|---------|
| Internet | Connect to your Teslamate server and third-party APIs |
| Foreground Service | Sync data in the background |
| Notifications | Show sync progress |

No location, camera, contacts, or other sensitive permissions are requested.

## Data Security

- API credentials are stored using Android's EncryptedSharedPreferences with hardware-backed Keystore
- All network communication uses HTTPS
- Optional support for self-signed certificates (user must explicitly enable)

## Data Retention and Deletion

All data is stored locally on your device. You can delete all App data at any time by:

1. Using the "Clear Data" option in Settings within the App, or
2. Clearing App data through Android Settings, or
3. Uninstalling the App

## Children's Privacy

MateDroid is not directed at children under 13 and does not knowingly collect data from children.

## Changes to This Policy

Updates to this privacy policy will be posted in the App repository and reflected in new releases. The "Last updated" date at the top indicates the most recent revision.

## Open Source

MateDroid is open-source software licensed under GPL-3.0. You can review the complete source code at:

https://github.com/vide/matedroid

## Contact

For questions about this privacy policy or the App, please open an issue on GitHub:

https://github.com/vide/matedroid/issues
