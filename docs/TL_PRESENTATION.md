# TL Presentation Notes

## Problem
Medication supply chains need to know exactly which lot was received, where it moved, whether it experienced a cold-chain excursion, how it was repackaged, and which patient ultimately received it.

## Core data model
- medication_lots: inventory and lot state
- lot_genealogy: parent -> child relationships
- patients: patient and care location
- dispensing: patient-level lot consumption
- recalls: recall event metadata

## Workflow
Receipt
  -> Available lot
  -> FEFO selection
  -> Dispensing
  -> Patient/location trace

OR

Receipt
  -> Repackaging / split
  -> Child lot
  -> More genealogy edges
  -> Dispensing
  -> Patient/location trace

Recall
  -> Root lot
  -> Traverse genealogy
  -> Find all impacted lots
  -> Find dispensing records
  -> Generate contact/action list

## FEFO
Candidates are sorted by expiry date ascending. Lots with:
- zero stock
- expired dates
- QUARANTINED status
- cold-chain excursion
are not eligible.

## Cold-chain
A flagged lot becomes QUARANTINED. This prevents it from being selected by FEFO until a quality decision is made.

## Genealogy
Every repackaging operation creates:
parentLot -> childLot

The same model supports splits. Merge operations create multiple:
sourceLot -> mergedLot
edges.

## Recall
The application uses a graph traversal. A recall of LOT-PARA-001 will also reach any descendants such as LOT-PARA-001-CHILD-001.

The final result contains:
- impacted lots
- patient count
- patient identifiers
- affected locations
- action records

## Interview questions
Q: Why FEFO instead of FIFO?
A: Expiry risk is more important than receipt age, so the earliest expiry is selected first.

Q: How do you trace a repackaged lot?
A: We store parent-child genealogy edges instead of overwriting the source lot relationship.

Q: What happens during a cold-chain excursion?
A: The lot is quarantined and excluded from normal FEFO selection.

Q: How does recall work?
A: Traverse all downstream genealogy nodes, then join the impacted lot set with dispensing records.

Q: How can this scale?
A: Index lot number, expiry, status, parent/child edges and dispensing lot references; for larger workloads, add caching/event processing and database recursive queries.
