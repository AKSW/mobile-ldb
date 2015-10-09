# DBpedia App #
## Build instructions ##

1. Clone project and open in Android-Studio.
2. Before building you need a Google Maps API key (see below).
3. Build & install

### Get a Google Maps API Key ###

1. Follow instructions [here](https://developers.google.com/maps/documentation/android-api/signup) to get a key. (But don't add it to AndroidManifest.xml)
2. Create the files app/src/debug/res/values/google_maps_api.xml and app/src/release/res/values/google_maps_api.xml
3. Paste the following in both of them and replace YOUR_KEY with the key you got in 1

```
#!xml
<resources>
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">
        <!-- Paste your key here -->
        YOUR_KEY
    </string>
</resources>
```
**Note:** The key is linked to the signature of your APK, so you need two different keys: one for debug builds and one for the release builds.
