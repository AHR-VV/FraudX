import React from 'react';
import { AnimatePresence, motion } from 'motion/react';
import { useFraudX } from '../context/FraudXContext';

export const FraudXToast: React.FC = () => {
  const { toastMessage, dismissToast } = useFraudX();

  return (
    <AnimatePresence>
      {toastMessage && (
        <motion.div
          initial={{ y: 20, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          exit={{ y: 16, opacity: 0 }}
          transition={{ duration: 0.18 }}
          className="absolute bottom-4 left-4 right-4 z-40 pointer-events-auto"
          role="status"
          aria-live="polite"
        >
          <div className="rounded-2xl bg-[#14231b] text-white px-4 py-3 shadow-lg flex items-center justify-between gap-3">
            <span className="text-[12.5px] font-medium leading-snug">{toastMessage}</span>
            <button
              type="button"
              onClick={dismissToast}
              aria-label="Dismiss notification"
              className="text-white/70 hover:text-white text-[11px] font-bold uppercase tracking-wider shrink-0 cursor-pointer"
            >
              OK
            </button>
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  );
};
