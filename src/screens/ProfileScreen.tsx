import React, { useState } from 'react';
import { ScrollView, View, Text, StyleSheet, Switch, Alert } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../theme/colors';
import { Button } from '../components/Button';
import { triggerHaptic } from '../theme/motion';

interface ProfileScreenProps {
  onOpenLogin: () => void;
}

export const ProfileScreen: React.FC<ProfileScreenProps> = ({ onOpenLogin }) => {
  const [faceIdEnabled, setFaceIdEnabled] = useState(true);
  const [pushEnabled, setPushEnabled] = useState(true);

  const handleToggleFaceId = (val: boolean) => {
    triggerHaptic('light');
    setFaceIdEnabled(val);
  };

  const handleTogglePush = (val: boolean) => {
    triggerHaptic('light');
    setPushEnabled(val);
  };

  const handleOpenTerms = () => {
    Alert.alert(
      'Termos de Uso',
      'Termos de serviço do Clube 01 (Auto Posto 01). O acúmulo de pontos é pessoal e intransferível, válido exclusivamente nos postos da rede credenciada.'
    );
  };

  const handleOpenPrivacy = () => {
    Alert.alert(
      'Política de Privacidade',
      'Em conformidade com a LGPD e diretrizes da Apple (App Tracking Transparency), seus dados são criptografados e utilizados unicamente para identificação no clube de fidelidade.'
    );
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      {/* Header do Usuário */}
      <View style={styles.userCard}>
        <View style={styles.avatar}>
          <Text style={styles.avatarText}>CS</Text>
        </View>
        <View style={styles.userInfo}>
          <Text style={styles.userName}>Carlos Silva</Text>
          <Text style={styles.userEmail}>carlos.silva@exemplo.com</Text>
          <Text style={styles.userCpf}>CPF: ***.892.401-**</Text>
        </View>
      </View>

      {/* Ajustes e Segurança */}
      <Text style={styles.groupTitle}>Segurança e Acesso</Text>
      <View style={styles.settingsGroup}>
        <View style={styles.settingRow}>
          <View style={styles.settingIconTitle}>
            <Ionicons name="scan" size={20} color={colors.primary} style={{ marginRight: 10 }} />
            <View>
              <Text style={styles.settingLabel}>Face ID / Biometria</Text>
              <Text style={styles.settingDesc}>Acesso rápido sem digitar senha</Text>
            </View>
          </View>
          <Switch
            value={faceIdEnabled}
            onValueChange={handleToggleFaceId}
            trackColor={{ false: colors.surfaceHover, true: colors.primary }}
            thumbColor="#FFFFFF"
          />
        </View>

        <View style={styles.divider} />

        <View style={styles.settingRow}>
          <View style={styles.settingIconTitle}>
            <Ionicons name="notifications" size={20} color={colors.amber} style={{ marginRight: 10 }} />
            <View>
              <Text style={styles.settingLabel}>Notificações de Preços</Text>
              <Text style={styles.settingDesc}>Avisar quando o combustível baixar</Text>
            </View>
          </View>
          <Switch
            value={pushEnabled}
            onValueChange={handleTogglePush}
            trackColor={{ false: colors.surfaceHover, true: colors.primary }}
            thumbColor="#FFFFFF"
          />
        </View>
      </View>

      {/* Conformidade e Jurídico (App Store Requisitos) */}
      <Text style={styles.groupTitle}>Privacidade e Legal</Text>
      <View style={styles.settingsGroup}>
        <Button
          title="Termos de Uso"
          onPress={handleOpenTerms}
          variant="ghost"
          size="md"
          icon={<Ionicons name="document-text-outline" size={18} color={colors.textSecondary} />}
          style={{ justifyContent: 'flex-start' }}
        />
        <View style={styles.divider} />
        <Button
          title="Política de Privacidade (LGPD)"
          onPress={handleOpenPrivacy}
          variant="ghost"
          size="md"
          icon={<Ionicons name="shield-outline" size={18} color={colors.textSecondary} />}
          style={{ justifyContent: 'flex-start' }}
        />
      </View>

      <Button
        title="Alternar Conta / Fazer Login"
        onPress={onOpenLogin}
        variant="secondary"
        size="md"
        icon={<Ionicons name="log-out-outline" size={18} color={colors.textPrimary} />}
        style={{ marginTop: 24 }}
      />
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
  userCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.surface,
    padding: 18,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: colors.border,
    marginBottom: 20,
  },
  avatar: {
    width: 56,
    height: 56,
    borderRadius: 28,
    backgroundColor: colors.primaryMuted,
    borderWidth: 1.5,
    borderColor: colors.primary,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 14,
  },
  avatarText: {
    fontSize: 20,
    fontWeight: '800',
    color: colors.primary,
  },
  userInfo: {
    flex: 1,
  },
  userName: {
    fontSize: 17,
    fontWeight: '700',
    color: colors.textPrimary,
  },
  userEmail: {
    fontSize: 13,
    color: colors.textSecondary,
    marginTop: 2,
  },
  userCpf: {
    fontSize: 12,
    color: colors.textTertiary,
    marginTop: 2,
  },
  groupTitle: {
    fontSize: 13,
    fontWeight: '700',
    color: colors.textSecondary,
    textTransform: 'uppercase',
    letterSpacing: 0.8,
    marginBottom: 8,
    marginTop: 10,
  },
  settingsGroup: {
    backgroundColor: colors.surface,
    borderRadius: 16,
    padding: 12,
    borderWidth: 1,
    borderColor: colors.border,
    marginBottom: 16,
  },
  settingRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
    paddingHorizontal: 4,
  },
  settingIconTitle: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  settingLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: colors.textPrimary,
  },
  settingDesc: {
    fontSize: 11,
    color: colors.textSecondary,
    marginTop: 2,
  },
  divider: {
    height: 1,
    backgroundColor: colors.border,
    marginVertical: 6,
  },
});
