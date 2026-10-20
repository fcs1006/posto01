import React from 'react';
import {
  Modal,
  View,
  Text,
  StyleSheet,
  Pressable,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import Svg, { Rect } from 'react-native-svg';
import { colors } from '../theme/colors';
import { Button } from './Button';
import { triggerHaptic } from '../theme/motion';

interface ClubQrModalProps {
  visible: boolean;
  onClose: () => void;
  userName?: string;
  cpf?: string;
  points?: number;
}

export const ClubQrModal: React.FC<ClubQrModalProps> = ({
  visible,
  onClose,
  userName = 'Carlos Silva',
  cpf = '***.892.401-**',
  points = 1250,
}) => {
  const handleClose = () => {
    triggerHaptic('light');
    onClose();
  };

  // Matriz visual estilizada de alta definição representando o QR Code de pontuação do cliente
  const renderQrMatrix = () => {
    const size = 180;
    const cells = 15;
    const cellSize = size / cells;

    // Padrão determinístico para simular QR Code de alta densidade
    const grid: boolean[][] = [];
    for (let r = 0; r < cells; r++) {
      grid[r] = [];
      for (let c = 0; c < cells; c++) {
        // Padrões de canto (Finder patterns)
        const isTopLeft = r < 5 && c < 5;
        const isTopRight = r < 5 && c >= cells - 5;
        const isBottomLeft = r >= cells - 5 && c < 5;

        if (isTopLeft || isTopRight || isBottomLeft) {
          const border =
            (r === 0 || r === 4 || c === 0 || c === 4) ||
            (r === 0 || r === 4 || c === cells - 1 || c === cells - 5) ||
            (r === cells - 1 || r === cells - 5 || c === 0 || c === 4);
          const center =
            (r === 2 && c === 2) ||
            (r === 2 && c === cells - 3) ||
            (r === cells - 3 && c === 2);
          grid[r][c] = border || center;
        } else {
          // Padrão de dados pseudo-aleatório baseado nas coordenadas
          grid[r][c] = ((r * 7 + c * 13 + (r + c) * 3) % 4) < 2;
        }
      }
    }

    return (
      <Svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
        <Rect x="0" y="0" width={size} height={size} fill="#FFFFFF" />
        {grid.map((row, r) =>
          row.map((active, c) =>
            active ? (
              <Rect
                key={`${r}-${c}`}
                x={c * cellSize}
                y={r * cellSize}
                width={cellSize}
                height={cellSize}
                fill="#0B0F19"
              />
            ) : null
          )
        )}
      </Svg>
    );
  };

  return (
    <Modal
      visible={visible}
      transparent
      animationType="slide"
      onRequestClose={handleClose}
    >
      <View style={styles.overlay}>
        <Pressable style={styles.backdropPress} onPress={handleClose} />

        <View style={styles.sheetContainer}>
          {/* iOS Sheet Drag Indicator */}
          <View style={styles.dragIndicator} />

          <View style={styles.header}>
            <View>
              <Text style={styles.title}>Cartão Clube 01</Text>
              <Text style={styles.subtitle}>Identificação para Pontuação</Text>
            </View>
            <Pressable onPress={handleClose} style={styles.closeIcon}>
              <Ionicons name="close" size={20} color={colors.textSecondary} />
            </Pressable>
          </View>

          {/* Cartão de Fidelidade Estilo Apple Wallet */}
          <View style={styles.cardContainer}>
            <View style={styles.cardHeader}>
              <View>
                <Text style={styles.clientLabel}>CLIENTE VIP</Text>
                <Text style={styles.clientName}>{userName}</Text>
                <Text style={styles.cpfText}>CPF: {cpf}</Text>
              </View>
              <View style={styles.pointsBadge}>
                <Ionicons name="sparkles" size={12} color={colors.amber} style={{ marginRight: 4 }} />
                <Text style={styles.pointsValue}>{points} pts</Text>
              </View>
            </View>

            {/* Caixa do QR Code em Alto Contraste */}
            <View style={styles.qrWrapper}>
              {renderQrMatrix()}
              <Text style={styles.qrCodeNumber}>ID: 01-9842-CLUBE</Text>
            </View>

            <View style={styles.instructionBox}>
              <Ionicons name="scan-outline" size={16} color={colors.primary} style={{ marginRight: 6 }} />
              <Text style={styles.instructionText}>
                Apresente na pista antes de abastecer para pontuar.
              </Text>
            </View>
          </View>

          <Button
            title="Concluir e Fechar"
            onPress={handleClose}
            variant="secondary"
            size="md"
            style={{ marginTop: 16 }}
          />
        </View>
      </View>
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
  cardContainer: {
    backgroundColor: colors.surfaceElevated,
    borderRadius: 18,
    padding: 18,
    borderWidth: 1,
    borderColor: colors.border,
    alignItems: 'center',
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    width: '100%',
    marginBottom: 16,
  },
  clientLabel: {
    fontSize: 10,
    fontWeight: '700',
    color: colors.primary,
    letterSpacing: 1,
  },
  clientName: {
    fontSize: 17,
    fontWeight: '700',
    color: colors.textPrimary,
    marginTop: 2,
  },
  cpfText: {
    fontSize: 12,
    color: colors.textSecondary,
    marginTop: 1,
  },
  pointsBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.amberMuted,
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: 'rgba(245, 158, 11, 0.3)',
  },
  pointsValue: {
    fontSize: 13,
    fontWeight: '700',
    color: colors.amber,
  },
  qrWrapper: {
    backgroundColor: '#FFFFFF',
    padding: 14,
    borderRadius: 16,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 4,
  },
  qrCodeNumber: {
    fontSize: 11,
    fontWeight: '700',
    color: '#334155',
    marginTop: 8,
    letterSpacing: 1,
  },
  instructionBox: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.primaryMuted,
    borderRadius: 10,
    paddingHorizontal: 12,
    paddingVertical: 8,
    marginTop: 16,
    borderWidth: 1,
    borderColor: 'rgba(16, 185, 129, 0.25)',
  },
  instructionText: {
    fontSize: 12,
    color: colors.primary,
    fontWeight: '600',
    flexShrink: 1,
  },
});
