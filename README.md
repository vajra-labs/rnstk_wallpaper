# @rnstk/wallpaper

A powerful React Native library for setting wallpapers (static and live video wallpapers) on Android using Nitro Modules for maximum performance.

## Features

✨ **Static Wallpapers**

- Set from network URLs (http/https)
- Set from base64 data URIs
- Set from local files (file://)
- Set from content URIs (gallery images)
- Set from ArrayBuffer (Skia/Canvas edited images)

🎬 **Live Wallpapers**

- Set video wallpapers (MP4)
- Looping playback
- Optimized battery usage

📍 **Location Support**

- Home screen only
- Lock screen only
- Both screens

⚡ **Performance**

- Built with Nitro Modules for native performance
- Zero-copy ArrayBuffer handling
- Async operations on background threads

## Installation

```bash
npm install @rnstk/wallpaper
# or
yarn add @rnstk/wallpaper
```

## Platform Support

| Platform | Static Wallpaper | Live Wallpaper | Notes                          |
| -------- | ---------------- | -------------- | ------------------------------ |
| Android  | ✅               | ✅             | Fully supported                |
| iOS      | ❌               | ❌             | Not supported (iOS limitation) |

**Note:** iOS does not provide APIs for third-party apps to set wallpapers. Users must manually set wallpapers through the Photos app.

## Permissions (Android)

The library automatically adds these permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.SET_WALLPAPER" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
```

**Note:** If you encounter permission errors, you may need to manually request runtime permissions in your app.

**Runtime Permissions:** For Android 13+, request storage permissions:

```typescript
import {PermissionsAndroid, Platform} from 'react-native';

if (Platform.OS === 'android') {
  // For images
  await PermissionsAndroid.request(
    PermissionsAndroid.PERMISSIONS.READ_MEDIA_IMAGES,
  );

  // For video wallpapers
  await PermissionsAndroid.request(
    PermissionsAndroid.PERMISSIONS.READ_MEDIA_VIDEO,
  );
}
```

## Usage

### Import

```typescript
import Wallpaper from '@rnstk/wallpaper';
```

### Set Static Wallpaper

#### From Network URL

```typescript
await Wallpaper.setWallpaper('https://example.com/image.jpg', 'HOME');
```

#### From Base64

```typescript
const base64 = 'data:image/png;base64,iVBORw0KGgo...';
await Wallpaper.setWallpaper(base64, 'LOCK');
```

#### From Local File

```typescript
await Wallpaper.setWallpaper(
  'file:///storage/emulated/0/image.jpg',
  'BOTH',
);
```

#### From Gallery (Content URI)

```typescript
import {launchImageLibrary} from 'react-native-image-picker';

const result = await launchImageLibrary({mediaType: 'photo'});
if (result.assets?.[0]?.uri) {
  await Wallpaper.setWallpaper(result.assets[0].uri, 'HOME');
}
```

#### From ArrayBuffer (Skia/Canvas)

```typescript
import {Skia} from '@shopify/react-native-skia';

// Edit image with Skia
const surface = Skia.Surface.Make(width, height);
const canvas = surface.getCanvas();
// ... draw/edit image

// Get as ArrayBuffer
const snapshot = surface.makeImageSnapshot();
const data = snapshot.encodeToBytes();

// Set as wallpaper
await Wallpaper.setByteWallpaper(data, 'HOME');
```

### Set Live Wallpaper (Video)

```typescript
// From local video file
await Wallpaper.setLiveWallpaper('/sdcard/Movies/video.mp4');

// From downloaded video
import RNFS from 'react-native-fs';

const localPath = `${RNFS.DocumentDirectoryPath}/video.mp4`;
await RNFS.downloadFile({
  fromUrl: 'https://example.com/video.mp4',
  toFile: localPath,
}).promise;

await Wallpaper.setLiveWallpaper(localPath);
```

**Note:** Live wallpaper picker will open. User needs to select your app's wallpaper service.

### Clear Wallpaper

```typescript
// Reset to default wallpaper
await Wallpaper.clearWallpaper('HOME');
await Wallpaper.clearWallpaper('LOCK');
await Wallpaper.clearWallpaper('BOTH');
```

### Check Permissions

```typescript
const isSupported = Wallpaper.isSupported();
const isAllowed = Wallpaper.isSetAllowed();

if (!isAllowed) {
  console.log('Wallpaper setting is not allowed');
}
```

## API Reference

### Methods

#### `setWallpaper(url: string, location: Location): Promise<void>`

Set a static wallpaper from URL, base64, file path, or content URI.

**Parameters:**

- `url`: Image source (network URL, base64, file://, content://, or absolute path)
- `location`: `'HOME'` | `'LOCK'` | `'BOTH'`

**Supported formats:**

- Network: `https://example.com/image.jpg`
- Base64: `data:image/png;base64,...`
- File: `file:///storage/emulated/0/image.jpg`
- Content: `content://media/external/images/media/123`
- Absolute: `/sdcard/Pictures/image.jpg`

#### `setByteWallpaper(buffer: ArrayBuffer, location: Location): Promise<void>`

Set wallpaper from ArrayBuffer (useful for Skia/Canvas edited images).

**Parameters:**

- `buffer`: ArrayBuffer containing image data
- `location`: `'HOME'` | `'LOCK'` | `'BOTH'`

#### `setLiveWallpaper(videoPath: string): Promise<void>`

Set a video as live wallpaper.

**Parameters:**

- `videoPath`: Local file path to MP4 video

**Note:** Opens system wallpaper picker. User must select your app's wallpaper service.

#### `clearWallpaper(location: Location): Promise<void>`

Reset wallpaper to system default.

**Parameters:**

- `location`: `'HOME'` | `'LOCK'` | `'BOTH'`

#### `isSupported(): boolean`

Check if wallpaper setting is supported on the device.

#### `isSetAllowed(): boolean`

Check if the app has permission to set wallpapers.

### Types

```typescript
type Location = 'HOME' | 'LOCK' | 'BOTH';
```

## Example

Check the [example](./example) folder for a complete working example.

```bash
cd example
npm install
npm run android
```

## Supported Formats

### Static Wallpapers

- **Images:** JPG, PNG, WebP, GIF
- **Sources:** Network URLs, Base64, Local files, Content URIs, ArrayBuffer

### Live Wallpapers

- **Video:** MP4 (H.264/H.265)
- **Sources:** Local files only

## Limitations

### Android

- Live wallpapers don't support "Lock screen only" option (Android system limitation)
- Live wallpapers require local file paths (no direct network URLs)
- Large images may cause memory issues on low-end devices

### iOS

- **Not supported:** iOS does not provide APIs for third-party apps to set wallpapers
- Users must manually set wallpapers through Settings > Wallpaper

## Troubleshooting

### Permission Denied Error

Make sure you've requested runtime permissions for Android 13+:

```typescript
await PermissionsAndroid.request(
  PermissionsAndroid.PERMISSIONS.READ_MEDIA_IMAGES,
);
```

### Live Wallpaper Not Working

1. Check video file exists at the path
2. Request `READ_MEDIA_VIDEO` permission
3. Ensure video is in MP4 format
4. Check video file is not corrupted

### Black Screen in Live Wallpaper

- Video file may be corrupted
- Check logcat: `adb logcat | grep LiveWallpaper`
- Ensure proper permissions are granted

### Network URL Not Working

- Check internet permission is granted
- Verify URL is accessible
- Large images may take time to download

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](./CONTRIBUTING.md) for details.
