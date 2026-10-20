import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../theme/colors';
import { Skeleton } from './Skeleton';

export interface FuelItem {
  id: string;
  name: string;
  type: 'gasolina_comum' | 'gasolina_aditivada' | 'etanol' | 'diesel';
  price: number;
  unit: string;
  updatedAt: string;
}

interface FuelPriceCardProps {
  fuel?: FuelItem;
  loading?: boolean;
}

export const FuelPriceCard: React.FC<FuelPriceCardProps> = ({ fuel, loading = false }) => {
  if (loading || !fuel) {
    return (
      <View style={styles.card}>
        <View style={styles.topRow}>
          <Skeleton width={120} height={20} borderRadius={6} />
          <Skeleton width={70} height={18} borderRadius={12} />
        </View>
        <View style={styles.priceRow}>
          <Skeleton width={140} height={32} borderRadius={8} />
          <Skeleton width={80} height={16} borderRadius={6} />
        </View>
      </View>
    );
  }

  const getFuelColor = () => {
    switch (fuel.type) {
      case 'gasolina_comum':
        return colors.fuelGasolina;
      case 'gasolina_aditivada':
        return colors.fuelAditivada;
      case 'etanol':
        return colors.fuelEtanol;
      case 'diesel':
        return colors.fuelDiesel;
      default:
        return colors.primary;
    }
  };

  const fuelColor = getFuelColor();

  return (
    <View style={styles.card}>
      <View style={styles.topRow}>
        <View style={styles.nameContainer}>
          <View style={[styles.colorPill, { backgroundColor: fuelColor }]} />
          <Text style={styles.fuelName}>{fuel.name}</Text>
        </View>
        <View style={[styles.badge, { backgroundColor: `${fuelColor}20`, borderColor: `${fuelColor}40` }]}>
          <Ionicons name="flash" size={11} color={fuelColor} style={{ marginRight: 3 }} />
          <Text style={[styles.badgeText, { color: fuelColor }]}>Preço Real</Text>
        </View>
      </View>

      <View style={styles.priceRow}>
        <View style={styles.valueGroup}>
          <Text style={styles.currencyPrefix}>R$</Text>
          <Text style={styles.priceValue}>{fuel.price.toFixed(2)}</Text>
          <Text style={styles.unitText}>/{fuel.unit}</Text>
        </View>

        <Text style={styles.updatedText}>{fuel.updatedAt}</Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: 16,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: colors.border,
  },
  topRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  nameContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  colorPill: {
    width: 10,
    height: 10,
    borderRadius: 5,
    marginRight: 8,
  },
  fuelName: {
    fontSize: 16,
    fontWeight: '700',
    color: colors.textPrimary,
  },
  badge: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 12,
    borderWidth: 1,
  },
  badgeText: {
    fontSize: 11,
    fontWeight: '700',
  },
  priceRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-end',
  },
  valueGroup: {
    flexDirection: 'row',
    alignItems: 'baseline',
  },
  currencyPrefix: {
    fontSize: 18,
    fontWeight: '600',
    color: colors.primary,
    marginRight: 4,
  },
  priceValue: {
    fontSize: 28,
    fontWeight: '800',
    color: colors.textPrimary,
    letterSpacing: -0.5,
  },
  unitText: {
    fontSize: 13,
    color: colors.textSecondary,
    marginLeft: 4,
    fontWeight: '500',
  },
  updatedText: {
    fontSize: 12,
    color: colors.textTertiary,
  },
});
