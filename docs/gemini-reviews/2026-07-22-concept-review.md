# Gemini review: Human Augmentation concept

Date: 2026-07-22

## Request

Review the NeoForge 1.21.1 Human Augmentation concept: body slots, surgery, data-driven DNA, optional mod integrations, Ice and Fire support, and a future Mekanism internal reactor.

## Useful recommendations

- Treat metabolism and rejection as costs rather than making augmentations ordinary equipment.
- Keep DNA definitions data-driven.
- Isolate optional integrations behind adapters.
- Persist player body data carefully across death and respawn.
- Avoid running every augmentation calculation every tick.
- Plan rendering compatibility for wings, armor, and other visible augmentations.

## Corrections made by Codex

- Gemini described polonium as reactor fuel. The planned Mekanism fission implant instead consumes Fissile Fuel; polonium and plutonium are products of nuclear-waste processing.
- Gemini recommended a legacy-style player capability. For NeoForge 1.21.1, the implementation uses a serializable, synchronized Data Attachment with copy-on-death behavior.

## Decision

Accepted the architectural recommendations after the corrections above. External-mod classes remain outside the core body model.

## Follow-up review status

An item and DNA implementation review was requested later. Gemini temporarily hit the API free-tier quota while processing it; no unverified proposal from that request has been adopted yet.
