import React from 'react';
import { ScrollView, View, Text, StyleSheet } from 'react-native';
import { colors } from '../theme/colors';
import { StationCard, StationItem } from '../components/StationCard';

const STATIONS_DATA: StationItem[] = [
  {
    id: '1',
    name: 'Auto Posto 01 - Matriz (Centro)',
    address: 'Av. Brasil, 1200 - Centro',
    distance: '1.2 km',
    hours: 'Aberto 24 Horas',
    services: ['Loja Petros', 'Troca de Óleo', 'Calibrador Digital', 'Ducha'],
    latitude: -23.55052,
    longitude: -46.633308,
  },
  {
    id: '2',
    name: 'Auto Posto 01 - Unidade Norte',
    address: 'Rodovia SP-101, Km 14 - Trevo Norte',
    distance: '4.8 km',
    hours: 'Aberto das 06:00 às 23:00',
    services: ['Diesel Rápido', 'Conveniência Petros', 'Estacionamento Seguro'],
    latitude: -23.52052,
    longitude: -46.613308,
  },
  {
    id: '3',
    name: 'Auto Posto 01 - Unidade Express',
    address: 'Rua das Indústrias, 450 - Polo Industrial',
    distance: '7.1 km',
    hours: 'Aberto das 05:00 às 00:00',
    services: ['Abastecimento Express', 'Calibrador', 'Loja Rápida'],
    latitude: -23.57052,
    longitude: -46.653308,
  },
];

export const StationsScreen: React.FC = () => {
  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <View style={styles.topInfo}>
        <Text style={styles.title}>Nossas Unidades</Text>
        <Text style={styles.subtitle}>
          Encontre o Auto Posto 01 mais perto e trace a melhor rota no Waze ou Maps.
        </Text>
      </View>

      {STATIONS_DATA.map((station) => (
        <StationCard key={station.id} station={station} />
      ))}
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
  topInfo: {
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
    marginTop: 4,
    lineHeight: 18,
  },
});
