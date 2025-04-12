package domain.models.rankingStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación de RankingOrderStrategy que ordena por puntuación máxima.
 */
public class MaximaScoreStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    
    @Override
    public List<String> ordenarRanking(
            Map<String, List<Integer>> puntuacionesPorUsuario,
            Map<String, Integer> puntuacionMaximaPorUsuario,
            Map<String, Double> puntuacionMediaPorUsuario,
            Map<String, Integer> partidasJugadasPorUsuario,
            Map<String, Integer> victoriasUsuario) {
        
        return puntuacionMaximaPorUsuario.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getNombre() {
        return "Puntuación Máxima";
    }
}