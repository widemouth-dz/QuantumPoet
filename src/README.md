# QuantumSim

Supports for simulating 28 quantum bits.

## Quantum Computing Operations

- write
- read
- not
- hadamard
- phaseShift
- conditionNot
- conditionHadamard
- conditionPhaseShift

## Sample

```kotlin
QPU(2) {
    write(0)
    hadamard(1)
    conditionNot(2, 1)
    read() // 00B or 11B
}
```