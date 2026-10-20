import React from 'react';
import { ScrollView, View, Text, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../theme/colors';
import { Button } from '../components/Button';

interface ClubScreenProps {
  onOpenQr: () => void;
}

export const ClubScreen: React.FC<ClubScreenProps> = ({ onOpenQr }) => {
  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      {/* Resumo de Nível e Saldo */}
      <View style={styles.pointsCard}>
        <Text style={styles.cardTag}>CLUBE 01 FIDELIDADE</Text>
        <Text style={styles.pointsBig}>1.250</Text>
        <Text style={styles.pointsLabel}>Pontos Acumulados</Text>

        {/* Barra de Progresso */}
        <View style={styles.progressContainer}>
          <View style={styles.progressBarBg}>
            <View style={[styles.progressBarFill, { width: '62%' }]} />
          </View>
          <View style={styles.progressLabels}>
            <Text style={styles.progressText}>Nível Ouro</Text>
            <Text style={styles.progressText}>Faltam 750 pts para Diamante</Text>
          </View>
        </View>

        <Button
          title="Bipar QR Code na Pista"
          onPress={onOpenQr}
          variant="primary"
          size="md"
          icon={<Ionicons name="qr-code" size={18} color="#0B0F19" />}
          style={{ marginTop: 14 }}
        />
      </View>

      {/* Regras de Acúmulo */}
      <View style={styles.howItWorksBox}>
        <Ionicons name="information-circle" size={18} color={colors.primary} style={{ marginRight: 8 }} />
        <Text style={styles.howItWorksText}>
          A cada 1 litro de combustível = 1 ponto. Compras na loja de conveniência Petros = 2 pontos por real.
        </Text>
      </View>

      {/* Recompensas Disponíveis */}
      <Text style={styles.sectionTitle}>Recompensas para Resgatar</Text>

      <View style={styles.rewardCard}>
        <View style={styles.rewardIcon}>
          <Ionicons name="water-outline" size={22} color={colors.primary} />
        </View>
        <View style={styles.rewardInfo}>
          <Text style={styles.rewardTitle}>Ducha Completa Grátis</Text>
          <Text style={styles.rewardCost}>500 pontos</Text>
        </View>
        <Button
          title="Resgatar"
          onPress={onOpenQr}
          variant="outline"
          size="sm"
        />
      </View>

      <View style={styles.rewardCard}>
        <View style={styles.rewardIcon}>
          <Ionicons name="cafe-outline" size={22} color={colors.amber} />
        </View>
        <View style={styles.rewardInfo}>
          <Text style={styles.rewardTitle}>Café Expresso na Loja Petros</Text>
          <Text style={styles.rewardCost}>250 pontos</Text>
        </View>
        <Button
          title="Resgatar"
          onPress={onOpenQr}
          variant="outline"
          size="sm"
        />
      </View>

      <View style={styles.rewardCard}>
        <View style={styles.rewardIcon}>
          <Ionicons name="speedometer-outline" size={22} color={colors.secondary} />
        </View>
        <View style={styles.rewardInfo}>
          <Text style={styles.rewardTitle}>R$ 20 de Desconto em Combustível</Text>
          <Text style={styles.rewardCost}>1.000 pontos</Text>
        </View>
        <Button
          title="Resgatar"
          onPress={onOpenQr}
          variant="outline"
          size="sm"
        />
      </View>
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
  pointsCard: {
    backgroundColor: colors.surface,
    borderRadius: 20,
    padding: 20,
    borderWidth: 1,
    borderColor: colors.border,
    marginBottom: 16,
  },
  cardTag: {
    fontSize: 11,
    fontWeight: '800',
    color: colors.primary,
    letterSpacing: 1,
  },
  pointsBig: {
    fontSize: 42,
    fontWeight: '800',
    color: colors.textPrimary,
    letterSpacing: -1,
    marginTop: 6,
  },
  pointsLabel: {
    fontSize: 13,
    color: colors.textSecondary,
    marginBottom: 16,
  },
  progressContainer: {
    marginBottom: 8,
  },
  progressBarBg: {
    height: 8,
    borderRadius: 4,
    backgroundColor: colors.surfaceElevated,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: colors.border,
  },
  progressBarFill: {
    height: '100%',
    backgroundColor: colors.primary,
    borderRadius: 4,
  },
  progressLabels: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 6,
  },
  progressText: {
    fontSize: 11,
    color: colors.textTertiary,
    fontWeight: '600',
  },
  howItWorksBox: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.surface,
    padding: 14,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: colors.border,
    marginBottom: 20,
  },
  howItWorksText: {
    fontSize: 12,
    color: colors.textSecondary,
    lineHeight: 18,
    flex: 1,
  },
  sectionTitle: {
    fontSize: 17,
    fontWeight: '700',
    color: colors.textPrimary,
    marginBottom: 12,
  },
  rewardCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.surface,
    padding: 14,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: colors.border,
    marginBottom: 10,
  },
  rewardIcon: {
    width: 44,
    height: 44,
    borderRadius: 12,
    backgroundColor: colors.surfaceElevated,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 12,
  },
  rewardInfo: {
    flex: 1,
  },
  rewardTitle: {
    fontSize: 14,
    fontWeight: '700',
    color: colors.textPrimary,
  },
  rewardCost: {
    fontSize: 12,
    color: colors.amber,
    fontWeight: '600',
    marginTop: 2,
  },
});
