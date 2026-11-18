# Transporter Assignment on Lanes

Spring Boot application that optimally assigns transporters to freight lanes, minimizing total cost while respecting a maximum transporter count constraint.

## APIs

### POST /api/v1/transporters/input
Submit lanes and transporter quotes.

### POST /api/v1/transporters/assignment
Get optimized assignments.
```json
{ "maxTransporters": 3 }
```

## Algorithm

The problem is a variant of **Weighted Set Cover** (NP-hard). The implementation uses **exhaustive search with pruning** behind an `AssignmentStrategy` interface.

### Optimizations applied
1. **Feasibility check** — if any lane has zero quotes, fail fast
2. **Forced transporter detection** — lanes with a single quoter force that transporter into the solution, shrinking the search space significantly
3. **Branch and bound** — prune any branch where the cost lower bound already exceeds the current best solution

### Complexity
- Worst case: O(C(n - forced, k - forced) × lanes)
- For the sample input (7 transporters, k=3): at most C(7,3) = 35 subsets — instant

### Scaling
For large inputs (50+ transporters, k=10+), the upgrade path is:
- Replace `ExhaustiveAssignmentStrategy` with an **OR-Tools MILP** implementation
- The `AssignmentStrategy` interface makes this a drop-in replacement with zero changes to the service or controller

## Running

```bash
mvn spring-boot:run
```

App starts on `http://localhost:8080`. H2 console at `http://localhost:8080/h2-console`.
