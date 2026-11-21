# FraudX – Voice Assistant Module

## My Contribution
I developed the Voice Assistant module for FraudX to help users receive spoken alerts about potential scams and suspicious links.

## Features
- Multilingual voice support for Indian languages.
- Language selection.
- Spoken scam number warnings.
- Spoken suspicious link warnings.
- Safe link announcements.
- Test voice functionality.
- Stop voice functionality.
- Animated orb and wave visual feedback while speaking.
- Browser-based text-to-speech using SpeechSynthesis API.
- Backend integration entry point using `handleBackendAlert(type)`.

## Technologies Used
- HTML
- CSS
- JavaScript
- Web Speech API (SpeechSynthesis)

## Alert Types
- Scam number alert
- Suspicious link alert
- Safe link announcement

## Purpose
The Voice Assistant aims to make scam awareness more accessible through spoken warnings in multiple languages.

## Integration
The voice module is designed to receive alert types from the FraudX backend through the `handleBackendAlert(type)` function.
