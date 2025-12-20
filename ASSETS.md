# Tesla Car Image Assets

This document describes how Tesla car images are fetched and mapped for MateDroid.

## Overview

Tesla provides a compositor service that generates car renders based on configuration options. The images are downloaded at build time using `util/fetch_tesla_assets.py` and bundled into the APK.

## Compositor Endpoints

Tesla has **two different compositor APIs**:

### Old Compositor (Legacy Models)
- **URL**: `https://static-assets.tesla.com/v1/compositor/`
- **For**: Pre-2024 Model 3, Pre-2025 Model Y
- **Output**: PNG with transparency
- **File size**: ~100-140 KB per image

**Example URL**:
```
https://static-assets.tesla.com/v1/compositor/?model=m3&view=STUD_3QTR&size=800&options=PMNG,W38B&bkba_opt=1
```

**Parameters**:
| Parameter | Description | Example |
|-----------|-------------|---------|
| `model` | Model code | `m3`, `my` |
| `view` | Camera angle | `STUD_3QTR` |
| `size` | Image width | `800` |
| `options` | Comma-separated codes | `PMNG,W38B` |
| `bkba_opt` | Background (1=transparent) | `1` |

### New Compositor (Highland/Juniper)
- **URL**: `https://static-assets.tesla.com/configurator/compositor`
- **For**: Model 3 Highland (2024+), Model Y Juniper (2025+)
- **Output**: JPEG (no transparency)
- **File size**: ~8-12 KB per image

**Example URL**:
```
https://static-assets.tesla.com/configurator/compositor?context=design_studio_2&options=$MT370,$PPSW,$W38A,$IPB3&view=STUD_FRONT34&model=m3&size=800&bkba_opt=2
```

**Parameters**:
| Parameter | Description | Example |
|-----------|-------------|---------|
| `context` | API context | `design_studio_2` |
| `model` | Model code | `m3`, `my` |
| `view` | Camera angle | `STUD_FRONT34` |
| `size` | Image width | `800` |
| `options` | `$`-prefixed codes | `$MT370,$PPSW,$W38A,$IPB3` |
| `bkba_opt` | Background option | `2` |

**Important**: New compositor uses `$` prefix for all option codes!

---

## Model Mappings

### TeslamateAPI Model → Compositor Model Code

| TeslamateAPI `car_details.model` | Compositor Code | Notes |
|----------------------------------|-----------------|-------|
| `3` | `m3` | Model 3 |
| `Y` | `my` | Model Y |
| `S` | `ms` | Model S (not downloaded) |
| `X` | `mx` | Model X (not downloaded) |

### Internal Model Variants (for asset naming)

| Variant | Description | Compositor | File Prefix |
|---------|-------------|------------|-------------|
| Legacy Model 3 | Pre-2024 | Old | `m3` |
| Highland Model 3 | 2024+ | New | `m3h` |
| Highland Model 3 Performance | 2024+ Perf | New | `m3hp` |
| Legacy Model Y | Pre-2025 | Old | `my` |
| Juniper Model Y | 2025+ | New | `myj` |

### Highland/Juniper Trim Codes

| Trim Code | Model | Variant | Interior Code |
|-----------|-------|---------|---------------|
| `MT369` | Model 3 Highland | Standard | `IPB2` |
| `MT370` | Model 3 Highland | Premium | `IPB3` |
| `MT371` | Model 3 Highland | Performance | `IPB4` |
| `MTY68` | Model Y Juniper | Standard | `IBB3` |

---

## Color Mappings

### TeslamateAPI Color → Compositor Code

| TeslamateAPI `car_exterior.exterior_color` | Compositor Code | Color Name | Available On |
|--------------------------------------------|-----------------|------------|--------------|
| `Black`, `SolidBlack` | `PBSB` | Solid Black | All |
| `ObsidianBlack` | `PMBL` | Obsidian Black Metallic | Legacy M3 |
| `MidnightSilver`, `MidnightSilverMetallic` | `PMNG` | Midnight Silver Metallic | Legacy |
| `Silver`, `SilverMetallic` | `PMSS` | Silver Metallic | Legacy M3 |
| `White`, `PearlWhite`, `PearlWhiteMultiCoat` | `PPSW` | Pearl White Multi-Coat | All |
| `DeepBlue`, `DeepBlueMetallic`, `Blue` | `PPSB` | Deep Blue Metallic | All except Juniper |
| `Red`, `RedMultiCoat` | `PPMR` | Red Multi-Coat | Legacy |
| `Quicksilver` | `PN00` | Quicksilver | Highland M3 |
| `StealthGrey`, `StealthGray` | `PN01` | Stealth Grey | Highland, Juniper |
| `MidnightCherryRed` | `PR00` | Midnight Cherry Red | None (discontinued) |
| `UltraRed` | `PR01` | Ultra Red | Highland M3 |
| `BlackDiamond` | `PX02` | Black Diamond | Highland, Juniper |

### Color Availability by Model Variant

| Color Code | Legacy M3 | Highland M3 | Legacy MY | Juniper MY |
|------------|-----------|-------------|-----------|------------|
| `PBSB` | ✅ | ✅ | ✅ | ❌ |
| `PMBL` | ✅ | ❌ | ❌ | ❌ |
| `PMNG` | ✅ | ❌ | ✅ | ❌ |
| `PMSS` | ✅ | ❌ | ❌ | ❌ |
| `PPSW` | ✅ | ✅ | ✅ | ✅ |
| `PPSB` | ✅ | ✅ | ✅ | ❌ |
| `PPMR` | ✅ | ❌ | ✅ | ❌ |
| `PN00` | ❌ | ✅ | ❌ | ❌ |
| `PN01` | ❌ | ✅ | ❌ | ✅ |
| `PR01` | ❌ | ✅ | ❌ | ❌ |
| `PX02` | ❌ | ✅ | ❌ | ✅ |

---

## Wheel Mappings

### TeslamateAPI Wheel → Compositor Code

| TeslamateAPI `car_exterior.wheel_type` Pattern | M3 Code | MY Code | Description |
|------------------------------------------------|---------|---------|-------------|
| `Pinwheel18*`, `Aero18*` | `W38B` | `WY18B` | 18" Aero Wheels |
| `AeroTurbine19*`, `Stiletto19*`, `Sport19*` | `W39B` | `WY19B` | 19" Sport Wheels |
| `Gemini19*` | - | `WY19B` | 19" Gemini (MY) |
| `Apollo19*` | - | `WY9S` | 19" Apollo (MY) |
| `Induction20*` | - | `WY0S` | 20" Induction (MY) |
| `Performance20*` | `W32P` | `WY20P` | 20" Performance |
| `Uberturbine21*` | - | `WY1S` | 21" Uberturbine (MY) |
| `Photon18*` | `W38A` | `WY18P` | 18" Photon (Highland/Juniper) |

**Note**: TeslamateAPI may append suffixes like `CapKit`, `Cover`, etc. The mapping strips these.

### Wheel Availability by Model Variant

| Wheel Code | Legacy M3 | Highland M3 | Highland M3P | Legacy MY | Juniper MY |
|------------|-----------|-------------|--------------|-----------|------------|
| `W38B` | ✅ | ❌ | ❌ | - | - |
| `W39B` | ✅ | ❌ | ❌ | - | - |
| `W32P` | ✅ | ❌ | ❌ | - | - |
| `W38A` | ❌ | ✅ | ❌ | - | - |
| `W30P` | ❌ | ❌ | ✅ | - | - |
| `WY18B` | - | - | - | ✅ | ❌ |
| `WY19B` | - | - | - | ✅ | ❌ |
| `WY20P` | - | - | - | ✅ | ❌ |
| `WY0S` | - | - | - | ✅ | ❌ |
| `WY1S` | - | - | - | ✅ | ❌ |
| `WY18P` | - | - | - | ❌ | ✅ |

---

## Gotchas and Known Issues

### 1. Placeholder Images from Old Compositor
The old compositor returns **grayscale shadow placeholders** (~26KB) for color/wheel combinations that don't exist. These look like car silhouettes but are not usable images.

**Detection**: Check file size. Valid images are 100KB+, placeholders are ~26KB.

### 2. New Colors Don't Work on Old Compositor
Colors introduced with Highland/Juniper (`PN00`, `PN01`, `PR01`, `PX02`) return placeholders on the old compositor. Must use the new compositor for these colors.

### 3. Highland/Juniper Have Limited Wheel Options
- Highland Model 3: Only `W38A` (standard) or `W30P` (Performance)
- Juniper Model Y: Only `WY18P`

Each trim level has exactly ONE wheel option available.

### 4. No Reliable Highland/Juniper Detection from TeslamateAPI
TeslamateAPI doesn't clearly indicate if a car is Highland/Juniper. Current detection heuristic:
- If exterior color is `PN00`, `PN01`, `PR01`, or `PX02` → Highland/Juniper
- Otherwise → Legacy

This may miss Highland/Juniper cars with common colors like Pearl White (`PPSW`).

### 5. Trim Badging for Performance Detection
- Legacy: `P` prefix (e.g., `P74D`) indicates Performance
- Highland: Uses `MT371` trim code

### 6. Model S/X Support
The old compositor supports Model S (`ms`) and Model X (`mx`) with legacy colors, but we don't download these to save space. Can be added if needed.

### 7. Discontinued Colors
- `PR00` (Midnight Cherry Red): Was briefly available, now discontinued. Compositor may still work but we don't download.

### 8. New Compositor Returns JPEG
The new compositor returns JPEG format, while the old returns PNG. Both are handled in the app.

---

## Asset File Naming Convention

Format: `{model_variant}_{color_code}_{wheel_code}.{ext}`

| Model Variant | Example Filename |
|---------------|------------------|
| Legacy Model 3 | `m3_PMNG_W38B.png` |
| Highland Model 3 | `m3h_PN01_W38A.jpg` |
| Highland M3 Performance | `m3hp_PR01_W30P.jpg` |
| Legacy Model Y | `my_PPSW_WY19B.png` |
| Juniper Model Y | `myj_PX02_WY18P.jpg` |

---

## Current Asset Inventory

| Model Variant | Colors | Wheels | Total Images | Format |
|---------------|--------|--------|--------------|--------|
| Legacy Model 3 | 7 | 3 | 21 | PNG |
| Highland Model 3 | 7 | 1 | 7 | JPEG |
| Highland M3 Performance | 7 | 1 | 7 | JPEG |
| Legacy Model Y | 5 | 5 | 25 | PNG |
| Juniper Model Y | 3 | 1 | 3 | JPEG |
| **Total** | | | **63** | **~6.1 MB** |

---

## Updating Assets

To re-download all assets:

```bash
# Preview what would be downloaded
./util/fetch_tesla_assets.py --dry-run

# Download all assets
./util/fetch_tesla_assets.py

# Download to custom directory
./util/fetch_tesla_assets.py --output-dir /path/to/assets
```

The script will:
1. Skip invalid combinations (checks response size/format)
2. Report warnings for failed downloads
3. Download in parallel batches

---

## Future Considerations

1. **New Model Variants**: When Tesla releases new refreshes (e.g., Model S/X Plaid+), new compositor codes may be needed.

2. **New Colors**: Tesla periodically introduces new colors. Check Tesla's configurator for current options.

3. **API Changes**: Tesla may deprecate or change compositor endpoints without notice.

4. **Better Highland/Juniper Detection**: Could potentially use VIN decoding or manufacture date if available from TeslamateAPI.
