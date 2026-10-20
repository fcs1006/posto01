import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Pressable,
} from 'react-native';
import { StatusBar } from 'expo-status-bar';
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import { colors } from './src/theme/colors';
import { triggerHaptic } from './src/theme/motion';
import { Header } from './src/components/Header';
import { ClubQrModal } from './src/components/ClubQrModal';
import { HomeScreen } from './src/screens/HomeScreen';
import { StationsScreen } from './src/screens/StationsScreen';
import { ClubScreen } from './src/screens/ClubScreen';
import { ProfileScreen } from './src/screens/ProfileScreen';
import { LoginModal } from './src/screens/LoginModal';

type TabKey = 'home' | 'stations' | 'club' | 'profile';

export default function App() {
  const [activeTab, setActiveTab] = useState<TabKey>('home');
  const [qrModalVisible, setQrModalVisible] = useState(false);
  const [loginModalVisible, setLoginModalVisible] = useState(false);
  const [userName, setUserName] = useState('Carlos Silva');

  const handleTabPress = (tab: TabKey) => {
    triggerHaptic('light');
    setActiveTab(tab);
  };

  const renderActiveScreen = () => {
    switch (activeTab) {
      case 'home':
        return (
          <HomeScreen
            onOpenQr={() => setQrModalVisible(true)}
            onNavigateTab={(tab) => setActiveTab(tab as TabKey)}
          />
        );
      case 'stations':
        return <StationsScreen />;
      case 'club':
        return <ClubScreen onOpenQr={() => setQrModalVisible(true)} />;
      case 'profile':
        return <ProfileScreen onOpenLogin={() => setLoginModalVisible(true)} />;
    }
  };

  return (
    <SafeAreaProvider>
      <SafeAreaView style={styles.container} edges={['top', 'left', 'right']}>
        <StatusBar style="light" backgroundColor={colors.background} />

        {/* Header Superior Estilo iOS */}
        <Header
          userName={userName.split(' ')[0]}
          onPressNotifications={() => alert('Você tem 2 promoções ativas hoje!')}
          unreadCount={2}
        />

        {/* Área Central: Tela Ativa */}
        <View style={styles.screenArea}>{renderActiveScreen()}</View>

        {/* Barra de Abas Inferior Nativa do iOS */}
        <View style={styles.bottomTabBar}>
          <Pressable
            style={styles.tabItem}
            onPress={() => handleTabPress('home')}
          >
            <Ionicons
              name={activeTab === 'home' ? 'flame' : 'flame-outline'}
              size={22}
              color={activeTab === 'home' ? colors.primary : colors.textTertiary}
            />
            <Text
              style={[
                styles.tabLabel,
                activeTab === 'home' && styles.tabLabelActive,
              ]}
            >
              Início
            </Text>
          </Pressable>

          <Pressable
            style={styles.tabItem}
            onPress={() => handleTabPress('stations')}
          >
            <Ionicons
              name={activeTab === 'stations' ? 'location' : 'location-outline'}
              size={22}
              color={activeTab === 'stations' ? colors.primary : colors.textTertiary}
            />
            <Text
              style={[
                styles.tabLabel,
                activeTab === 'stations' && styles.tabLabelActive,
              ]}
            >
              Postos
            </Text>
          </Pressable>

          <Pressable
            style={styles.tabItem}
            onPress={() => handleTabPress('club')}
          >
            <Ionicons
              name={activeTab === 'club' ? 'gift' : 'gift-outline'}
              size={22}
              color={activeTab === 'club' ? colors.primary : colors.textTertiary}
            />
            <Text
              style={[
                styles.tabLabel,
                activeTab === 'club' && styles.tabLabelActive,
              ]}
            >
              Clube 01
            </Text>
          </Pressable>

          <Pressable
            style={styles.tabItem}
            onPress={() => handleTabPress('profile')}
          >
            <Ionicons
              name={activeTab === 'profile' ? 'person' : 'person-outline'}
              size={22}
              color={activeTab === 'profile' ? colors.primary : colors.textTertiary}
            />
            <Text
              style={[
                styles.tabLabel,
                activeTab === 'profile' && styles.tabLabelActive,
              ]}
            >
              Perfil
            </Text>
          </Pressable>
        </View>

        {/* Modal de Identificação QR Code */}
        <ClubQrModal
          visible={qrModalVisible}
          onClose={() => setQrModalVisible(false)}
          userName={userName}
          cpf="***.892.401-**"
          points={1250}
        />

        {/* Modal de Autenticação / Troca de Conta */}
        <LoginModal
          visible={loginModalVisible}
          onClose={() => setLoginModalVisible(false)}
          onLoginSuccess={(name) => setUserName(name)}
        />
      </SafeAreaView>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  screenArea: {
    flex: 1,
  },
  bottomTabBar: {
    flexDirection: 'row',
    backgroundColor: colors.surface,
    borderTopWidth: 1,
    borderTopColor: colors.border,
    paddingTop: 8,
    paddingBottom: 16,
    justifyContent: 'space-around',
    alignItems: 'center',
  },
  tabItem: {
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
    paddingVertical: 4,
  },
  tabLabel: {
    fontSize: 11,
    fontWeight: '600',
    color: colors.textTertiary,
    marginTop: 4,
  },
  tabLabelActive: {
    color: colors.primary,
    fontWeight: '700',
  },
});
