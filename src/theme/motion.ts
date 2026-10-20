import * as Haptics from 'expo-haptics';
import { Platform } from 'react-native';

export const motion = {
  durationFast: 150,
  durationNormal: 250,
  durationSlow: 350,
  scalePress: 0.96,
};

/**
 * Dispara feedback tátil nativo (Taptic Engine no iOS / vibração no Android)
 * sem quebrar em navegadores Web.
 */
export const triggerHaptic = (style: 'light' | 'medium' | 'heavy' = 'light') => {
  if (Platform.OS === 'web') return;
  try {
    if (style === 'light') {
      Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
    } else if (style === 'medium') {
      Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium);
    } else if (style === 'heavy') {
      Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Heavy);
    }
  } catch {
    // Silencia erros se o dispositivo não tiver suporte
  }
};
