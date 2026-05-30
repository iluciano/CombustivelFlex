package igorluciano.com.br.combustivelflex;

public class OilChangeRecord {
    public long timestamp;
    public String date;              // "DD/MM/YYYY" — data da troca
    public long km;                  // km do carro na troca
    public long nextKm;              // km alvo para próxima troca
    public String nextDate;          // "DD/MM/YYYY" — data prevista da próxima troca
    public boolean changedEngineOil;
    public boolean changedOilFilter;
    public boolean changedAirFilter;
    public boolean changedFuelFilter;
    public boolean changedCabinFilter;
    public boolean changedBrakeFluid;
    public boolean changedSparkPlugs;
    public String oilType;           // tipo do óleo (opcional)
    public String notes;             // observações (opcional, max 120 chars)

    public OilChangeRecord() {}
}
