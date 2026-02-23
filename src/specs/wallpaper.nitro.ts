import {type HybridObject} from 'react-native-nitro-modules';

/**
 * Supported wallpaper locations.
 *
 * - `home`  → Home screen only
 * - `lock`  → Lock screen only
 * - `both`  → Home and lock screens
 */
type Location = 'HOME' | 'LOCK' | 'BOTH';

export interface Wallpaper extends HybridObject<{android: 'kotlin'}> {
  /**
   * Set wallpaper from a remote URL/Base64/File path.
   *
   * @param url Image URL/Base64/File path
   * @param location Target screen(s)
   * @returns Resolves when wallpaper is successfully applied
   */
  setWallpaper(url: string, location: Location): Promise<void>;

  /**
   * Set wallpaper from a byte array.
   *
   * @param bytes Image bytes
   * @param location Target screen(s)
   * @returns Resolves when wallpaper is successfully applied
   */
  setByteWallpaper(bytes: ArrayBuffer, location: Location): Promise<void>;

  /**
   * Set Live wallpaper from a video path.
   *
   * @param videoPath Video file path
   * @returns Resolves when wallpaper is successfully applied
   */
  setLiveWallpaper(videoPath: string): Promise<void>;

  /**
   * Clear the current wallpaper and reset to default.
   *
   * @returns Resolves when wallpaper is successfully cleared
   */
  clearWallpaper(location: Location): Promise<void>;

  /**
   * Check if wallpaper setting is supported on the current platform.
   *
   * @returns `true` if supported
   */
  isSupported(): boolean;

  /**
   * Check if setting wallpaper is currently allowed.
   *
   * This may depend on OS restrictions or permissions.
   *
   * @returns `true` if allowed
   */
  isSetAllowed(): boolean;
}
