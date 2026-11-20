import React from 'react';
import { FraudXShieldLogo } from './FraudXShieldLogo';

interface FraudXTopBarProps {
  subtitle: string;
  isRootDashboard: boolean;
  onLeadingClick: () => void;
  subtitleAccentGreen?: boolean;
}

export const FraudXTopBar: React.FC<FraudXTopBarProps> = ({
  subtitle,
  isRootDashboard,
  onLeadingClick,
  subtitleAccentGreen = false,
}) => {
  return (
    <header className="sticky top-0 z-30 w-full bg-[#f8faf9]/95 backdrop-blur-xl border-b border-[#e2e8e5]/90 shrink-0">
      <div className="h-16 px-3 flex items-center justify-between">
        <div className="flex items-center gap-1.5">
          <button
            type="button"
            onClick={onLeadingClick}
            aria-label={isRootDashboard ? 'Open Settings' : 'Go back'}
            className="min-w-[44px] min-h-[44px] rounded-full flex items-center justify-center text-[#133324] hover:bg-surface-container active:scale-95 transition-all cursor-pointer focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
          >
            <span className="material-symbols-outlined text-[24px]">
              {isRootDashboard ? 'settings' : 'arrow_back'}
            </span>
          </button>

          <div className="flex items-center gap-2">
            <FraudXShieldLogo className="w-[23px] h-[27px] shrink-0" />
            <div className="flex flex-col leading-none">
              <span
                className="text-[18px] font-bold tracking-tight text-[#14231b]"
                style={{ fontFamily: 'var(--font-display)' }}
              >
                Fraud<span className="text-[#15803d]">X</span>
              </span>
              <span
                className={`text-[10px] font-bold mt-0.5 ${
                  isRootDashboard
                    ? 'uppercase tracking-[0.14em] text-[#4a5c52]'
                    : subtitleAccentGreen
                      ? 'tracking-[0.04em] text-[#006e2f]'
                      : 'tracking-[0.04em] text-outline'
                }`}
              >
                {subtitle}
              </span>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};
