import React from 'react';
import { AnimatePresence, motion } from 'motion/react';
import { FraudXTopBar } from '../components/FraudXTopBar';
import { useFraudX } from '../context/FraudXContext';

export const DashboardScreen: React.FC = () => {
  const {
    navigate,
    protection,
    isMasterEnabled,
    armedModuleCount,
    interceptions,
    toggleMasterShield,
    openInspector,
    openFullLog,
  } = useFraudX();

  return (
    <div className="flex flex-col h-full bg-[#f8faf9]">
      <FraudXTopBar
        subtitle="Dashboard"
        isRootDashboard={true}
        onLeadingClick={() => navigate('settings')}
      />

      <div className="flex-1 min-h-0 overflow-y-auto no-scrollbar">
        <div className="px-5 pt-3 pb-10 flex flex-col">
        {/* Top Status Chip & Live Engine Telemetry */}
        <div className="flex items-center justify-between gap-2 pb-1">
          <div
            className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full border shadow-2xs transition-colors whitespace-nowrap ${
              isMasterEnabled
                ? 'bg-[#c7ebd4]/75 border-[#456554]/20 text-[#0f5328]'
                : 'bg-[#fee2e2] border-[#fecaca] text-[#991b1b]'
            }`}
          >
            <span
              className={`w-2 h-2 rounded-full shrink-0 ${
                isMasterEnabled ? 'bg-[#16a34a] animate-pulse' : 'bg-[#ef4444] animate-pulse'
              }`}
            />
            <span className="text-[10px] font-bold uppercase tracking-[0.06em]">
              {!isMasterEnabled
                ? 'Perimeter: Inactive'
                : armedModuleCount < 3
                  ? `Live Perimeter: ${armedModuleCount}/3`
                  : 'Live Perimeter: Active'}
            </span>
          </div>

          <div
            className={`inline-flex items-center gap-1 text-[11px] font-semibold whitespace-nowrap shrink-0 ${
              isMasterEnabled ? 'text-[#6d7b6c]' : 'text-[#b91c1c]'
            }`}
          >
            <span
              className={`material-symbols-outlined text-[16px] ${
                isMasterEnabled ? 'text-[#006e2f]' : 'text-[#b91c1c]'
              }`}
            >
              {isMasterEnabled ? 'verified_user' : 'gpp_maybe'}
            </span>
            <span>{isMasterEnabled ? protection.engineVersion : 'Unprotected'}</span>
          </div>
        </div>

        {/* Concentric Circular Master ON/OFF Shield Toggle */}
        <div className="flex flex-col items-center justify-center my-6">
          <div className="relative flex items-center justify-center w-56 h-56">
            {/* Soft Ambient Aura */}
            <div
              className={`absolute inset-0 rounded-full blur-2xl pointer-events-none transition-colors duration-500 ${
                isMasterEnabled ? 'bg-[#4ade80]/25' : 'bg-rose-200/60'
              }`}
            />

            {isMasterEnabled ? (
              <div className="relative z-10 w-56 h-56 rounded-full border-[10px] border-[#34d399]/40 flex items-center justify-center p-2.5 shadow-[0_6px_26px_rgba(52,211,153,0.28)] transition-all duration-300">
                <button
                  type="button"
                  onClick={() => void toggleMasterShield()}
                  aria-label="Turn off real-time protection"
                  aria-pressed={true}
                  className="w-full h-full rounded-full bg-[#1e4635] hover:bg-[#193d2e] text-white flex items-center justify-center shadow-md active:scale-95 transition-transform cursor-pointer focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-[#22c55e]/60"
                >
                  <span
                    className="text-[42px] font-extrabold tracking-tight leading-none text-white select-none"
                    style={{ fontFamily: 'var(--font-display)' }}
                  >
                    ON
                  </span>
                </button>
              </div>
            ) : (
              <div className="relative z-10 w-56 h-56 rounded-full border-[10px] border-[#fee2e2]/80 flex items-center justify-center transition-all duration-300">
                <button
                  type="button"
                  onClick={() => void toggleMasterShield()}
                  aria-label="Resume real-time protection"
                  aria-pressed={false}
                  className="w-48 h-48 rounded-full p-2.5 bg-white border-4 border-[#fca5a5] shadow-[0_8px_24px_rgba(244,63,94,0.14)] flex items-center justify-center active:scale-95 transition-transform cursor-pointer focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-[#ef4444]/40"
                >
                  <div className="w-full h-full rounded-full bg-[#fee2e2]/65 border-2 border-[#f87171] flex items-center justify-center">
                    <span
                      className="text-[36px] font-extrabold tracking-tight leading-none text-[#991b1b] select-none"
                      style={{ fontFamily: 'var(--font-display)' }}
                    >
                      OFF
                    </span>
                  </div>
                </button>
              </div>
            )}
          </div>

          <div className="flex flex-col items-center text-center mt-4">
            <h1
              className={`text-[20px] font-bold tracking-tight transition-colors ${
                isMasterEnabled ? 'text-[#14231b]' : 'text-[#991b1b]'
              }`}
              style={{ fontFamily: 'var(--font-display)' }}
            >
              {isMasterEnabled ? 'Protection enabled' : 'Protection disabled'}
            </h1>

            <p
              className={`mt-1 text-[13px] flex items-center justify-center gap-1 ${
                isMasterEnabled ? 'text-[#6d7b6c]' : 'text-[#3d4a3d] font-medium'
              }`}
            >
              {!isMasterEnabled && (
                <span className="material-symbols-outlined text-[16px] text-[#dc2626]">
                  warning
                </span>
              )}
              <span>
                {isMasterEnabled
                  ? armedModuleCount < 3
                    ? `${armedModuleCount} of 3 modules active • Tap to turn off`
                    : 'Tap to turn off'
                  : 'Tap On/Off button to resume real-time protection'}
              </span>
            </p>
          </div>
        </div>

        {/* Device Vulnerable Alert Card (Smoothly revealed when OFF) */}
        <AnimatePresence initial={false}>
          {!isMasterEnabled && (
            <motion.div
              initial={{ opacity: 0, height: 0, marginBottom: 0 }}
              animate={{ opacity: 1, height: 'auto', marginBottom: 24 }}
              exit={{ opacity: 0, height: 0, marginBottom: 0 }}
              transition={{ duration: 0.22, ease: [0.16, 1, 0.3, 1] }}
              className="overflow-hidden"
            >
              <div className="flex items-start gap-3 p-4 rounded-[24px] bg-[#fff1f2] border border-[#fecdd3] shadow-2xs">
                <span className="material-symbols-outlined text-[#e11d48] text-[24px] shrink-0 mt-0.5">
                  shield_with_heart
                </span>
                <div className="flex flex-col min-w-0 flex-1">
                  <div className="flex items-center justify-between gap-2">
                    <span className="text-[12px] font-bold text-[#9f1239] uppercase tracking-[0.06em]">
                      Device Vulnerable
                    </span>
                    <span className="px-2 py-0.5 rounded-full text-[10px] bg-[#fda4af] text-[#881337] font-bold leading-none uppercase">
                      Alert
                    </span>
                  </div>
                  <p className="text-[12.5px] leading-[18px] text-[#4c0519]/80 mt-1.5">
                    Real-time fraud heuristics, SMS smishing filters, and suspicious call detection
                    are paused.
                  </p>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Something Look Suspicious? CTA Section */}
        <section className="flex flex-col gap-2.5 mb-6">
          <h2
            className="text-[17px] font-bold text-[#14231b] tracking-tight"
            style={{ fontFamily: 'var(--font-display)' }}
          >
            Something look suspicious?
          </h2>

          <button
            type="button"
            onClick={openInspector}
            className={`w-full min-h-[58px] flex items-center justify-between px-5 py-4 rounded-full font-bold shadow-[0_2px_10px_rgba(75,120,40,0.08)] active:scale-[0.98] transition-all group cursor-pointer focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#006e2f] ${
              isMasterEnabled
                ? 'bg-[#cbf2a2] hover:bg-[#c2ed96] text-[#143021]'
                : 'bg-[#c7ebd4] hover:bg-[#b5e4c4] text-[#143d26]'
            }`}
            style={{ fontFamily: 'var(--font-display)' }}
          >
            <div className="flex items-center gap-3 min-w-0">
              <span className="material-symbols-outlined text-[23px] shrink-0 group-hover:rotate-12 transition-transform">
                link
              </span>
              <span className="truncate text-[16px] font-bold">Check a link or message</span>
            </div>
            <span className="material-symbols-outlined text-[22px] shrink-0 group-hover:translate-x-1 transition-transform">
              arrow_forward
            </span>
          </button>
        </section>

        {/* Recent Interceptions Preview */}
        <section className="flex flex-col gap-2.5">
          <div className="flex items-center justify-between">
            <h2 className="text-[14.5px] font-bold text-[#191c1c]">Recent Interceptions</h2>
            <button
              type="button"
              onClick={() => openFullLog(null)}
              className="text-[11px] font-bold uppercase tracking-[0.05em] text-[#006e2f] hover:underline py-1.5 px-1 cursor-pointer"
            >
              View full log
            </button>
          </div>

          <div className="flex flex-col gap-2.5">
            {interceptions.slice(0, 2).map((item) => {
              const isBlocked = item.status === 'blocked';
              return (
                <button
                  key={item.id}
                  type="button"
                  onClick={() => openFullLog(item.id)}
                  className="w-full text-left bg-white hover:bg-[#fcfdfc] rounded-[26px] px-4 py-3.5 flex items-center justify-between gap-3 border border-[#e6e9e8] shadow-[0_2px_8px_rgba(0,0,0,0.03)] active:scale-[0.99] transition-all cursor-pointer"
                >
                  <div className="flex items-center gap-3 min-w-0">
                    <div
                      className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 ${
                        isBlocked ? 'bg-[#fee2e2] text-[#dc2626]' : 'bg-[#c7ebd4] text-[#006e2f]'
                      }`}
                    >
                      <span className="material-symbols-outlined text-[20px]">
                        {isBlocked ? 'gpp_bad' : 'security_update_good'}
                      </span>
                    </div>
                    <div className="flex flex-col min-w-0">
                      <div className="flex items-center gap-1.5">
                        <span className="text-[13px] font-bold text-[#191c1c] truncate">
                          {item.target}
                        </span>
                        <span
                          className={`px-1.5 py-0.5 rounded-md text-[9.5px] font-bold leading-none shrink-0 ${
                            isBlocked
                              ? isMasterEnabled
                                ? 'bg-[#ba1a1a] text-white'
                                : 'bg-[#fee2e2] text-[#b91c1c]'
                              : 'bg-[#c7ebd4] text-[#2e4d3d]'
                          }`}
                        >
                          {isBlocked ? 'BLOCKED' : 'CLEARED'}
                        </span>
                      </div>
                      <span className="text-[12px] text-[#6d7b6c] truncate mt-0.5">
                        {item.summary}
                      </span>
                    </div>
                  </div>

                  <span className="text-[11px] font-semibold text-[#6d7b6c] shrink-0 whitespace-nowrap">
                    {item.relativeTime}
                  </span>
                </button>
              );
            })}
          </div>
        </section>
        </div>
      </div>
    </div>
  );
};
