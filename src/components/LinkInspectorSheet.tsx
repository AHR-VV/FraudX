import React, { useEffect, useRef, useState } from 'react';
import { AnimatePresence, motion } from 'motion/react';
import { useFraudX } from '../context/FraudXContext';

const QUICK_SAMPLES = [
  {
    label: 'Phishing Link',
    value: 'https://secure-banking-login.xyz/verify-account',
  },
  {
    label: 'Suspicious SMS',
    value: 'URGENT: Unpaid parcel fee, confirm now at bit.ly/3xPay',
  },
  {
    label: 'Trusted Domain',
    value: 'https://chase.com/security',
  },
];

export const LinkInspectorSheet: React.FC = () => {
  const {
    inspectorOpen,
    closeInspector,
    isScanningPayload,
    lastScanVerdict,
    inspectPayload,
    escalateVerdictToRule,
    saveVerdictToLog,
  } = useFraudX();

  const [inputValue, setInputValue] = useState('');
  const inputRef = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    if (inspectorOpen) {
      setInputValue('');
      const timer = window.setTimeout(() => inputRef.current?.focus(), 140);
      return () => window.clearTimeout(timer);
    }
  }, [inspectorOpen]);

  const handleScan = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputValue.trim() || isScanningPayload) return;
    void inspectPayload(inputValue);
  };

  return (
    <AnimatePresence>
      {inspectorOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.18 }}
          className="absolute inset-0 z-50 bg-black/40 backdrop-blur-[2px] flex flex-col justify-end p-3"
          role="dialog"
          aria-modal="true"
          aria-label="Inspect Unknown Payload"
          onClick={closeInspector}
        >
          <motion.div
            initial={{ y: 40, opacity: 0.85 }}
            animate={{ y: 0, opacity: 1 }}
            exit={{ y: 40, opacity: 0 }}
            transition={{ duration: 0.22, ease: [0.16, 1, 0.3, 1] }}
            className="w-full bg-white rounded-[26px] p-5 shadow-2xl border border-[#e6e9e8] flex flex-col gap-3.5 max-h-[85%] overflow-y-auto no-scrollbar"
            onClick={(e) => e.stopPropagation()}
          >
            {/* Grab Handle */}
            <div className="w-10 h-1 rounded-full bg-[#d8dada] mx-auto -mt-1 mb-0.5" />

            {/* Header */}
            <div className="flex items-center justify-between gap-2">
              <div className="flex items-center gap-2 min-w-0">
                <span className="material-symbols-outlined text-[#006e2f] text-[22px]">
                  find_in_page
                </span>
                <h2
                  className="text-[16px] font-bold text-[#191c1c] truncate"
                  style={{ fontFamily: 'var(--font-display)' }}
                >
                  Inspect Unknown Payload
                </h2>
              </div>
              <button
                type="button"
                onClick={closeInspector}
                aria-label="Close inspector"
                className="min-w-[36px] min-h-[36px] rounded-full bg-[#eceeed] flex items-center justify-center text-[#3d4a3d] hover:text-[#191c1c] cursor-pointer transition-colors"
              >
                <span className="material-symbols-outlined text-[18px]">close</span>
              </button>
            </div>

            <p className="text-[13.5px] leading-5 text-[#6d7b6c]">
              Paste any suspicious text message, unfamiliar URL, or sender handle to simulate
              isolation before opening.
            </p>

            {/* Quick Sample Buttons */}
            <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-0.5">
              {QUICK_SAMPLES.map((sample) => (
                <button
                  key={sample.label}
                  type="button"
                  onClick={() => setInputValue(sample.value)}
                  className="px-3 py-1.5 rounded-full bg-[#f2f4f3] hover:bg-[#e6e9e8] border border-[#e2e8e5] text-[11px] font-semibold text-[#133324] whitespace-nowrap shrink-0 cursor-pointer transition-colors"
                >
                  Try: {sample.label}
                </button>
              ))}
            </div>

            {/* Form */}
            <form onSubmit={handleScan} className="flex flex-col gap-3">
              <input
                ref={inputRef}
                type="text"
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                placeholder="https://... or paste raw text message"
                className="w-full h-12 bg-[#f2f4f3] text-[#191c1c] px-4 rounded-2xl text-[14px] placeholder:text-[#6d7b6c] border border-[#d4ded7] focus:outline-none focus:ring-2 focus:ring-[#22c55e] focus:bg-white transition-all"
              />

              <button
                type="submit"
                disabled={!inputValue.trim() || isScanningPayload}
                className="w-full h-12 rounded-2xl bg-[#006e2f] hover:bg-[#005a26] disabled:opacity-50 text-white text-[15px] font-bold flex items-center justify-center gap-2 active:scale-[0.99] transition-all cursor-pointer shadow-sm"
                style={{ fontFamily: 'var(--font-display)' }}
              >
                <span
                  className={`material-symbols-outlined text-[20px] ${
                    isScanningPayload ? 'animate-spin' : ''
                  }`}
                >
                  {isScanningPayload ? 'sync' : 'policy'}
                </span>
                <span>{isScanningPayload ? 'Analyzing target heuristics...' : 'Deep Scan'}</span>
              </button>
            </form>

            {/* Detailed Scan Verdict Card */}
            {lastScanVerdict && (
              <div
                className={`rounded-[18px] p-4 border flex flex-col gap-2.5 ${
                  lastScanVerdict.verdict === 'malicious'
                    ? 'bg-[#fff1f2] border-[#fecdd3]'
                    : lastScanVerdict.verdict === 'suspicious'
                      ? 'bg-[#fffbeb] border-[#fde68a]'
                      : 'bg-[#f1faf4] border-[#bbf7d0]'
                }`}
              >
                <div className="flex items-center justify-between gap-2">
                  <div className="flex items-center gap-1.5">
                    <span
                      className={`material-symbols-outlined text-[18px] ${
                        lastScanVerdict.verdict === 'malicious'
                          ? 'text-[#ba1a1a]'
                          : lastScanVerdict.verdict === 'suspicious'
                            ? 'text-[#b45309]'
                            : 'text-[#006e2f]'
                      }`}
                    >
                      {lastScanVerdict.verdict === 'malicious'
                        ? 'gpp_bad'
                        : lastScanVerdict.verdict === 'suspicious'
                          ? 'warning'
                          : 'check_circle'}
                    </span>
                    <span
                      className={`text-[11px] font-bold uppercase tracking-wider tabular-nums ${
                        lastScanVerdict.verdict === 'malicious'
                          ? 'text-[#ba1a1a]'
                          : lastScanVerdict.verdict === 'suspicious'
                            ? 'text-[#b45309]'
                            : 'text-[#006e2f]'
                      }`}
                      style={{ fontFamily: 'var(--font-mono)' }}
                    >
                      {lastScanVerdict.verdict === 'malicious'
                        ? 'HIGH RISK'
                        : lastScanVerdict.verdict === 'suspicious'
                          ? 'SUSPICIOUS'
                          : 'SAFE'}{' '}
                      · {lastScanVerdict.riskScore}/100
                    </span>
                  </div>
                  <span
                    className="text-[10px] font-semibold uppercase text-[#6d7b6c]"
                    style={{ fontFamily: 'var(--font-mono)' }}
                  >
                    {lastScanVerdict.detectedType === 'website'
                      ? 'WEB / URL'
                      : lastScanVerdict.detectedType === 'phone'
                        ? 'PHONE'
                        : 'SMS'}
                  </span>
                </div>

                <h3
                  className="text-[14.5px] font-bold text-[#191c1c] leading-snug"
                  style={{ fontFamily: 'var(--font-display)' }}
                >
                  {lastScanVerdict.headline}
                </h3>

                <p className="text-[12.5px] leading-relaxed text-[#3d4a3d]">
                  {lastScanVerdict.explanation}
                </p>

                <ul className="space-y-1">
                  {lastScanVerdict.signals.map((sig) => (
                    <li key={sig} className="text-[12px] text-[#3d4a3d] flex items-start gap-1.5">
                      <span aria-hidden="true" className="text-[#6d7b6c]">
                        •
                      </span>
                      <span>{sig}</span>
                    </li>
                  ))}
                </ul>

                <div className="grid grid-cols-2 gap-2 pt-1">
                  {lastScanVerdict.verdict !== 'safe' ? (
                    <>
                      <button
                        type="button"
                        onClick={() => void escalateVerdictToRule(lastScanVerdict, 'blocked')}
                        className="h-10 rounded-xl bg-[#ba1a1a] hover:bg-[#991b1b] text-white text-[12px] font-bold cursor-pointer transition-colors"
                      >
                        Block Target
                      </button>
                      <button
                        type="button"
                        onClick={() => void saveVerdictToLog(lastScanVerdict)}
                        className="h-10 rounded-xl bg-white hover:bg-[#f2f4f3] border border-[#d8dada] text-[#191c1c] text-[12px] font-semibold cursor-pointer transition-colors"
                      >
                        Save to Log
                      </button>
                    </>
                  ) : (
                    <>
                      <button
                        type="button"
                        onClick={() => void escalateVerdictToRule(lastScanVerdict, 'allowed')}
                        className="h-10 rounded-xl bg-[#006e2f] hover:bg-[#005a26] text-white text-[12px] font-bold cursor-pointer transition-colors"
                      >
                        Add to Allowlist
                      </button>
                      <button
                        type="button"
                        onClick={() => void saveVerdictToLog(lastScanVerdict)}
                        className="h-10 rounded-xl bg-white hover:bg-[#f2f4f3] border border-[#d8dada] text-[#191c1c] text-[12px] font-semibold cursor-pointer transition-colors"
                      >
                        Log Cleared
                      </button>
                    </>
                  )}
                </div>
              </div>
            )}
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
};
