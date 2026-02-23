import React from 'react';
import Wallpaper from '@rnstk/wallpaper';
import {
  Text,
  View,
  StyleSheet,
  Button,
  Alert,
  PermissionsAndroid,
  Platform,
  StatusBar,
} from 'react-native';
import {WALLPAPERS} from './consts';

function App(): React.JSX.Element {
  const [loading, setLoading] = React.useState(false);

  const setNetworkWallpaper = async () => {
    setLoading(true);
    try {
      await Wallpaper.setWallpaper(
        WALLPAPERS[(Math.random() * WALLPAPERS.length) | 0],
        'HOME',
      );
      Alert.alert('Success', 'Wallpaper set successfully!');
    } catch (error) {
      Alert.alert('Error', String(error));
    } finally {
      setLoading(false);
    }
  };

  const setBase64Wallpaper = async () => {
    setLoading(true);
    try {
      // Small red square example
      const base64 =
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8DwHwAFBQIAX8jx0gAAAABJRU5ErkJggg==';
      await Wallpaper.setWallpaper(base64, 'LOCK');
      Alert.alert('Success', 'Lock screen wallpaper set!');
    } catch (error) {
      Alert.alert('Error', String(error));
    } finally {
      setLoading(false);
    }
  };

  const setBufferWallpaper = async () => {
    setLoading(true);
    try {
      // Fetch image as ArrayBuffer
      const response = await fetch(
        WALLPAPERS[(Math.random() * WALLPAPERS.length) | 0],
      );
      const arrayBuffer = await response.arrayBuffer();
      await Wallpaper.setByteWallpaper(arrayBuffer, 'BOTH');
      Alert.alert('Success', 'Buffer wallpaper set (HOME + LOCK)!');
    } catch (error) {
      Alert.alert('Error', String(error));
    } finally {
      setLoading(false);
    }
  };

  const setLocalFileWallpaper = async () => {
    setLoading(true);
    try {
      // Request storage permission
      if (Platform.OS === 'android') {
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.READ_MEDIA_IMAGES,
        );
        if (granted !== PermissionsAndroid.RESULTS.GRANTED) {
          Alert.alert('Error', 'Storage permission denied');
          setLoading(false);
          return;
        }
      }

      // Using file path from emulator
      const filePath = 'file:///sdcard/Pictures/test_wallpaper.jpg';

      await Wallpaper.setWallpaper(filePath, 'HOME');
      Alert.alert('Success', 'Local file wallpaper set!');
    } catch (error) {
      Alert.alert('Error', String(error));
    } finally {
      setLoading(false);
    }
  };

  const checkPermissions = () => {
    const supported = Wallpaper.isSupported();
    const allowed = Wallpaper.isSetAllowed();
    Alert.alert(
      'Permissions',
      `Supported: ${supported}\nAllowed: ${allowed}`,
    );
  };

  const setLiveWallpaperExample = async () => {
    setLoading(true);
    try {
      // Request video permission
      if (Platform.OS === 'android') {
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.READ_MEDIA_VIDEO,
        );
        if (granted !== PermissionsAndroid.RESULTS.GRANTED) {
          Alert.alert('Error', 'Video permission denied');
          setLoading(false);
          return;
        }
      }

      // Video path on emulator
      const videoPath = '/sdcard/Movies/test_video.mp4';

      await Wallpaper.setLiveWallpaper(videoPath);
      Alert.alert(
        'Info',
        'Wallpaper picker opened. Select "Video Live Wallpaper" to apply.',
      );
    } catch (error) {
      Alert.alert('Error', String(error));
    } finally {
      setLoading(false);
    }
  };

  const clearWallpaper = async () => {
    setLoading(true);
    try {
      await Wallpaper.clearWallpaper('BOTH');
      Alert.alert('Success', 'Wallpaper cleared!');
    } catch (error) {
      Alert.alert('Error', String(error));
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.text}>Wallpaper Example</Text>
      <StatusBar barStyle="dark-content" />

      <View style={styles.buttonContainer}>
        <Button
          title="Set Network Wallpaper (HOME)"
          onPress={setNetworkWallpaper}
          disabled={loading}
        />
        <Button
          title="Set Base64 Wallpaper (LOCK)"
          onPress={setBase64Wallpaper}
          disabled={loading}
        />
        <Button
          title="Set Buffer Wallpaper (BOTH)"
          onPress={setBufferWallpaper}
          disabled={loading}
        />
        <Button
          title="Set Local File (content://)"
          onPress={setLocalFileWallpaper}
          disabled={loading}
        />
        <Button
          title="Set Live Wallpaper (Video)"
          onPress={setLiveWallpaperExample}
          disabled={loading}
        />
        <Button
          title="Clear Wallpaper (HOME + LOCK)"
          onPress={clearWallpaper}
          disabled={loading}
        />
        <Button title="Check Permissions" onPress={checkPermissions} />
      </View>

      {loading && <Text style={styles.loading}>Setting Wallpaper...</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#f0f0f0',
  },
  text: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 30,
  },
  buttonContainer: {
    gap: 15,
    width: '100%',
  },
  loading: {
    marginTop: 20,
    fontSize: 16,
    color: '#666',
  },
});

export default App;
