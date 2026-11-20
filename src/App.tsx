import React, { useState } from 'react';
import { AnimatePresence, motion } from 'motion/react';
import { AuditLogSheet } from './components/AuditLogSheet';
import { FraudXShieldLogo } from './components/FraudXShieldLogo';
import { FraudXToast } from './components/FraudXToast';
import { LinkInspectorSheet } from './components/LinkInspectorSheet';
import { BackendHandoffViewer, KotlinSourceViewer } from './components/StudioCodePanels';
import { FraudXProvider, useFraudX } from './context/FraudXContext';
import { AllowBlockScreen } from './screens/AllowBlockScreen';
import { DashboardScreen } from './screens/DashboardScreen';
import { InterceptionLogScreen } from './screens/InterceptionLogScreen';
import { SettingsScreen } from './screens/SettingsScreen';

type StudioTab = 'preview' | 'kotlin' | 'handoff';

const AndroidPhoneViewport: React.FC = () => {
  const { activeScreen } = useFraudX();

  return (
    <div className="relative w-full h-dvh sm:w-[430px] sm:h-[min(860px,calc(100dvh-104px))] bg-[#f8faf9] sm:rounded-[40px] sm:border-[8px] sm:border-[#18261f] sm:shadow-[0_28px_70px_-18px_rgba(19,51,36,0.28)] overflow-hidden flex flex-col">
      {/* Screen Router */}
      <div className="relative flex-1 min-h-0 overflow-hidden">
        <AnimatePresence mode="wait" initial={false}>
          <motion.div
            key={activeScreen}
            initial={{ opacity: 0, x: activeScreen === 'dashboard' ? -14 : 14 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: activeScreen === 'dashboard' ? 14 : -14 }}
            transition={{ duration: 0.18, ease: [0.16, 1, 0.3, 1] }}
            className="w-full h-full"
          >
            {activeScreen === 'dashboard' && <DashboardScreen />}
            {activeScreen === 'settings' && <SettingsScreen />}
            {activeScreen === 'allow_block' && <AllowBlockScreen />}
            {activeScreen === 'interception_log' && <InterceptionLogScreen />}
          </motion.div>
        </AnimatePresence>
      </div>

      {/* In-Phone Overlays */}
      <LinkInspectorSheet />
      <AuditLogSheet />
      <FraudXToast />
    </div>
  );
};

export default function App() {
  const [studioTab, setStudioTab] = useState<StudioTab>('preview');

  return (
    <FraudXProvider>
      <div className="min-h-dvh w-full bg-[#eef3f0] flex flex-col items-center justify-center sm:px-6 sm:py-4">
        {/* Desktop-only Studio Switcher outside the 430px Android phone frame */}
        <div className="hidden sm:flex items-center justify-between w-full max-w-[1120px] mb-3.5 px-1">
          <div className="flex items-center gap-2.5">
            <FraudXShieldLogo className="w-5 h-6" />
            <span
              className="text-[15px] font-bold tracking-tight text-[#14231b]"
              style={{ fontFamily: 'var(--font-display)' }}
            >
              Fraud<span className="text-[#15803d]">X</span> Android
            </span>
            <span className="text-[#bccbb9]" aria-hidden="true">
              /
            </span>
            <span className="text-[12.5px] font-medium text-[#4b6b59]">
              Kotlin + Jetpack Compose
            </span>
          </div>

          <nav
            aria-label="Workspace view"
            className="inline-flex items-center gap-1 p-1 rounded-full bg-white/90 border border-[#d5e0d9] shadow-2xs"
          >
            {(
              [
                { id: 'preview', label: 'Android Preview' },
                { id: 'kotlin', label: 'Kotlin Compose Source' },
                { id: 'handoff', label: 'Backend Handoff' },
              ] as const
            ).map((tab) => {
              const active = studioTab === tab.id;
              return (
                <button
                  key={tab.id}
                  type="button"
                  onClick={() => setStudioTab(tab.id)}
                  className={`px-3.5 py-1.5 rounded-full text-[12px] font-semibold whitespace-nowrap transition-all cursor-pointer ${
                    active
                      ? 'bg-[#1e4635] text-white shadow-xs'
                      : 'text-[#3d4a3d] hover:text-[#14231b]'
                  }`}
                >
                  {tab.label}
                </button>
              );
            })}
          </nav>
        </div>

        {/* Main Stage */}
        {studioTab === 'preview' && <AndroidPhoneViewport />}
        {studioTab === 'kotlin' && <KotlinSourceViewer />}
        {studioTab === 'handoff' && <BackendHandoffViewer />}
      </div>
    </FraudXProvider>
  );
}
