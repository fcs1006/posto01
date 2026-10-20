import React, { useState } from 'react';
import {
  ScrollView,
  View,
  Text,
  StyleSheet,
  Pressable,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../theme/colors';
import { FuelPriceCard, FuelItem } from '../components/FuelPriceCard';
import { Button } from '../components/Button';
import { triggerHaptic } from '../theme/motion';

interface HomeScreenProps {
  onOpenQr: () => void;
  onNavigateTab: (tabKey: string) => void;
}

const INITIAL_FUELS: FuelItem[] = [
  {
    id: '1',
    name: 'Gasolina Comum',
    type: 'gasolina_comum',
    price: 5.89,
    unit: 'litro',
    updatedAt: 'Hoje às 06:30',
  },
  {
    id: '2',
    name: 'Gasolina Aditivada',
    type: 'gasolina_aditivada',
    price: 6.09,
    unit: 'litro',
    updatedAt: 'Hoje às 06:30',
  },
  {
    id: '3',
    name: 'Etanol Hidratado',
    type: 'etanol',
    price: 3.99,
    unit: 'litro',
    updatedAt: 'Hoje às 06:30',
  },
  {
    id: '4',
    name: 'Diesel S10',
    type: 'diesel',
    price: 5.79,
    unit: 'litro',
    updatedAt: 'Hoje às 06:30',
  },
];

export const HomeScreen: React.FC<HomeScreenProps> = ({ onOpenQr, onNavigateTab }) => {
  const [selectedStation, setSelectedStation] = useState('Matriz (Centro)');
  const [isLoading, setIsLoading] = useState(false);

  // Botão de teste para simular busca no Firestore e exibir Skeletons
  const handleRefreshPrices = () => {
    triggerHaptic('medium');
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
    }, 1200);
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      {/* Card Hero Clube 01 */}
      <View style={styles.heroCard}>
        <View style={styles.heroHeader}>
          <View>
            <Text style={styles.heroBadge}>CLUBE 01 • FIDELIDADE</Text>
            <Text style={styles.heroTitle}>Seu Saldo Atual</Text>
          </View>
          <View style={styles.tierPill}>
            <Ionicons name="shield-checkmark" size={13} color={colors.amber} style={{ marginRight: 4 }} />
            <Text style={styles.tierText}>Nível Ouro</Text>
          </View>
        </View>

        <View style={styles.balanceRow}>
          <Text style={styles.balanceNumber}>1.250</Text>
          <Text style={styles.balanceUnit}>pontos</Text>
        </View>

        <Text style={styles.heroSubtitle}>
          Equivalente a R$ 25,00 em descontos no próximo abastecimento.
        </Text>

        <Button
          title="Ver meu QR Code de Pontuação"
          onPress={onOpenQr}
          variant="primary"
          size="md"
          icon={<Ionicons name="qr-code-outline" size={18} color="#0B0F19" />}
          style={{ marginTop: 14 }}
        />
      </View>

      {/* Ações Rápidas em Grid 2x2 */}
      <View style={styles.quickActionsGrid}>
        <Pressable
          style={styles.actionCard}
          onPress={() => onNavigateTab('stations')}
        >
          <View style={[styles.actionIconCircle, { backgroundColor: colors.secondaryMuted }]}>
            <Ionicons name="location-sharp" size={20} color={colors.secondary} />
          </View>
          <Text style={styles.actionCardTitle}>Nossos Postos</Text>
          <Text style={styles.actionCardDesc}>3 unidades abertas</Text>
        </Pressable>

        <Pressable
          style={styles.actionCard}
          onPress={() => onNavigateTab('club')}
        >
          <View style={[styles.actionIconCircle, { backgroundColor: colors.amberMuted }]}>
            <Ionicons name="gift-outline" size={20} color={colors.amber} />
          </View>
          <Text style={styles.actionCardTitle}>Recompensas</Text>
          <Text style={styles.actionCardDesc}>Resgatar prêmios</Text>
        </Pressable>
      </View>

      {/* Seção Preços dos Combustíveis */}
      <View style={styles.sectionHeader}>
        <View>
          <Text style={styles.sectionTitle}>Combustíveis em Tempo Real</Text>
          <Text style={styles.sectionSubtitle}>Unidade: {selectedStation}</Text>
        </View>

        <Pressable onPress={handleRefreshPrices} style={styles.refreshBtn}>
          <Ionicons
            name="reload-sharp"
            size={16}
            color={isLoading ? colors.primary : colors.textSecondary}
          />
        </Pressable>
      </View>

      {/* Lista de Preços com Suporte a Skeleton */}
      {isLoading ? (
        <>
          <FuelPriceCard loading={true} />
          <FuelPriceCard loading={true} />
          <FuelPriceCard loading={true} />
          <FuelPriceCard loading={true} />
        </>
      ) : (
        INITIAL_FUELS.map((fuel) => <FuelPriceCard key={fuel.id} fuel={fuel} />)
      )}
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  content: {
    padding: 16,
    paddingBottom: 40,
  },
  heroCard: {
    backgroundColor: colors.surface,
    borderRadius: 20,
    padding: 18,
    borderWidth: 1,
    borderColor: colors.border,
    marginBottom: 16,
  },
  heroHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
  },
  heroBadge: {
    fontSize: 11,
    fontWeight: '800',
    color: colors.primary,
    letterSpacing: 1,
  },
  heroTitle: {
    fontSize: 14,
    color: colors.textSecondary,
    marginTop: 2,
  },
  tierPill: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.amberMuted,
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: 'rgba(245, 158, 11, 0.25)',
  },
  tierText: {
    fontSize: 11,
    fontWeight: '700',
    color: colors.amber,
  },
  balanceRow: {
    flexDirection: 'row',
    alignItems: 'baseline',
    marginTop: 8,
  },
  balanceNumber: {
    fontSize: 38,
    fontWeight: '800',
    color: colors.textPrimary,
    letterSpacing: -1,
  },
  balanceUnit: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.textSecondary,
    marginLeft: 6,
  },
  heroSubtitle: {
    fontSize: 13,
    color: colors.textSecondary,
    marginTop: 6,
    lineHeight: 18,
  },
  quickActionsGrid: {
    flexDirection: 'row',
    gap: 12,
    marginBottom: 20,
  },
  actionCard: {
    flex: 1,
    backgroundColor: colors.surface,
    borderRadius: 16,
    padding: 14,
    borderWidth: 1,
    borderColor: colors.border,
  },
  actionIconCircle: {
    width: 36,
    height: 36,
    borderRadius: 18,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 10,
  },
  actionCardTitle: {
    fontSize: 14,
    fontWeight: '700',
    color: colors.textPrimary,
  },
  actionCardDesc: {
    fontSize: 12,
    color: colors.textSecondary,
    marginTop: 2,
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 14,
    marginTop: 4,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: colors.textPrimary,
  },
  sectionSubtitle: {
    fontSize: 12,
    color: colors.textSecondary,
    marginTop: 2,
  },
  refreshBtn: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: colors.surfaceElevated,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 1,
    borderColor: colors.border,
  },
});
