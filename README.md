# QuantumSim

Supports for simulating 26 quantum bits, the performance of simulation is related to the number of bits.
With each additional bit, the memory doubles and the speed is halved.

M = 4B * 2 * 2^n 

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