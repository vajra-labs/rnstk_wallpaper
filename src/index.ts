import {NitroModules} from 'react-native-nitro-modules';
import type {Wallpaper as WallpaperSpec} from './specs/wallpaper.nitro';

const Wallpaper =
  NitroModules.createHybridObject<WallpaperSpec>('Wallpaper');

export default Wallpaper;
