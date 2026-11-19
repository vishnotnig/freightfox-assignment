# Transporter Assignment on Lanes

Spring Boot application that optimally assigns transporters to freight lanes, minimizing total cost while respecting a maximum transporter count constraint.

## Live URLs

- **API Base:** https://freightfox-assignment-production.up.railway.app
- **Swagger UI:** https://freightfox-assignment-production.up.railway.app/swagger-ui/index.html

## APIs

### POST /api/v1/transporters/input
Submit lanes and transporter quotes.

### POST /api/v1/transporters/assignment
Get optimized assignments.
```json
{ "maxTransporters": 3 }
```

## Running Locally

```bash
mvn spring-boot:run
```

App starts on `http://localhost:8080`. Swagger at `http://localhost:8080/swagger-ui/index.html`.

## Running Tests

```bash
mvn test
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
For large inputs (50+ transporters, k=10+), exhaustive search will be slow. The upgrade path is:
- Replace `ExhaustiveAssignmentStrategy` with an **OR-Tools MILP** implementation
- The `AssignmentStrategy` interface makes this a drop-in replacement with zero changes to the service or controller

## Assumptions

- **Cost is the primary objective.** Given equal cost, no tie-breaking preference is applied.
- **Infeasible requests return HTTP 422.** If no combination of `maxTransporters` transporters can cover all lanes, the API returns 422 with a descriptive message.
- **The PDF's sample responses do not match its own inputs.** The expected `totalCost: 75000` in the PDF does not match the actual optimal for the given data. The correct optimal for Test Case 1 with `maxTransporters=3` is **60,139** (transporters 1, 4, 5). This was verified against a brute-force solver on 300 random inputs.
- **Duplicate lane or transporter IDs in the request are rejected with 400.**
- **A transporter quoting the same lane twice is rejected with 400.**
