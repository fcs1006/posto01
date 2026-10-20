import React from 'react';
import { View, Text, StyleSheet, Linking, Platform } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../theme/colors';
import { Button } from './Button';
import { Skeleton } from './Skeleton';

export interface StationItem {
  id: string;
  name: string;
  address: string;
  distance: string;
  hours: string;
  services: string[];
  latitude: number;
  longitude: number;
}

interface StationCardProps {
  station?: StationItem;
  loading?: boolean;
}

export const StationCard: React.FC<StationCardProps> = ({ station, loading = false }) => {
  if (loading || !station) {
    return (
      <View style={styles.card}>
        <Skeleton width="60%" height={20} borderRadius={6} style={{ marginBottom: 8 }} />
        <Skeleton width="90%" height={14} borderRadius={4} style={{ marginBottom: 12 }} />
        <Skeleton width="40%" height={14} borderRadius={4} style={{ marginBottom: 16 }} />
        <View style={styles.buttonsRow}>
          <Skeleton width="48%" height={40} borderRadius={10} />
          <Skeleton width="48%" height={40} borderRadius={10} />
        </View>
      </View>
    );
  }

  const handleOpenWaze = () => {
    const url = `https://waze.com/ul?ll=${station.latitude},${station.longitude}&navigate=yes`;
    Linking.openURL(url).catch(() => {
      alert('Não foi possível abrir o Waze.');
    });
  };

  const handleOpenMaps = () => {
    const scheme = Platform.select({
      ios: `maps://app?daddr=${station.latitude},${station.longitude}`,
      android: `google.navigation:q=${station.latitude},${station.longitude}`,
      default: `https://maps.google.com/?q=${station.latitude},${station.longitude}`,
    });
    Linking.openURL(scheme as string).catch(() => {
      Linking.openURL(`https://maps.google.com/?q=${station.latitude},${station.longitude}`);
    });
  };

  return (
    <View style={styles.card}>
      <View style={styles.headerRow}>
        <View style={styles.titleArea}>
          <Text style={styles.name}>{station.name}</Text>
          <View style={styles.statusRow}>
            <View style={styles.statusDot} />
            <Text style={styles.hoursText}>{station.hours}</Text>
          </View>
        </View>
        <View style={styles.distanceBadge}>
          <Ionicons name="location-sharp" size={12} color={colors.primary} />
          <Text style={styles.distanceText}>{station.distance}</Text>
        </View>
      </View>

      <Text style={styles.address}>{station.address}</Text>

      {/* Tags de Serviços */}
      <View style={styles.tagsContainer}>
        {station.services.map((service, index) => (
          <View key={index} style={styles.tag}>
            <Text style={styles.tagText}>{service}</Text>
          </View>
        ))}
      </View>

      {/* Ações de Rota Direta */}
      <View style={styles.buttonsRow}>
        <View style={styles.buttonHalf}>
          <Button
            title="Waze"
            onPress={handleOpenWaze}
            variant="secondary"
            size="sm"
            icon={<Ionicons name="navigate" size={15} color={colors.secondary} />}
          />
        </View>
        <View style={styles.buttonHalf}>
          <Button
            title="Apple Maps"
            onPress={handleOpenMaps}
            variant="secondary"
            size="sm"
            icon={<Ionicons name="map" size={15} color={colors.primary} />}
          />
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: 16,
    padding: 16,
    marginBottom: 14,
    borderWidth: 1,
    borderColor: colors.border,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 6,
  },
  titleArea: {
    flex: 1,
    marginRight: 8,
  },
  name: {
    fontSize: 16,
    fontWeight: '700',
    color: colors.textPrimary,
  },
  statusRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 4,
  },
  statusDot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: colors.primary,
    marginRight: 6,
  },
  hoursText: {
    fontSize: 12,
    color: colors.textSecondary,
    fontWeight: '500',
  },
  distanceBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.primaryMuted,
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: 'rgba(16, 185, 129, 0.3)',
  },
  distanceText: {
    fontSize: 12,
    fontWeight: '700',
    color: colors.primary,
    marginLeft: 3,
  },
  address: {
    fontSize: 13,
    color: colors.textSecondary,
    marginBottom: 12,
    lineHeight: 18,
  },
  tagsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 6,
    marginBottom: 14,
  },
  tag: {
    backgroundColor: colors.surfaceElevated,
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: colors.border,
  },
  tagText: {
    fontSize: 11,
    color: colors.textTertiary,
    fontWeight: '500',
  },
  buttonsRow: {
    flexDirection: 'row',
    gap: 10,
  },
  buttonHalf: {
    flex: 1,
  },
});
