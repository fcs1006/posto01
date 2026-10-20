import React, { useState } from 'react';
import {
  Modal,
  View,
  Text,
  TextInput,
  StyleSheet,
  Pressable,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { colors } from '../theme/colors';
import { Button } from '../components/Button';
import { triggerHaptic } from '../theme/motion';

interface LoginModalProps {
  visible: boolean;
  onClose: () => void;
  onLoginSuccess: (name: string) => void;
}

export const LoginModal: React.FC<LoginModalProps> = ({
  visible,
  onClose,
  onLoginSuccess,
}) => {
  const [email, setEmail] = useState('carlos.silva@exemplo.com');
  const [password, setPassword] = useState('******');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleStandardLogin = () => {
    triggerHaptic('medium');
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      onLoginSuccess('Carlos Silva');
      onClose();
    }, 1000);
  };

  const handleAppleLogin = () => {
    triggerHaptic('medium');
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      onLoginSuccess('Carlos (Apple ID)');
      onClose();
    }, 800);
  };

  const handleFaceIdLogin = () => {
    triggerHaptic('heavy');
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      onLoginSuccess('Carlos Silva');
      onClose();
    }, 600);
  };

  return (
    <Modal
      visible={visible}
      transparent
      animationType="slide"
      onRequestClose={onClose}
    >
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        style={styles.overlay}
      >
        <Pressable style={styles.backdropPress} onPress={onClose} />

        <View style={styles.sheetContainer}>
          <View style={styles.dragIndicator} />

          <View style={styles.header}>
            <View>
              <Text style={styles.title}>Acesse sua Conta</Text>
              <Text style={styles.subtitle}>Clube 01 • Auto Posto 01</Text>
            </View>
            <Pressable onPress={onClose} style={styles.closeIcon}>
              <Ionicons name="close" size={20} color={colors.textSecondary} />
            </Pressable>
          </View>

          {/* Input E-mail */}
          <Text style={styles.inputLabel}>E-mail ou CPF</Text>
          <View style={styles.inputContainer}>
            <Ionicons name="mail-outline" size={18} color={colors.textSecondary} style={{ marginRight: 10 }} />
            <TextInput
              style={styles.input}
              value={email}
              onChangeText={setEmail}
              placeholder="Digite seu e-mail"
              placeholderTextColor={colors.textTertiary}
              keyboardType="email-address"
              autoCapitalize="none"
            />
          </View>

          {/* Input Senha */}
          <Text style={styles.inputLabel}>Senha de Acesso</Text>
          <View style={styles.inputContainer}>
            <Ionicons name="lock-closed-outline" size={18} color={colors.textSecondary} style={{ marginRight: 10 }} />
            <TextInput
              style={styles.input}
              value={password}
              onChangeText={setPassword}
              placeholder="Digite sua senha"
              placeholderTextColor={colors.textTertiary}
              secureTextEntry={!showPassword}
            />
            <Pressable onPress={() => setShowPassword(!showPassword)}>
              <Ionicons
                name={showPassword ? 'eye-off-outline' : 'eye-outline'}
                size={18}
                color={colors.textSecondary}
              />
            </Pressable>
          </View>

          {/* Botão Entrar Primário */}
          <Button
            title="Entrar"
            onPress={handleStandardLogin}
            loading={loading}
            variant="primary"
            size="lg"
            style={{ marginTop: 8 }}
          />

          <View style={styles.dividerRow}>
            <View style={styles.dividerLine} />
            <Text style={styles.dividerText}>ou acesse com</Text>
            <View style={styles.dividerLine} />
          </View>

          {/* Botão Oficial Sign in with Apple (Mandatório para iOS) */}
          <Button
            title="Continuar com a Apple"
            onPress={handleAppleLogin}
            variant="apple"
            size="md"
            icon={<Ionicons name="logo-apple" size={18} color="#FFFFFF" />}
            style={{ marginBottom: 10 }}
          />

          {/* Botão Face ID */}
          <Button
            title="Entrar com Face ID"
            onPress={handleFaceIdLogin}
            variant="secondary"
            size="md"
            icon={<Ionicons name="scan-outline" size={18} color={colors.primary} />}
          />
        </View>
      </KeyboardAvoidingView>
    </Modal>
  );
};

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.75)',
    justifyContent: 'flex-end',
  },
  backdropPress: {
    flex: 1,
  },
  sheetContainer: {
    backgroundColor: colors.surface,
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    paddingHorizontal: 20,
    paddingTop: 12,
    paddingBottom: 36,
    borderWidth: 1,
    borderColor: colors.border,
  },
  dragIndicator: {
    width: 40,
    height: 5,
    borderRadius: 2.5,
    backgroundColor: colors.borderLight,
    alignSelf: 'center',
    marginBottom: 16,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  title: {
    fontSize: 20,
    fontWeight: '700',
    color: colors.textPrimary,
  },
  subtitle: {
    fontSize: 13,
    color: colors.textSecondary,
    marginTop: 2,
  },
  closeIcon: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: colors.surfaceElevated,
    alignItems: 'center',
    justifyContent: 'center',
  },
  inputLabel: {
    fontSize: 12,
    fontWeight: '600',
    color: colors.textSecondary,
    marginBottom: 6,
    marginTop: 10,
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.surfaceElevated,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: colors.border,
    paddingHorizontal: 14,
    height: 48,
    marginBottom: 6,
  },
  input: {
    flex: 1,
    color: colors.textPrimary,
    fontSize: 15,
  },
  dividerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 18,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: colors.border,
  },
  dividerText: {
    fontSize: 12,
    color: colors.textTertiary,
    paddingHorizontal: 10,
  },
});
