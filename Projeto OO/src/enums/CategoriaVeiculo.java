package enums;

public enum CategoriaVeiculo {
    COMUM(5.00, 1.00),
    LUXO(9.00, 2.20);

    private final double tarifaBase;
    private final double multiplicadorKm;

    CategoriaVeiculo(double tarifaBase, double multiplicadorKm) {
        this.tarifaBase = tarifaBase;
        this.multiplicadorKm = multiplicadorKm;
    }

    public double getTarifaBase() { return tarifaBase; }
    public double getMultiplicadorKm() { return multiplicadorKm; }
}