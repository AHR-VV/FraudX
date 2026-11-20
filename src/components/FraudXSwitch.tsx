import React from 'react';

interface FraudXSwitchProps {
  checked: boolean;
  onChange: (checked: boolean) => void;
  ariaLabel: string;
}

export const FraudXSwitch: React.FC<FraudXSwitchProps> = ({ checked, onChange, ariaLabel }) => {
  return (
    <button
      type="button"
      role="switch"
      aria-checked={checked}
      aria-label={ariaLabel}
      onClick={() => onChange(!checked)}
      className="min-w-[48px] min-h-[44px] inline-flex items-center justify-center shrink-0 cursor-pointer focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary rounded-full"
    >
      <span
        className={`relative inline-flex h-7 w-12 items-center rounded-full p-0.5 transition-colors duration-200 ${
          checked ? 'bg-[#22c55e]' : 'bg-[#d8dada]'
        }`}
      >
        <span
          className={`inline-flex h-6 w-6 transform items-center justify-center rounded-full bg-white shadow-md transition-transform duration-200 ${
            checked ? 'translate-x-5' : 'translate-x-0'
          }`}
        >
          <span
            className={`material-symbols-outlined text-[14px] font-bold ${
              checked ? 'text-[#006e2f]' : 'text-[#6d7b6c]'
            }`}
          >
            {checked ? 'check' : 'close'}
          </span>
        </span>
      </span>
    </button>
  );
};
