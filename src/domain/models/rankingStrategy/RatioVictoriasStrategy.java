package domain.models.rankingStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación de RankingOrderStrategy que ordena por ratio de victorias.
 */
public class RatioVictoriasStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    
    @Override
    public List<String> ordenarRanking(
            Map<String, List<Integer>> puntuacionesPorUsuario,
            Map<String, Integer> puntuacionMaximaPorUsuario,
            Map<String, Double> puntuacionMediaPorUsuario,
            Map<String, Integer> partidasJugadasPorUsuario,
            Map<String, Integer> victoriasUsuario) {
        
        // Calcular el ratio de victorias para cada usuario
        Map<String, Double> ratioVictorias = new HashMap<>();
        
        for (String username : partidasJugadasPorUsuario.keySet()) {
            int partidas = partidasJugadasPorUsuario.getOrDefault(username, 0);
            int victorias = victoriasUsuario.getOrDefault(username, 0);
            
            double ratio = partidas > 0 ? (double) victorias / partidas : 0.0;
            ratioVictorias.put(username, ratio);
        }
        
        return ratioVictorias.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getNombre() {
        return "Ratio de Victorias";
    }
}