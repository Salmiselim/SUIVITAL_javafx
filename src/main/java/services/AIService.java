package services;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AIService {

    
    private final Map<String, List<String>> symptomToMedicationMap;

    public AIService() {
        symptomToMedicationMap = new HashMap<>();
        

        List<String> feverMeds = new ArrayList<>();
        feverMeds.add("Paracétamol");
        feverMeds.add("Ibuprofène");
        symptomToMedicationMap.put("Fièvre", feverMeds);

        List<String> coughMeds = new ArrayList<>();
        coughMeds.add("Sirop antitussif");
        coughMeds.add("Pastilles pour la gorge");
        symptomToMedicationMap.put("Toux", coughMeds);

        List<String> headacheMeds = new ArrayList<>();
        headacheMeds.add("Paracétamol");
        headacheMeds.add("Aspirine");
        symptomToMedicationMap.put("Maux de tête", headacheMeds);

        List<String> musclePainMeds = new ArrayList<>();
        musclePainMeds.add("Ibuprofène");
        musclePainMeds.add("Diclofénac");
        symptomToMedicationMap.put("Douleurs musculaires", musclePainMeds);

    }

    public List<String> getMedicationSuggestions(List<String> symptoms) {
        List<String> suggestions = new ArrayList<>();
        
        for (String symptom : symptoms) {
            List<String> medications = symptomToMedicationMap.get(symptom);
            if (medications != null) {
                suggestions.addAll(medications);
            }
        }
        
        // Remove duplicates
        return suggestions.stream().distinct().collect(Collectors.toList());
    }
} 