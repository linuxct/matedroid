# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.0] - 2025-12-21

### Added
- **Dashboard**: Real-time vehicle status with dynamic Tesla 3D car images
  - Battery level, range, and charging status
  - Climate information (inside/outside temperature)
  - Vehicle state (locked, location, software version)
  - Car images adapt to your vehicle's color, model, and wheels
- **Charges Screen**: Complete charging history
  - Summary statistics (sessions, energy, cost)
  - Date filtering (7/30/90 days, year, all time)
  - Charge details with power/voltage/current profiles
  - Interactive line graphs with Y-axis labels
  - Map showing charge location
- **Drives Screen**: Drive history with efficiency metrics
  - Summary statistics (distance, energy, efficiency)
  - Drive details with speed/power/battery/elevation graphs
  - Route map visualization
  - Start and end times with duration
- **Mileage Screen**: Yearly/monthly/daily mileage breakdown
  - Interactive drill-down (year → month → day)
  - Bar charts with car accent colors
  - Statistics per period
- **Software Versions Screen**: Update history tracking
  - Overview with total updates and average interval
  - Monthly updates bar chart
  - Duration and days installed per version
  - Trophy badge for longest-installed version
- **Battery Health Screen**: Battery degradation tracking
- **Car Color Palettes**: Dynamic UI theming based on car exterior color
  - Supports all Tesla colors including Highland/Juniper models
  - Accent colors for UI elements and graphs
- **Settings**: TeslamateApi server configuration
  - URL and API token configuration
  - Accept invalid certificates option for self-hosted
  - Currency selection for cost display

### Technical
- Jetpack Compose with Material Design 3
- Dark/light mode with dynamic colors (Android 12+)
- Pull-to-refresh on all data screens
- Hilt dependency injection
- Retrofit + Moshi for API communication
- DataStore for settings persistence
- OSMDroid for map visualization

## [0.2.0] - 2025-12-20

### Added
- Drives screen with drive history
- Charge detail screen with graphs
- Drive detail screen with route map
- Mileage tracking screen
- Software versions screen
- Battery health screen

## [0.1.0] - 2025-12-19

### Added
- Initial project setup
- Settings screen for server configuration
- Dashboard with basic vehicle status
- Charges screen with history list

[Unreleased]: https://github.com/yourusername/matedroid/compare/v0.3.0...HEAD
[0.3.0]: https://github.com/yourusername/matedroid/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/yourusername/matedroid/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/yourusername/matedroid/releases/tag/v0.1.0
