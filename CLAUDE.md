# RULES TO FOLLOW FOR THIS PROJECT

## Development style

* create a new branch and associated Github pull request for each new feature or set of bugfixes.
* automatically commit to git any meaningful change, with a short but clear commit message.
* follow the "Keep a changelog" and Semantic versioning best practices but DO NOT release major versions by yourself. Ask me if you think they might be released.
* create a new release only when specifically told.
* keep an always up-to-date README.md, but don't change its writing style.
* always update `docs/DEVELOPMENT.md` and `docs/ASSETS.md` if anything related to those parts changes.

## Charts and graphs

### Histograms

* Every histogram bar MUST be tappable, and when tapped it will show a tooltip with its value
* The bars will be in the palette accent color

### Line graphs

* The line graph MUST have 4 labels on the Y axis: 1st quarter, half, 3rd quarter, end.
* It MUST have 5 labels on the X axis (which is usually time): start, 1st quarter, half, 3rd quarter, end.
* Tapping on one point of the graph will show a tooltip with the value of the Y axis.

## F-Droid and Fastlane

* The app is published on F-Droid, which builds from source.
* Fastlane metadata is stored in `fastlane/metadata/android/en-US/`:
  - `title.txt`, `short_description.txt`, `full_description.txt` for app store listing
  - `changelogs/<versionCode>.txt` for each release (e.g., `12.txt` for versionCode 12)
  - `images/icon.png` (512x512) and `images/phoneScreenshots/` for visuals
* When releasing a new version, create a new changelog file matching the versionCode in `app/build.gradle.kts`.
* The F-Droid build recipe is stored in `fdroid/com.matedroid.yml` (reference copy; actual recipe lives in fdroiddata repo).

## Gotchas and notes

* Always check the local instance of Teslamate API and its documentation to know what's the returned JSON format.
* The TeslamateApi's parseDateParam function only accepts:
  - RFC3339 format: 2024-12-07T00:00:00Z
  - DateTime format: 2024-12-07 00:00:00

